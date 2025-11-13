package ui.controllers;

import java.awt.Color;

import javax.swing.SwingUtilities;

import algorithms.SortingAlgorithms;
import model.SortingState;
import ui.SortVisualizerFrame;
import ui.callbacks.VisualizationCallback;
import ui.components.BarPanel;
import util.ArrayGenerator;

public class SortController {
  private final SortVisualizerFrame frame;

  public SortController(SortVisualizerFrame frame) {
    this.frame = frame;
  }

  public void startSorting() {
    if (frame.getRunning().get())
      return;

    frame.getRunning().set(true);
    frame.getSingleState().getStopRequested().set(false);
    frame.getState1().getStopRequested().set(false);
    frame.getState2().getStopRequested().set(false);
    frame.getToneGenerator().reset();
    frame.setControlsEnabled(false);

    if (frame.getCompareToggle().isSelected()) {
      startComparisonSort();
    } else {
      startSingleSort();
    }
  }

  private void startSingleSort() {
    BarPanel panel = frame.getBarPanel();
    if (panel.getValues() == null || panel.getValues().length == 0)
      return;

    SortingState state = frame.getSingleState();
    state.setStartTime(System.currentTimeMillis());
    int[] arr = ArrayGenerator.copyArray(panel.getValues());
    String algo = (String) frame.getAlgoCombo().getSelectedItem();

    panel.setSortingState(true);

    VisualizationCallback callback = new VisualizationCallback() {
      @Override
      public void onHighlight(int i, int j) {
        SwingUtilities.invokeLater(() -> panel.setHighlights(i, j));
      }

      @Override
      public void onClearHighlights() {
        SwingUtilities.invokeLater(() -> panel.clearHighlights());
      }

      @Override
      public void onRepaint() {
        SwingUtilities.invokeLater(() -> panel.repaint());
      }

      @Override
      public void onIncrementComparisons() {
        SwingUtilities.invokeLater(() -> panel.incrementComparisons());
      }

      @Override
      public void onIncrementSwaps() {
        SwingUtilities.invokeLater(() -> panel.incrementSwaps());
      }

      @Override
      public void onStepDelay() {
        frame.stepDelay();
      }
    };

    SortingAlgorithms sorter = new SortingAlgorithms(arr, callback,
        state.getStopRequested(), frame.getSoundToggle().isSelected(),
        frame.getToneGenerator());

    Thread timerThread = createTimerThread(state, panel);
    state.setTimerThread(timerThread);
    timerThread.start();

    Thread workerThread = new Thread(() -> {
      try {
        runAlgorithm(sorter, algo);
        if (!state.getStopRequested().get()) {
          SwingUtilities.invokeLater(() -> panel.setSortedState());
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        SwingUtilities.invokeLater(this::resetUiAfterRun);
      }
    }, "sort-worker");

    state.setWorkerThread(workerThread);
    panel.attachLiveArray(arr, frame.getSpeedSlider().getValue());
    workerThread.start();
  }

  private void startComparisonSort() {
    BarPanel panel1 = frame.getBarPanel1();
    BarPanel panel2 = frame.getBarPanel2();

    if (panel1.getValues() == null || panel1.getValues().length == 0)
      return;

    SortingState state1 = frame.getState1();
    SortingState state2 = frame.getState2();

    state1.setFinished(false);
    state2.setFinished(false);
    state1.setStartTime(System.currentTimeMillis());
    state2.setStartTime(System.currentTimeMillis());

    int[] arr1 = ArrayGenerator.copyArray(panel1.getValues());
    int[] arr2 = ArrayGenerator.copyArray(panel2.getValues());
    String algo1 = (String) frame.getAlgoCombo1().getSelectedItem();
    String algo2 = (String) frame.getAlgoCombo2().getSelectedItem();

    panel1.setSortingState(true);
    panel2.setSortingState(true);
    frame.getComparisonResultLabel().setText("Both algorithms are running...");
    frame.getComparisonResultLabel().setForeground(new Color(251, 191, 36));

    VisualizationCallback callback1 = createCallback(panel1);
    VisualizationCallback callback2 = createCallback(panel2);

    SortingAlgorithms sorter1 = new SortingAlgorithms(arr1, callback1,
        state1.getStopRequested(), frame.getSoundToggle().isSelected(),
        frame.getToneGenerator());
    SortingAlgorithms sorter2 = new SortingAlgorithms(arr2, callback2,
        state2.getStopRequested(), frame.getSoundToggle().isSelected(),
        frame.getToneGenerator());

    Thread timerThread1 = createTimerThread(state1, panel1);
    Thread timerThread2 = createTimerThread(state2, panel2);
    state1.setTimerThread(timerThread1);
    state2.setTimerThread(timerThread2);
    timerThread1.start();
    timerThread2.start();

    Thread workerThread1 = new Thread(() -> {
      try {
        runAlgorithm(sorter1, algo1);
        state1.setFinished(true);
        if (!state1.getStopRequested().get()) {
          SwingUtilities.invokeLater(() -> {
            panel1.setSortedState();
            checkComparisonComplete();
          });
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        synchronized (this) {
          if (state1.isFinished() && state2.isFinished()) {
            SwingUtilities.invokeLater(this::resetUiAfterRun);
          }
        }
      }
    }, "sort-worker-1");

    Thread workerThread2 = new Thread(() -> {
      try {
        runAlgorithm(sorter2, algo2);
        state2.setFinished(true);
        if (!state2.getStopRequested().get()) {
          SwingUtilities.invokeLater(() -> {
            panel2.setSortedState();
            checkComparisonComplete();
          });
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        synchronized (this) {
          if (state1.isFinished() && state2.isFinished()) {
            SwingUtilities.invokeLater(this::resetUiAfterRun);
          }
        }
      }
    }, "sort-worker-2");

    state1.setWorkerThread(workerThread1);
    state2.setWorkerThread(workerThread2);
    panel1.attachLiveArray(arr1, frame.getSpeedSlider().getValue());
    panel2.attachLiveArray(arr2, frame.getSpeedSlider().getValue());
    workerThread1.start();
    workerThread2.start();
  }

  private Thread createTimerThread(SortingState state, BarPanel panel) {
    return new Thread(() -> {
      while (frame.getRunning().get() && !Thread.currentThread().isInterrupted()) {
        try {
          Thread.sleep(100);
          long elapsed = (System.currentTimeMillis() - state.getStartTime()) / 1000;
          SwingUtilities.invokeLater(() -> panel.setElapsedTime(elapsed));
        } catch (InterruptedException e) {
          break;
        }
      }
    }, "timer-thread");
  }

  private VisualizationCallback createCallback(BarPanel panel) {
    return new VisualizationCallback() {
      @Override
      public void onHighlight(int i, int j) {
        SwingUtilities.invokeLater(() -> panel.setHighlights(i, j));
      }

      @Override
      public void onClearHighlights() {
        SwingUtilities.invokeLater(() -> panel.clearHighlights());
      }

      @Override
      public void onRepaint() {
        SwingUtilities.invokeLater(() -> panel.repaint());
      }

      @Override
      public void onIncrementComparisons() {
        SwingUtilities.invokeLater(() -> panel.incrementComparisons());
      }

      @Override
      public void onIncrementSwaps() {
        SwingUtilities.invokeLater(() -> panel.incrementSwaps());
      }

      @Override
      public void onStepDelay() {
        frame.stepDelay();
      }
    };
  }

  private void checkComparisonComplete() {
    SortingState state1 = frame.getState1();
    SortingState state2 = frame.getState2();

    if (state1.isFinished() && state2.isFinished()) {
      BarPanel panel1 = frame.getBarPanel1();
      BarPanel panel2 = frame.getBarPanel2();

      long time1 = panel1.getElapsedSeconds();
      long time2 = panel2.getElapsedSeconds();
      long comp1 = panel1.getComparisons();
      long comp2 = panel2.getComparisons();
      long swap1 = panel1.getSwaps();
      long swap2 = panel2.getSwaps();

      String algo1Name = (String) frame.getAlgoCombo1().getSelectedItem();
      String algo2Name = (String) frame.getAlgoCombo2().getSelectedItem();

      StringBuilder result = new StringBuilder("Comparison Complete! ");

      if (time1 < time2) {
        result.append(String.format("🏆 %s was faster (%ds vs %ds)", algo1Name, time1, time2));
      } else if (time2 < time1) {
        result.append(String.format("🏆 %s was faster (%ds vs %ds)", algo2Name, time2, time1));
      } else {
        result.append("⚖ Both took the same time!");
      }

      result.append(String.format(" | Comparisons: %d vs %d | Swaps: %d vs %d", comp1, comp2, swap1, swap2));

      frame.getComparisonResultLabel().setText(result.toString());
      frame.getComparisonResultLabel().setForeground(new Color(16, 185, 129));
    }
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

  public void requestStop() {
    if (!frame.getRunning().get()) {
      return;
    }

    frame.getSingleState().stop();
    frame.getState1().stop();
    frame.getState2().stop();
    frame.getToneGenerator().stopAllSounds();

    new Thread(() -> {
      try {
        Thread.sleep(300);
        SwingUtilities.invokeLater(this::resetUiAfterRun);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }, "stop-handler").start();
  }

  private void resetUiAfterRun() {
    frame.getRunning().set(false);
    frame.getSingleState().reset();
    frame.getState1().reset();
    frame.getState2().reset();
    frame.getToneGenerator().stopAllSounds();
    frame.getToneGenerator().reset();
    frame.setControlsEnabled(true);

    if (!frame.getCompareToggle().isSelected()) {
      frame.getBarPanel().setSortingState(false);
    } else {
      frame.getBarPanel1().setSortingState(false);
      frame.getBarPanel2().setSortingState(false);
    }
  }
}
