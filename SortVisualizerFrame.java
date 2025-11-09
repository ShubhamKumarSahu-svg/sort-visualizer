import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/**
 * Main frame for the Sort Visualizer application with comparison mode.
 */
public class SortVisualizerFrame extends JFrame {
  // UI Components
  private BarPanel barPanel;
  private BarPanel barPanel1; // For comparison mode
  private BarPanel barPanel2; // For comparison mode
  private JPanel visualizationPanel;
  private CardLayout vizCardLayout;

  private final JComboBox<String> algoCombo;
  private final JComboBox<String> algoCombo1; // For comparison
  private final JComboBox<String> algoCombo2; // For comparison
  private final JSlider sizeSlider;
  private final JSlider speedSlider;
  private final JCheckBox soundToggle;
  private final JButton startBtn;
  private final JButton stopBtn;
  private final JToggleButton compareToggle;
  private final JLabel sizeLabel;
  private final JLabel speedLabel;
  private final JLabel algoDescLabel;
  private final JLabel comparisonResultLabel;

  // Sorting state
  private volatile Thread workerThread;
  private volatile Thread workerThread1;
  private volatile Thread workerThread2;
  private volatile Thread timerThread;
  private volatile Thread timerThread1;
  private volatile Thread timerThread2;
  private final AtomicBoolean running = new AtomicBoolean(false);
  private final AtomicBoolean stopRequested = new AtomicBoolean(false);
  private volatile long startTime = 0;
  private volatile long startTime1 = 0;
  private volatile long startTime2 = 0;
  private volatile boolean algo1Finished = false;
  private volatile boolean algo2Finished = false;

  // Sound generator
  private final ToneGenerator toneGenerator;

  // Modern color scheme
  private static final Color BG_COLOR = new Color(241, 245, 249);
  private static final Color PANEL_BG = new Color(255, 255, 255);
  private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
  private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
  private static final Color BORDER_COLOR = new Color(226, 232, 240);

  // Algorithm descriptions
  private static final String[] ALGORITHMS = {
      "Bubble Sort", "Selection Sort", "Insertion Sort",
      "Merge Sort", "Quick Sort", "Heap Sort",
      "Shell Sort", "Cocktail Sort", "Comb Sort",
      "Gnome Sort", "Radix Sort", "Counting Sort"
  };

  private static final String[] DESCRIPTIONS = {
      "O(n²) - Repeatedly swaps adjacent elements if they're in wrong order",
      "O(n²) - Finds minimum element and places it at the beginning",
      "O(n²) - Builds sorted array one item at a time",
      "O(n log n) - Divides array and merges sorted halves",
      "O(n log n) avg - Picks pivot and partitions around it",
      "O(n log n) - Builds max heap and extracts elements",
      "O(n log n) - Insertion sort with decreasing gaps",
      "O(n²) - Bubble sort variant that goes both directions",
      "O(n²/2ᵖ) - Bubble sort with decreasing gaps",
      "O(n²) - Similar to insertion sort with position tracking",
      "O(nk) - Non-comparative, sorts by individual digits",
      "O(n+k) - Counts occurrences of each value"
  };

  public SortVisualizerFrame() {
    super("Sort Visualizer Pro");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setLayout(new BorderLayout(0, 0));
    setMinimumSize(new Dimension(1150, 750));

    this.toneGenerator = new ToneGenerator();

    // Initialize panels
    this.barPanel = new BarPanel();
    this.barPanel1 = new BarPanel();
    this.barPanel2 = new BarPanel();

    barPanel1.setComparisonMode(true, "Algorithm 1");
    barPanel2.setComparisonMode(true, "Algorithm 2");

    // Initialize controls
    algoCombo = createStyledComboBox();
    algoCombo1 = createStyledComboBox();
    algoCombo2 = createStyledComboBox();
    algoCombo2.setSelectedIndex(3); // Default to Merge Sort for second

    sizeSlider = createStyledSlider(10, 400, 100);
    speedSlider = createStyledSlider(1, 100, 60);
    soundToggle = createStyledCheckBox("🔊 Sound", true);

    startBtn = createStyledButton("▶ Start", new Color(16, 185, 129), new Color(5, 150, 105));
    stopBtn = createStyledButton("⏹ Stop", new Color(239, 68, 68), new Color(220, 38, 38));
    stopBtn.setEnabled(false);

    compareToggle = new JToggleButton("⚖ Compare Mode");
    styleToggleButton(compareToggle);

    sizeLabel = new JLabel("100");
    speedLabel = new JLabel("60");
    algoDescLabel = new JLabel(DESCRIPTIONS[0]);
    comparisonResultLabel = new JLabel("");

    styleValueLabel(sizeLabel);
    styleValueLabel(speedLabel);
    algoDescLabel.setFont(new Font("Inter", Font.PLAIN, 12));
    algoDescLabel.setForeground(TEXT_SECONDARY);

    comparisonResultLabel.setFont(new Font("Inter", Font.BOLD, 14));
    comparisonResultLabel.setForeground(new Color(59, 130, 246));
    comparisonResultLabel.setHorizontalAlignment(SwingConstants.CENTER);

    // Build UI
    setupUI();
    setupListeners();

    // Generate initial data
    generateData();
  }

  private JComboBox<String> createStyledComboBox() {
    JComboBox<String> combo = new JComboBox<>(ALGORITHMS);
    combo.setFont(new Font("Inter", Font.PLAIN, 13));
    combo.setBackground(PANEL_BG);
    combo.setForeground(TEXT_PRIMARY);
    combo.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 1),
        BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    return combo;
  }

  private JSlider createStyledSlider(int min, int max, int value) {
    JSlider slider = new JSlider(min, max, value);
    slider.setBackground(PANEL_BG);
    slider.setForeground(new Color(59, 130, 246));
    return slider;
  }

  private JCheckBox createStyledCheckBox(String text, boolean selected) {
    JCheckBox checkBox = new JCheckBox(text, selected);
    checkBox.setFont(new Font("Inter", Font.PLAIN, 13));
    checkBox.setBackground(PANEL_BG);
    checkBox.setForeground(TEXT_PRIMARY);
    checkBox.setFocusPainted(false);
    return checkBox;
  }

  private void styleToggleButton(JToggleButton button) {
    button.setFont(new Font("Inter", Font.BOLD, 13));
    button.setBackground(new Color(59, 130, 246));
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setOpaque(true);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(150, 38));
    button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
  }

  private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
    JButton button = new JButton(text);
    button.setFont(new Font("Inter", Font.BOLD, 13));
    button.setBackground(bgColor);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setOpaque(true);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(150, 38));
    button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

    button.addMouseListener(new java.awt.event.MouseAdapter() {
      private final Color originalColor = bgColor;
      private final Color hover = hoverColor;

      public void mouseEntered(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          button.setBackground(hover);
        }
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        button.setBackground(originalColor);
      }
    });

    return button;
  }

  private void styleValueLabel(JLabel label) {
    label.setFont(new Font("Inter", Font.BOLD, 12));
    label.setForeground(new Color(59, 130, 246));
    label.setPreferredSize(new Dimension(40, 20));
  }

  private void setupUI() {
    JPanel mainContainer = new JPanel(new BorderLayout(0, 0));
    mainContainer.setBackground(BG_COLOR);

    // Top control panel
    JPanel topPanel = new JPanel(new BorderLayout(0, 0));
    topPanel.setBackground(BG_COLOR);
    topPanel.setBorder(new EmptyBorder(20, 20, 15, 20));

    JPanel controlCard = new JPanel(new BorderLayout(15, 15));
    controlCard.setBackground(PANEL_BG);
    controlCard.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 1),
        new EmptyBorder(20, 20, 20, 20)));

    // Algorithm selection - changes based on mode
    JPanel algoSelectionPanel = new JPanel(new CardLayout());
    algoSelectionPanel.setBackground(PANEL_BG);

    // Single mode panel
    JPanel singleAlgoPanel = new JPanel(new BorderLayout(8, 8));
    singleAlgoPanel.setBackground(PANEL_BG);
    JLabel algoLabel = new JLabel("Algorithm");
    algoLabel.setFont(new Font("Inter", Font.BOLD, 13));
    algoLabel.setForeground(TEXT_PRIMARY);
    singleAlgoPanel.add(algoLabel, BorderLayout.NORTH);
    singleAlgoPanel.add(algoCombo, BorderLayout.CENTER);
    singleAlgoPanel.add(algoDescLabel, BorderLayout.SOUTH);

    // Comparison mode panel
    JPanel compAlgoPanel = new JPanel(new GridLayout(2, 1, 0, 15));
    compAlgoPanel.setBackground(PANEL_BG);

    JPanel algo1Panel = new JPanel(new BorderLayout(8, 5));
    algo1Panel.setBackground(PANEL_BG);
    JLabel algo1Label = new JLabel("Algorithm 1");
    algo1Label.setFont(new Font("Inter", Font.BOLD, 12));
    algo1Label.setForeground(TEXT_PRIMARY);
    algo1Panel.add(algo1Label, BorderLayout.NORTH);
    algo1Panel.add(algoCombo1, BorderLayout.CENTER);

    JPanel algo2Panel = new JPanel(new BorderLayout(8, 5));
    algo2Panel.setBackground(PANEL_BG);
    JLabel algo2Label = new JLabel("Algorithm 2");
    algo2Label.setFont(new Font("Inter", Font.BOLD, 12));
    algo2Label.setForeground(TEXT_PRIMARY);
    algo2Panel.add(algo2Label, BorderLayout.NORTH);
    algo2Panel.add(algoCombo2, BorderLayout.CENTER);

    compAlgoPanel.add(algo1Panel);
    compAlgoPanel.add(algo2Panel);

    algoSelectionPanel.add(singleAlgoPanel, "single");
    algoSelectionPanel.add(compAlgoPanel, "compare");

    // Slider panel
    JPanel sliderPanel = new JPanel(new GridLayout(2, 1, 0, 15));
    sliderPanel.setBackground(PANEL_BG);

    JPanel sizePanel = createSliderPanel("Array Size", sizeSlider, sizeLabel);
    JPanel speedPanel = createSliderPanel("Speed", speedSlider, speedLabel);

    sliderPanel.add(sizePanel);
    sliderPanel.add(speedPanel);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    buttonPanel.setBackground(PANEL_BG);
    buttonPanel.add(compareToggle);
    buttonPanel.add(soundToggle);
    buttonPanel.add(startBtn);
    buttonPanel.add(stopBtn);

    // Combine sections
    JPanel leftSection = new JPanel(new BorderLayout(0, 15));
    leftSection.setBackground(PANEL_BG);
    leftSection.add(algoSelectionPanel, BorderLayout.CENTER);
    leftSection.add(buttonPanel, BorderLayout.SOUTH);

    JPanel contentGrid = new JPanel(new GridLayout(1, 2, 20, 0));
    contentGrid.setBackground(PANEL_BG);
    contentGrid.add(leftSection);
    contentGrid.add(sliderPanel);

    controlCard.add(contentGrid, BorderLayout.CENTER);
    topPanel.add(controlCard, BorderLayout.CENTER);

    // Visualization panel with CardLayout
    JPanel vizContainer = new JPanel(new BorderLayout());
    vizContainer.setBackground(BG_COLOR);
    vizContainer.setBorder(new EmptyBorder(0, 20, 20, 20));

    vizCardLayout = new CardLayout();
    visualizationPanel = new JPanel(vizCardLayout);
    visualizationPanel.setBackground(BG_COLOR);

    // Single visualization
    JPanel singleVizCard = new JPanel(new BorderLayout());
    singleVizCard.setBackground(PANEL_BG);
    singleVizCard.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 1),
        new EmptyBorder(2, 2, 2, 2)));
    singleVizCard.add(barPanel, BorderLayout.CENTER);

    // Comparison visualization
    JPanel compVizCard = new JPanel(new BorderLayout(0, 10));
    compVizCard.setBackground(BG_COLOR);

    JPanel compGrid = new JPanel(new GridLayout(1, 2, 15, 0));
    compGrid.setBackground(BG_COLOR);

    JPanel viz1Card = new JPanel(new BorderLayout());
    viz1Card.setBackground(PANEL_BG);
    viz1Card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 1),
        new EmptyBorder(2, 2, 2, 2)));
    viz1Card.add(barPanel1, BorderLayout.CENTER);

    JPanel viz2Card = new JPanel(new BorderLayout());
    viz2Card.setBackground(PANEL_BG);
    viz2Card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 1),
        new EmptyBorder(2, 2, 2, 2)));
    viz2Card.add(barPanel2, BorderLayout.CENTER);

    compGrid.add(viz1Card);
    compGrid.add(viz2Card);

    // Result panel
    JPanel resultPanel = new JPanel(new BorderLayout());
    resultPanel.setBackground(PANEL_BG);
    resultPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 1),
        new EmptyBorder(15, 20, 15, 20)));
    resultPanel.add(comparisonResultLabel, BorderLayout.CENTER);
    resultPanel.setPreferredSize(new Dimension(0, 60));

    compVizCard.add(compGrid, BorderLayout.CENTER);
    compVizCard.add(resultPanel, BorderLayout.SOUTH);

    visualizationPanel.add(singleVizCard, "single");
    visualizationPanel.add(compVizCard, "compare");

    vizContainer.add(visualizationPanel, BorderLayout.CENTER);

    mainContainer.add(topPanel, BorderLayout.NORTH);
    mainContainer.add(vizContainer, BorderLayout.CENTER);

    add(mainContainer, BorderLayout.CENTER);
  }

  private JPanel createSliderPanel(String title, JSlider slider, JLabel valueLabel) {
    JPanel panel = new JPanel(new BorderLayout(10, 8));
    panel.setBackground(PANEL_BG);

    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(PANEL_BG);

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Inter", Font.PLAIN, 13));
    titleLabel.setForeground(TEXT_PRIMARY);

    headerPanel.add(titleLabel, BorderLayout.WEST);
    headerPanel.add(valueLabel, BorderLayout.EAST);

    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(slider, BorderLayout.CENTER);

    return panel;
  }

  private void setupListeners() {
    startBtn.addActionListener(e -> startSorting());
    stopBtn.addActionListener(e -> requestStop());

    compareToggle.addActionListener(e -> {
      if (running.get()) {
        compareToggle.setSelected(!compareToggle.isSelected());
        JOptionPane.showMessageDialog(this,
            "Please stop the current sorting before changing mode.",
            "Cannot Change Mode", JOptionPane.WARNING_MESSAGE);
        return;
      }

      boolean isCompare = compareToggle.isSelected();
      CardLayout algoLayout = (CardLayout) ((JPanel) algoCombo.getParent().getParent()).getLayout();
      algoLayout.show((JPanel) algoCombo.getParent().getParent(), isCompare ? "compare" : "single");
      vizCardLayout.show(visualizationPanel, isCompare ? "compare" : "single");

      if (isCompare) {
        compareToggle.setBackground(new Color(139, 92, 246));
        compareToggle.setText("⚖ Compare Mode ✓");
      } else {
        compareToggle.setBackground(new Color(59, 130, 246));
        compareToggle.setText("⚖ Compare Mode");
      }

      generateData();
    });

    algoCombo.addActionListener(e -> {
      int index = algoCombo.getSelectedIndex();
      algoDescLabel.setText(DESCRIPTIONS[index]);
      barPanel.setAlgorithmName(ALGORITHMS[index]);
    });

    algoCombo1.addActionListener(e -> {
      barPanel1.setAlgorithmName(ALGORITHMS[algoCombo1.getSelectedIndex()]);
    });

    algoCombo2.addActionListener(e -> {
      barPanel2.setAlgorithmName(ALGORITHMS[algoCombo2.getSelectedIndex()]);
    });

    sizeSlider.addChangeListener(e -> {
      int value = sizeSlider.getValue();
      sizeLabel.setText(String.valueOf(value));
      if (!sizeSlider.getValueIsAdjusting() && !running.get()) {
        generateData();
      }
    });

    speedSlider.addChangeListener(e -> {
      int value = speedSlider.getValue();
      speedLabel.setText(String.valueOf(value));
    });
  }

  private void generateData() {
    if (running.get())
      return;

    int n = sizeSlider.getValue();
    int[] arr = new int[n];
    Random rng = new Random();
    for (int i = 0; i < n; i++) {
      arr[i] = 5 + rng.nextInt(995);
    }

    if (compareToggle.isSelected()) {
      barPanel1.setValues(Arrays.copyOf(arr, arr.length));
      barPanel2.setValues(Arrays.copyOf(arr, arr.length));
      comparisonResultLabel.setText("");
    } else {
      barPanel.setValues(arr);
    }
  }

  private void startSorting() {
    if (running.get())
      return;

    running.set(true);
    stopRequested.set(false);
    toneGenerator.reset();
    setControlsEnabled(false);

    if (compareToggle.isSelected()) {
      startComparisonSort();
    } else {
      startSingleSort();
    }
  }

  private void startSingleSort() {
    if (barPanel.getValues() == null || barPanel.getValues().length == 0)
      return;

    startTime = System.currentTimeMillis();
    int[] arr = Arrays.copyOf(barPanel.getValues(), barPanel.getValues().length);
    String algo = (String) algoCombo.getSelectedItem();
    int speed = speedSlider.getValue();

    barPanel.setSortingState(true);

    SortingAlgorithms sorter = new SortingAlgorithms(
        arr, this, null, stopRequested, soundToggle.isSelected(), toneGenerator, false);

    timerThread = createTimerThread(barPanel, startTime);
    timerThread.start();

    workerThread = new Thread(() -> {
      try {
        runAlgorithm(sorter, algo);
        if (!stopRequested.get()) {
          SwingUtilities.invokeLater(() -> barPanel.setSortedState());
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        SwingUtilities.invokeLater(this::resetUiAfterRun);
      }
    }, "sort-worker");

    barPanel.attachLiveArray(arr, speed);
    workerThread.start();
  }

  private void startComparisonSort() {
    if (barPanel1.getValues() == null || barPanel1.getValues().length == 0)
      return;

    algo1Finished = false;
    algo2Finished = false;
    startTime1 = System.currentTimeMillis();
    startTime2 = System.currentTimeMillis();

    int[] arr1 = Arrays.copyOf(barPanel1.getValues(), barPanel1.getValues().length);
    int[] arr2 = Arrays.copyOf(barPanel2.getValues(), barPanel2.getValues().length);
    String algo1 = (String) algoCombo1.getSelectedItem();
    String algo2 = (String) algoCombo2.getSelectedItem();
    int speed = speedSlider.getValue();

    barPanel1.setSortingState(true);
    barPanel2.setSortingState(true);
    comparisonResultLabel.setText("Both algorithms are running...");
    comparisonResultLabel.setForeground(new Color(251, 191, 36));

    SortingAlgorithms sorter1 = new SortingAlgorithms(
        arr1, null, this, stopRequested, false, toneGenerator, true);
    SortingAlgorithms sorter2 = new SortingAlgorithms(
        arr2, null, this, stopRequested, false, toneGenerator, true);

    timerThread1 = createTimerThread(barPanel1, startTime1);
    timerThread2 = createTimerThread(barPanel2, startTime2);
    timerThread1.start();
    timerThread2.start();

    workerThread1 = new Thread(() -> {
      try {
        runAlgorithm(sorter1, algo1);
        algo1Finished = true;
        if (!stopRequested.get()) {
          SwingUtilities.invokeLater(() -> {
            barPanel1.setSortedState();
            checkComparisonComplete();
          });
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, "sort-worker-1");

    workerThread2 = new Thread(() -> {
      try {
        runAlgorithm(sorter2, algo2);
        algo2Finished = true;
        if (!stopRequested.get()) {
          SwingUtilities.invokeLater(() -> {
            barPanel2.setSortedState();
            checkComparisonComplete();
          });
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, "sort-worker-2");

    barPanel1.attachLiveArray(arr1, speed);
    barPanel2.attachLiveArray(arr2, speed);
    workerThread1.start();
    workerThread2.start();
  }

  private void checkComparisonComplete() {
    if (algo1Finished && algo2Finished) {
      long time1 = barPanel1.getElapsedSeconds();
      long time2 = barPanel2.getElapsedSeconds();
      long comp1 = barPanel1.getComparisons();
      long comp2 = barPanel2.getComparisons();
      long swap1 = barPanel1.getSwaps();
      long swap2 = barPanel2.getSwaps();

      String algo1Name = (String) algoCombo1.getSelectedItem();
      String algo2Name = (String) algoCombo2.getSelectedItem();

      StringBuilder result = new StringBuilder("Comparison Complete! ");

      if (time1 < time2) {
        result.append(String.format("🏆 %s was faster (%ds vs %ds)", algo1Name, time1, time2));
      } else if (time2 < time1) {
        result.append(String.format("🏆 %s was faster (%ds vs %ds)", algo2Name, time2, time1));
      } else {
        result.append("⚖ Both took the same time!");
      }

      result.append(String.format(" | Comparisons: %d vs %d | Swaps: %d vs %d", comp1, comp2, swap1, swap2));

      comparisonResultLabel.setText(result.toString());
      comparisonResultLabel.setForeground(new Color(16, 185, 129));

      resetUiAfterRun();
    }
  }

  private Thread createTimerThread(BarPanel panel, long startTime) {
    return new Thread(() -> {
      while (running.get() && !stopRequested.get()) {
        try {
          Thread.sleep(1000);
          long elapsed = (System.currentTimeMillis() - startTime) / 1000;
          SwingUtilities.invokeLater(() -> panel.setElapsedTime(elapsed));
        } catch (InterruptedException e) {
          break;
        }
      }
    }, "timer-thread");
  }

  private void runAlgorithm(SortingAlgorithms sorter, String algo) {
    switch (algo) {
      case "Bubble Sort" -> sorter.bubbleSort();
      case "Selection Sort" -> sorter.selectionSort();
      case "Insertion Sort" -> sorter.insertionSort();
      case "Merge Sort" -> sorter.mergeSort();
      case "Quick Sort" -> sorter.quickSort();
      case "Heap Sort" -> sorter.heapSort();
      case "Shell Sort" -> sorter.shellSort();
      case "Cocktail Sort" -> sorter.cocktailSort();
      case "Comb Sort" -> sorter.combSort();
      case "Gnome Sort" -> sorter.gnomeSort();
      case "Radix Sort" -> sorter.radixSort();
      case "Counting Sort" -> sorter.countingSort();
    }
  }

  private void requestStop() {
    stopRequested.set(true);
    stopBtn.setEnabled(false);
    toneGenerator.stopAllSounds();
    if (timerThread != null)
      timerThread.interrupt();
    if (timerThread1 != null)
      timerThread1.interrupt();
    if (timerThread2 != null)
      timerThread2.interrupt();
  }

  private void resetUiAfterRun() {
    running.set(false);
    toneGenerator.stopAllSounds();
    toneGenerator.reset();
    setControlsEnabled(true);
    if (!compareToggle.isSelected()) {
      barPanel.setSortingState(false);
    } else {
      barPanel1.setSortingState(false);
      barPanel2.setSortingState(false);
    }
  }

  private void setControlsEnabled(boolean enabled) {
    startBtn.setEnabled(enabled);
    stopBtn.setEnabled(!enabled);
    compareToggle.setEnabled(enabled);
    algoCombo.setEnabled(enabled);
    algoCombo1.setEnabled(enabled);
    algoCombo2.setEnabled(enabled);
    sizeSlider.setEnabled(enabled);
  }

  // Methods for single mode
  public void highlight(int i, int j) {
    SwingUtilities.invokeLater(() -> barPanel.setHighlights(i, j));
  }

  public void clearHighlights() {
    SwingUtilities.invokeLater(() -> barPanel.clearHighlights());
  }

  public void repaintBars() {
    SwingUtilities.invokeLater(() -> barPanel.repaint());
  }

  public void incrementComparisons() {
    SwingUtilities.invokeLater(() -> barPanel.incrementComparisons());
  }

  public void incrementSwaps() {
    SwingUtilities.invokeLater(() -> barPanel.incrementSwaps());
  }

  // Methods for comparison mode - panel 1
  public void highlight1(int i, int j) {
    SwingUtilities.invokeLater(() -> barPanel1.setHighlights(i, j));
  }

  public void clearHighlights1() {
    SwingUtilities.invokeLater(() -> barPanel1.clearHighlights());
  }

  public void repaintBars1() {
    SwingUtilities.invokeLater(() -> barPanel1.repaint());
  }

  public void incrementComparisons1() {
    SwingUtilities.invokeLater(() -> barPanel1.incrementComparisons());
  }

  public void incrementSwaps1() {
    SwingUtilities.invokeLater(() -> barPanel1.incrementSwaps());
  }

  // Methods for comparison mode - panel 2
  public void highlight2(int i, int j) {
    SwingUtilities.invokeLater(() -> barPanel2.setHighlights(i, j));
  }

  public void clearHighlights2() {
    SwingUtilities.invokeLater(() -> barPanel2.clearHighlights());
  }

  public void repaintBars2() {
    SwingUtilities.invokeLater(() -> barPanel2.repaint());
  }

  public void incrementComparisons2() {
    SwingUtilities.invokeLater(() -> barPanel2.incrementComparisons());
  }

  public void incrementSwaps2() {
    SwingUtilities.invokeLater(() -> barPanel2.incrementSwaps());
  }

  public void stepDelay() {
    int sp = speedSlider.getValue();
    int delay = Math.max(1, 120 - sp);
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
