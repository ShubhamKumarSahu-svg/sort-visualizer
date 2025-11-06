import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main frame for the Sort Visualizer application.
 */
public class SortVisualizerFrame extends JFrame {
  // UI Components
  private final BarPanel barPanel;
  private final JComboBox<String> algoCombo;
  private final JSlider sizeSlider;
  private final JSlider speedSlider;
  private final JCheckBox soundToggle;
  private final JTextField valuesField;
  private final JButton generateBtn;
  private final JButton shuffleBtn;
  private final JButton startBtn;
  private final JButton stopBtn;
  private final JLabel sizeLabel;
  private final JLabel speedLabel;
  private final JLabel algoDescLabel;

  // Sorting state
  private volatile Thread workerThread;
  private final AtomicBoolean running = new AtomicBoolean(false);
  private final AtomicBoolean stopRequested = new AtomicBoolean(false);

  // Sound generator
  private final ToneGenerator toneGenerator;

  // Algorithm descriptions
  private static final String[] ALGORITHMS = {
      "Bubble Sort", "Selection Sort", "Insertion Sort",
      "Merge Sort", "Quick Sort", "Heap Sort",
      "Shell Sort", "Cocktail Sort", "Comb Sort",
      "Gnome Sort", "Radix Sort", "Counting Sort",
      "Bogo Sort"
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
      "O(n²/2^p) - Bubble sort with decreasing gaps",
      "O(n²) - Similar to insertion sort with position tracking",
      "O(nk) - Non-comparative, sorts by individual digits",
      "O(n+k) - Counts occurrences of each value",
      "O(∞) - Randomly shuffles until sorted (for fun!)"
  };

  public SortVisualizerFrame() {
    super("🎨 Sort Visualizer Pro");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setLayout(new BorderLayout(0, 0));
    setMinimumSize(new Dimension(1100, 700));

    this.toneGenerator = new ToneGenerator();
    this.barPanel = new BarPanel();

    // Initialize controls with modern styling
    algoCombo = createStyledComboBox();
    sizeSlider = createStyledSlider(10, 400, 100);
    speedSlider = createStyledSlider(1, 100, 60);
    soundToggle = createStyledCheckBox("🔊 Sound", true);

    valuesField = new JTextField(30);
    valuesField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    valuesField.setToolTipText("Enter comma-separated integers (e.g., 5,3,8,1,9)");

    generateBtn = createStyledButton("Generate Random", new Color(52, 152, 219));
    shuffleBtn = createStyledButton("Shuffle", new Color(155, 89, 182));
    startBtn = createStyledButton("▶ Start", new Color(46, 204, 113));
    stopBtn = createStyledButton("⏹ Stop", new Color(231, 76, 60));
    stopBtn.setEnabled(false);

    sizeLabel = new JLabel("Bars: 100");
    speedLabel = new JLabel("Speed: 60");
    algoDescLabel = new JLabel(DESCRIPTIONS[0]);

    styleLabel(sizeLabel);
    styleLabel(speedLabel);
    algoDescLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
    algoDescLabel.setForeground(new Color(149, 165, 166));

    // Build UI
    setupUI();
    setupListeners();

    // Generate initial data
    onGenerate(null);
  }

  private JComboBox<String> createStyledComboBox() {
    JComboBox<String> combo = new JComboBox<>(ALGORITHMS);
    combo.setFont(new Font("Segoe UI", Font.BOLD, 13));
    combo.setBackground(Color.WHITE);
    combo.setForeground(new Color(44, 62, 80));
    return combo;
  }

  private JSlider createStyledSlider(int min, int max, int value) {
    JSlider slider = new JSlider(min, max, value);
    slider.setBackground(new Color(236, 240, 241));
    slider.setForeground(new Color(52, 152, 219));
    return slider;
  }

  private JCheckBox createStyledCheckBox(String text, boolean selected) {
    JCheckBox checkBox = new JCheckBox(text, selected);
    checkBox.setFont(new Font("Segoe UI", Font.BOLD, 13));
    checkBox.setBackground(new Color(236, 240, 241));
    checkBox.setForeground(new Color(44, 62, 80));
    checkBox.setFocusPainted(false);
    return checkBox;
  }

  private JButton createStyledButton(String text, Color bgColor) {
    JButton button = new JButton(text);
    button.setFont(new Font("Segoe UI", Font.BOLD, 13));
    button.setBackground(bgColor);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setOpaque(true);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(120, 35));

    // Hover effect
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          button.setBackground(bgColor.brighter());
        }
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        button.setBackground(bgColor);
      }
    });

    return button;
  }

  private void styleLabel(JLabel label) {
    label.setFont(new Font("Segoe UI", Font.BOLD, 13));
    label.setForeground(new Color(44, 62, 80));
  }

  private void setupUI() {
    // Main container with modern background
    JPanel mainContainer = new JPanel(new BorderLayout(0, 0));
    mainContainer.setBackground(new Color(236, 240, 241));

    // Top control panel
    JPanel topPanel = new JPanel(new BorderLayout(10, 10));
    topPanel.setBackground(new Color(236, 240, 241));
    topPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

    // Algorithm selection section
    JPanel algoPanel = new JPanel(new BorderLayout(10, 5));
    algoPanel.setBackground(new Color(236, 240, 241));

    JLabel algoLabel = new JLabel("Select Algorithm:");
    styleLabel(algoLabel);
    algoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

    JPanel algoComboPanel = new JPanel(new BorderLayout());
    algoComboPanel.setBackground(new Color(236, 240, 241));
    algoComboPanel.add(algoCombo, BorderLayout.CENTER);

    algoPanel.add(algoLabel, BorderLayout.NORTH);
    algoPanel.add(algoComboPanel, BorderLayout.CENTER);
    algoPanel.add(algoDescLabel, BorderLayout.SOUTH);

    // Slider panel
    JPanel sliderPanel = new JPanel(new GridLayout(2, 1, 5, 10));
    sliderPanel.setBackground(new Color(236, 240, 241));

    JPanel sizePanel = createSliderPanel("Array Size", sizeSlider, sizeLabel);
    JPanel speedPanel = createSliderPanel("Animation Speed", speedSlider, speedLabel);

    sliderPanel.add(sizePanel);
    sliderPanel.add(speedPanel);

    // Input and controls panel
    JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    inputPanel.setBackground(new Color(236, 240, 241));

    JLabel valuesLabel = new JLabel("Custom Values:");
    styleLabel(valuesLabel);

    inputPanel.add(valuesLabel);
    inputPanel.add(valuesField);
    inputPanel.add(soundToggle);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    buttonPanel.setBackground(new Color(236, 240, 241));
    buttonPanel.add(generateBtn);
    buttonPanel.add(shuffleBtn);
    buttonPanel.add(startBtn);
    buttonPanel.add(stopBtn);

    // Combine top sections
    JPanel topContent = new JPanel(new GridLayout(4, 1, 5, 10));
    topContent.setBackground(new Color(236, 240, 241));
    topContent.add(algoPanel);
    topContent.add(sliderPanel);
    topContent.add(inputPanel);
    topContent.add(buttonPanel);

    topPanel.add(topContent, BorderLayout.CENTER);

    // Visualization panel with modern card style
    JPanel vizContainer = new JPanel(new BorderLayout());
    vizContainer.setBackground(new Color(236, 240, 241));
    vizContainer.setBorder(new EmptyBorder(0, 15, 15, 15));

    JPanel vizCard = new JPanel(new BorderLayout());
    vizCard.setBackground(Color.WHITE);
    vizCard.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
        new EmptyBorder(10, 10, 10, 10)));
    vizCard.add(barPanel, BorderLayout.CENTER);

    vizContainer.add(vizCard, BorderLayout.CENTER);

    mainContainer.add(topPanel, BorderLayout.NORTH);
    mainContainer.add(vizContainer, BorderLayout.CENTER);

    add(mainContainer, BorderLayout.CENTER);
  }

  private JPanel createSliderPanel(String title, JSlider slider, JLabel valueLabel) {
    JPanel panel = new JPanel(new BorderLayout(10, 5));
    panel.setBackground(new Color(236, 240, 241));

    JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    labelPanel.setBackground(new Color(236, 240, 241));
    JLabel titleLabel = new JLabel(title);
    styleLabel(titleLabel);
    labelPanel.add(titleLabel);
    labelPanel.add(Box.createHorizontalStrut(10));
    labelPanel.add(valueLabel);

    panel.add(labelPanel, BorderLayout.NORTH);
    panel.add(slider, BorderLayout.CENTER);

    return panel;
  }

  private void setupListeners() {
    generateBtn.addActionListener(this::onGenerate);
    shuffleBtn.addActionListener(e -> onShuffle());
    startBtn.addActionListener(e -> startSorting());
    stopBtn.addActionListener(e -> requestStop());

    algoCombo.addActionListener(e -> {
      int index = algoCombo.getSelectedIndex();
      algoDescLabel.setText(DESCRIPTIONS[index]);
      barPanel.setAlgorithmName(ALGORITHMS[index]);
    });

    sizeSlider.addChangeListener(e -> {
      int value = sizeSlider.getValue();
      sizeLabel.setText("Bars: " + value);
      if (!sizeSlider.getValueIsAdjusting() && !running.get()) {
        onGenerate(null);
      }
    });

    speedSlider.addChangeListener(e -> {
      int value = speedSlider.getValue();
      speedLabel.setText("Speed: " + value);
    });
  }

  private void onGenerate(ActionEvent e) {
    if (running.get())
      return;

    int[] dataFromField = parseValues(valuesField.getText().trim());
    final int n = dataFromField != null ? dataFromField.length : sizeSlider.getValue();
    int[] arr = new int[n];

    if (dataFromField != null) {
      arr = dataFromField;
    } else {
      Random rng = new Random();
      for (int i = 0; i < n; i++) {
        arr[i] = 5 + rng.nextInt(995);
      }
    }
    barPanel.setValues(arr);
  }

  private void onShuffle() {
    if (barPanel.getValues() == null || barPanel.getValues().length == 0) {
      onGenerate(null);
    } else {
      int[] values = barPanel.getValues();
      shuffleArray(values);
      barPanel.setValues(values);
    }
  }

  private int[] parseValues(String text) {
    if (text == null || text.isEmpty())
      return null;

    try {
      String[] parts = text.split(",");
      int[] vals = new int[parts.length];
      for (int i = 0; i < parts.length; i++) {
        vals[i] = Integer.parseInt(parts[i].trim());
      }
      return vals;
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
          "Invalid input. Please enter comma-separated integers.",
          "Parse Error", JOptionPane.WARNING_MESSAGE);
      return null;
    }
  }

  private void startSorting() {
    if (barPanel.getValues() == null || barPanel.getValues().length == 0)
      return;
    if (running.get())
      return;

    running.set(true);
    stopRequested.set(false);
    toneGenerator.reset();
    setControlsEnabled(false);

    int[] arr = Arrays.copyOf(barPanel.getValues(), barPanel.getValues().length);
    String algo = (String) algoCombo.getSelectedItem();
    int speed = speedSlider.getValue();

    SortingAlgorithms sorter = new SortingAlgorithms(
        arr, this, stopRequested, soundToggle.isSelected(), toneGenerator);

    workerThread = new Thread(() -> {
      try {
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
          case "Bogo Sort" -> sorter.bogoSort();
        }
        SwingUtilities.invokeLater(() -> barPanel.setSortedState());
      } catch (Exception e) {
        e.printStackTrace();
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
            "Error during sorting: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE));
      } finally {
        SwingUtilities.invokeLater(this::resetUiAfterRun);
      }
    }, "sort-worker");

    barPanel.attachLiveArray(arr, speed);
    workerThread.start();
  }

  private void requestStop() {
    stopRequested.set(true);
    stopBtn.setEnabled(false);
    toneGenerator.stopAllSounds();
  }

  private void resetUiAfterRun() {
    running.set(false);
    toneGenerator.stopAllSounds();
    toneGenerator.reset();
    setControlsEnabled(true);
  }

  private void setControlsEnabled(boolean enabled) {
    startBtn.setEnabled(enabled);
    stopBtn.setEnabled(!enabled);
    generateBtn.setEnabled(enabled);
    shuffleBtn.setEnabled(enabled);
    valuesField.setEnabled(enabled);
    algoCombo.setEnabled(enabled);
    sizeSlider.setEnabled(enabled);
  }

  private static void shuffleArray(int[] arr) {
    Random r = new Random();
    for (int i = arr.length - 1; i > 0; i--) {
      int j = r.nextInt(i + 1);
      int t = arr[i];
      arr[i] = arr[j];
      arr[j] = t;
    }
  }

  public void highlight(int i, int j) {
    SwingUtilities.invokeLater(() -> barPanel.setHighlights(i, j));
  }

  public void clearHighlights() {
    SwingUtilities.invokeLater(() -> barPanel.clearHighlights());
  }

  public void repaintBars() {
    SwingUtilities.invokeLater(() -> barPanel.repaint());
  }

  public void stepDelay() {
    int sp = speedSlider.getValue();
    int delay = Math.max(0, 12 - (sp / 9));
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
