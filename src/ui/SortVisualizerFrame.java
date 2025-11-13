package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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

import audio.ToneGenerator;
import constants.AlgorithmConstants;
import constants.UIConstants;
import model.SortingState;
import ui.components.BarPanel;
import ui.components.UIComponentFactory;
import ui.controllers.SortController;
import util.ArrayGenerator;

public class SortVisualizerFrame extends JFrame {
  private BarPanel barPanel;
  private BarPanel barPanel1;
  private BarPanel barPanel2;
  private JPanel visualizationPanel;
  private CardLayout vizCardLayout;

  private final JComboBox<String> algoCombo;
  private final JComboBox<String> algoCombo1;
  private final JComboBox<String> algoCombo2;
  private final JSlider sizeSlider;
  private final JSlider speedSlider;
  private final JCheckBox soundToggle;
  private final JButton startBtn;
  private final JButton stopBtn;
  private final JToggleButton compareToggle;
  private final JButton backToSingleBtn;
  private final JLabel sizeLabel;
  private final JLabel speedLabel;
  private final JLabel algoDescLabel;
  private final JLabel comparisonResultLabel;

  private final AtomicBoolean running = new AtomicBoolean(false);
  private final SortingState singleState = new SortingState();
  private final SortingState state1 = new SortingState();
  private final SortingState state2 = new SortingState();

  private final ToneGenerator toneGenerator;
  private SortController sortController;

  public SortVisualizerFrame() {
    super("Sort Visualizer Pro");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setLayout(new BorderLayout(0, 0));
    setMinimumSize(new Dimension(UIConstants.MIN_WIDTH, UIConstants.MIN_HEIGHT));

    this.toneGenerator = new ToneGenerator();

    this.barPanel = new BarPanel();
    this.barPanel1 = new BarPanel();
    this.barPanel2 = new BarPanel();

    barPanel1.setComparisonMode(true, "Algorithm 1");
    barPanel2.setComparisonMode(true, "Algorithm 2");

    algoCombo = UIComponentFactory.createStyledComboBox(AlgorithmConstants.ALGORITHMS);
    algoCombo1 = UIComponentFactory.createStyledComboBox(AlgorithmConstants.ALGORITHMS);
    algoCombo2 = UIComponentFactory.createStyledComboBox(AlgorithmConstants.ALGORITHMS);
    algoCombo2.setSelectedIndex(3);

    sizeSlider = UIComponentFactory.createStyledSlider(10, 400, 100);
    speedSlider = UIComponentFactory.createStyledSlider(1, 100, 60);
    soundToggle = UIComponentFactory.createStyledCheckBox("🔊 Sound", true);

    startBtn = UIComponentFactory.createStyledButton("▶ Start",
        UIConstants.START_BTN_COLOR, UIConstants.START_BTN_HOVER);
    stopBtn = UIComponentFactory.createStyledButton("⏹ Stop",
        UIConstants.STOP_BTN_COLOR, UIConstants.STOP_BTN_HOVER);
    stopBtn.setEnabled(false);

    compareToggle = UIComponentFactory.createStyledToggleButton("⚖ Compare Mode");
    backToSingleBtn = UIComponentFactory.createStyledButton("← Single Mode",
        UIConstants.BACK_BTN_COLOR, UIConstants.BACK_BTN_HOVER);
    backToSingleBtn.setVisible(false);

    sizeLabel = UIComponentFactory.createValueLabel();
    sizeLabel.setText("100");
    speedLabel = UIComponentFactory.createValueLabel();
    speedLabel.setText("60");

    algoDescLabel = new JLabel(AlgorithmConstants.DESCRIPTIONS[0]);
    algoDescLabel.setFont(new Font("Inter", Font.PLAIN, 12));
    algoDescLabel.setForeground(UIConstants.TEXT_SECONDARY);

    comparisonResultLabel = new JLabel("");
    comparisonResultLabel.setFont(new Font("Inter", Font.BOLD, 14));
    comparisonResultLabel.setForeground(new Color(59, 130, 246));
    comparisonResultLabel.setHorizontalAlignment(SwingConstants.CENTER);

    sortController = new SortController(this);

    setupUI();
    setupListeners();
    generateData();
  }

  private void setupUI() {
    JPanel mainContainer = new JPanel(new BorderLayout(0, 0));
    mainContainer.setBackground(UIConstants.BG_COLOR);

    JPanel topPanel = createTopPanel();
    JPanel vizContainer = createVisualizationContainer();

    mainContainer.add(topPanel, BorderLayout.NORTH);
    mainContainer.add(vizContainer, BorderLayout.CENTER);

    add(mainContainer, BorderLayout.CENTER);
  }

  private JPanel createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout(0, 0));
    topPanel.setBackground(UIConstants.BG_COLOR);
    topPanel.setBorder(new EmptyBorder(20, 20, 15, 20));

    JPanel controlCard = new JPanel(new BorderLayout(15, 15));
    controlCard.setBackground(UIConstants.PANEL_BG);
    controlCard.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
        new EmptyBorder(20, 20, 20, 20)));

    JPanel algoSelectionPanel = createAlgorithmSelectionPanel();
    JPanel sliderPanel = createSliderPanel();
    JPanel buttonPanel = createButtonPanel();

    JPanel leftSection = new JPanel(new BorderLayout(0, 15));
    leftSection.setBackground(UIConstants.PANEL_BG);
    leftSection.add(algoSelectionPanel, BorderLayout.CENTER);
    leftSection.add(buttonPanel, BorderLayout.SOUTH);

    JPanel contentGrid = new JPanel(new GridLayout(1, 2, 20, 0));
    contentGrid.setBackground(UIConstants.PANEL_BG);
    contentGrid.add(leftSection);
    contentGrid.add(sliderPanel);

    controlCard.add(contentGrid, BorderLayout.CENTER);
    topPanel.add(controlCard, BorderLayout.CENTER);

    return topPanel;
  }

  private JPanel createAlgorithmSelectionPanel() {
    JPanel algoSelectionPanel = new JPanel(new CardLayout());
    algoSelectionPanel.setBackground(UIConstants.PANEL_BG);

    JPanel singleAlgoPanel = createSingleAlgorithmPanel();
    JPanel compAlgoPanel = createComparisonAlgorithmPanel();

    algoSelectionPanel.add(singleAlgoPanel, "single");
    algoSelectionPanel.add(compAlgoPanel, "compare");

    return algoSelectionPanel;
  }

  private JPanel createSingleAlgorithmPanel() {
    JPanel singleAlgoPanel = new JPanel(new BorderLayout(8, 8));
    singleAlgoPanel.setBackground(UIConstants.PANEL_BG);

    JLabel algoLabel = new JLabel("Algorithm");
    algoLabel.setFont(UIConstants.FONT_BOLD);
    algoLabel.setForeground(UIConstants.TEXT_PRIMARY);

    singleAlgoPanel.add(algoLabel, BorderLayout.NORTH);
    singleAlgoPanel.add(algoCombo, BorderLayout.CENTER);
    singleAlgoPanel.add(algoDescLabel, BorderLayout.SOUTH);

    return singleAlgoPanel;
  }

  private JPanel createComparisonAlgorithmPanel() {
    JPanel compAlgoPanel = new JPanel(new GridLayout(2, 1, 0, 15));
    compAlgoPanel.setBackground(UIConstants.PANEL_BG);

    JPanel algo1Panel = new JPanel(new BorderLayout(8, 5));
    algo1Panel.setBackground(UIConstants.PANEL_BG);
    JLabel algo1Label = new JLabel("Algorithm 1");
    algo1Label.setFont(new Font("Inter", Font.BOLD, 12));
    algo1Label.setForeground(UIConstants.TEXT_PRIMARY);
    algo1Panel.add(algo1Label, BorderLayout.NORTH);
    algo1Panel.add(algoCombo1, BorderLayout.CENTER);

    JPanel algo2Panel = new JPanel(new BorderLayout(8, 5));
    algo2Panel.setBackground(UIConstants.PANEL_BG);
    JLabel algo2Label = new JLabel("Algorithm 2");
    algo2Label.setFont(new Font("Inter", Font.BOLD, 12));
    algo2Label.setForeground(UIConstants.TEXT_PRIMARY);
    algo2Panel.add(algo2Label, BorderLayout.NORTH);
    algo2Panel.add(algoCombo2, BorderLayout.CENTER);

    compAlgoPanel.add(algo1Panel);
    compAlgoPanel.add(algo2Panel);

    return compAlgoPanel;
  }

  private JPanel createSliderPanel() {
    JPanel sliderPanel = new JPanel(new GridLayout(2, 1, 0, 15));
    sliderPanel.setBackground(UIConstants.PANEL_BG);

    JPanel sizePanel = UIComponentFactory.createSliderPanel("Array Size", sizeSlider, sizeLabel);
    JPanel speedPanel = UIComponentFactory.createSliderPanel("Speed", speedSlider, speedLabel);

    sliderPanel.add(sizePanel);
    sliderPanel.add(speedPanel);

    return sliderPanel;
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    buttonPanel.setBackground(UIConstants.PANEL_BG);
    buttonPanel.add(compareToggle);
    buttonPanel.add(backToSingleBtn);
    buttonPanel.add(soundToggle);
    buttonPanel.add(startBtn);
    buttonPanel.add(stopBtn);
    stopBtn.setVisible(true);

    return buttonPanel;
  }

  private JPanel createVisualizationContainer() {
    JPanel vizContainer = new JPanel(new BorderLayout());
    vizContainer.setBackground(UIConstants.BG_COLOR);
    vizContainer.setBorder(new EmptyBorder(0, 20, 20, 20));

    vizCardLayout = new CardLayout();
    visualizationPanel = new JPanel(vizCardLayout);
    visualizationPanel.setBackground(UIConstants.BG_COLOR);

    JPanel singleVizCard = createSingleVisualizationCard();
    JPanel compVizCard = createComparisonVisualizationCard();

    visualizationPanel.add(singleVizCard, "single");
    visualizationPanel.add(compVizCard, "compare");

    vizContainer.add(visualizationPanel, BorderLayout.CENTER);

    return vizContainer;
  }

  private JPanel createSingleVisualizationCard() {
    JPanel singleVizCard = new JPanel(new BorderLayout());
    singleVizCard.setBackground(UIConstants.PANEL_BG);
    singleVizCard.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
        new EmptyBorder(2, 2, 2, 2)));
    singleVizCard.add(barPanel, BorderLayout.CENTER);

    return singleVizCard;
  }

  private JPanel createComparisonVisualizationCard() {
    JPanel compVizCard = new JPanel(new BorderLayout(0, 10));
    compVizCard.setBackground(UIConstants.BG_COLOR);

    JPanel compGrid = new JPanel(new GridLayout(1, 2, 15, 0));
    compGrid.setBackground(UIConstants.BG_COLOR);

    JPanel viz1Card = createVisualizationCard(barPanel1);
    JPanel viz2Card = createVisualizationCard(barPanel2);

    compGrid.add(viz1Card);
    compGrid.add(viz2Card);

    JPanel resultPanel = createResultPanel();

    compVizCard.add(compGrid, BorderLayout.CENTER);
    compVizCard.add(resultPanel, BorderLayout.SOUTH);

    return compVizCard;
  }

  private JPanel createVisualizationCard(BarPanel panel) {
    JPanel vizCard = new JPanel(new BorderLayout());
    vizCard.setBackground(UIConstants.PANEL_BG);
    vizCard.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
        new EmptyBorder(2, 2, 2, 2)));
    vizCard.add(panel, BorderLayout.CENTER);

    return vizCard;
  }

  private JPanel createResultPanel() {
    JPanel resultPanel = new JPanel(new BorderLayout());
    resultPanel.setBackground(UIConstants.PANEL_BG);
    resultPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
        new EmptyBorder(15, 20, 15, 20)));
    resultPanel.add(comparisonResultLabel, BorderLayout.CENTER);
    resultPanel.setPreferredSize(new Dimension(0, 60));

    return resultPanel;
  }

  private void setupListeners() {
    startBtn.addActionListener(e -> sortController.startSorting());
    stopBtn.addActionListener(e -> sortController.requestStop());

    backToSingleBtn.addActionListener(e -> {
      if (running.get()) {
        JOptionPane.showMessageDialog(this,
            "Please stop the current sorting before changing mode.",
            "Cannot Change Mode", JOptionPane.WARNING_MESSAGE);
        return;
      }
      compareToggle.setSelected(false);
      switchToSingleMode();
    });

    compareToggle.addActionListener(e -> {
      if (running.get()) {
        compareToggle.setSelected(!compareToggle.isSelected());
        JOptionPane.showMessageDialog(this,
            "Please stop the current sorting before changing mode.",
            "Cannot Change Mode", JOptionPane.WARNING_MESSAGE);
        return;
      }

      boolean isCompare = compareToggle.isSelected();
      if (isCompare) {
        switchToCompareMode();
      } else {
        switchToSingleMode();
      }
    });

    algoCombo.addActionListener(e -> {
      int index = algoCombo.getSelectedIndex();
      algoDescLabel.setText(AlgorithmConstants.DESCRIPTIONS[index]);
      barPanel.setAlgorithmName(AlgorithmConstants.ALGORITHMS[index]);
    });

    algoCombo1.addActionListener(e -> {
      barPanel1.setAlgorithmName(AlgorithmConstants.ALGORITHMS[algoCombo1.getSelectedIndex()]);
    });

    algoCombo2.addActionListener(e -> {
      barPanel2.setAlgorithmName(AlgorithmConstants.ALGORITHMS[algoCombo2.getSelectedIndex()]);
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

  private void switchToCompareMode() {
    CardLayout algoLayout = (CardLayout) ((JPanel) algoCombo.getParent().getParent()).getLayout();
    algoLayout.show((JPanel) algoCombo.getParent().getParent(), "compare");
    vizCardLayout.show(visualizationPanel, "compare");

    compareToggle.setBackground(UIConstants.COMPARE_BTN_ACTIVE);
    compareToggle.setText("⚖ Compare Mode ✓");
    backToSingleBtn.setVisible(true);

    generateData();
  }

  private void switchToSingleMode() {
    CardLayout algoLayout = (CardLayout) ((JPanel) algoCombo.getParent().getParent()).getLayout();
    algoLayout.show((JPanel) algoCombo.getParent().getParent(), "single");
    vizCardLayout.show(visualizationPanel, "single");

    compareToggle.setBackground(UIConstants.COMPARE_BTN_COLOR);
    compareToggle.setText("⚖ Compare Mode");
    backToSingleBtn.setVisible(false);

    generateData();
  }

  private void generateData() {
    if (running.get())
      return;

    int[] arr = ArrayGenerator.generateRandomArray(sizeSlider.getValue());

    if (compareToggle.isSelected()) {
      barPanel1.setValues(ArrayGenerator.copyArray(arr));
      barPanel2.setValues(ArrayGenerator.copyArray(arr));
      comparisonResultLabel.setText("");
    } else {
      barPanel.setValues(arr);
    }
  }

  // Getters for controller
  public AtomicBoolean getRunning() {
    return running;
  }

  public SortingState getSingleState() {
    return singleState;
  }

  public SortingState getState1() {
    return state1;
  }

  public SortingState getState2() {
    return state2;
  }

  public ToneGenerator getToneGenerator() {
    return toneGenerator;
  }

  public BarPanel getBarPanel() {
    return barPanel;
  }

  public BarPanel getBarPanel1() {
    return barPanel1;
  }

  public BarPanel getBarPanel2() {
    return barPanel2;
  }

  public JComboBox<String> getAlgoCombo() {
    return algoCombo;
  }

  public JComboBox<String> getAlgoCombo1() {
    return algoCombo1;
  }

  public JComboBox<String> getAlgoCombo2() {
    return algoCombo2;
  }

  public JSlider getSpeedSlider() {
    return speedSlider;
  }

  public JCheckBox getSoundToggle() {
    return soundToggle;
  }

  public JToggleButton getCompareToggle() {
    return compareToggle;
  }

  public JLabel getComparisonResultLabel() {
    return comparisonResultLabel;
  }

  public void setControlsEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> {
      startBtn.setEnabled(enabled);
      stopBtn.setEnabled(!enabled);
      compareToggle.setEnabled(enabled);
      backToSingleBtn.setEnabled(enabled);
      algoCombo.setEnabled(enabled);
      algoCombo1.setEnabled(enabled);
      algoCombo2.setEnabled(enabled);
      sizeSlider.setEnabled(enabled);
      soundToggle.setEnabled(true);
    });
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
