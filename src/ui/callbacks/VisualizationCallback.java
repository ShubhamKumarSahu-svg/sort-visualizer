package ui.callbacks;

public interface VisualizationCallback {
  void onHighlight(int i, int j);

  void onClearHighlights();

  void onRepaint();

  void onIncrementComparisons();

  void onIncrementSwaps();

  void onStepDelay();
}
