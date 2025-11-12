import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class SortingAlgorithms {
  private final int[] array;
  private final SortVisualizerFrame frameSingle;
  private final SortVisualizerFrame frameCompare;
  private final AtomicBoolean stopRequested;
  private final boolean soundEnabled;
  private final ToneGenerator toneGenerator;
  private final boolean isComparisonPanel2;

  public SortingAlgorithms(int[] array, SortVisualizerFrame frameSingle,
      SortVisualizerFrame frameCompare, AtomicBoolean stopRequested,
      boolean soundEnabled, ToneGenerator toneGenerator, boolean isPanel2) {
    this.array = array;
    this.frameSingle = frameSingle;
    this.frameCompare = frameCompare;
    this.stopRequested = stopRequested;
    this.soundEnabled = soundEnabled;
    this.toneGenerator = toneGenerator;
    this.isComparisonPanel2 = isPanel2;
  }

  public void bubbleSort() {
    int n = array.length;
    for (int i = 0; i < n - 1 && !stopRequested.get(); i++) {
      for (int j = 0; j < n - i - 1 && !stopRequested.get(); j++) {
        highlight(j, j + 1);
        compare(array[j], array[j + 1]);
        if (array[j] > array[j + 1]) {
          swap(j, j + 1);
        }
        stepDelay();
      }
    }
    clearHighlights();
  }

  public void selectionSort() {
    int n = array.length;
    for (int i = 0; i < n - 1 && !stopRequested.get(); i++) {
      int min = i;
      for (int j = i + 1; j < n && !stopRequested.get(); j++) {
        highlight(min, j);
        compare(array[min], array[j]);
        if (array[j] < array[min]) {
          min = j;
        }
        stepDelay();
      }
      if (min != i) {
        swap(i, min);
      }
    }
    clearHighlights();
  }

  public void insertionSort() {
    for (int i = 1; i < array.length && !stopRequested.get(); i++) {
      int key = array[i];
      int j = i - 1;
      while (j >= 0 && !stopRequested.get()) {
        highlight(j, j + 1);
        compare(array[j], key);
        if (array[j] > key) {
          array[j + 1] = array[j];
          incrementSwaps();
          playSwap(array[j], key);
          repaintBars();
          stepDelay();
          j--;
        } else {
          break;
        }
      }
      array[j + 1] = key;
      repaintBars();
      stepDelay();
    }
    clearHighlights();
  }

  public void mergeSort() {
    mergeSort(0, array.length - 1, new int[array.length]);
    clearHighlights();
  }

  private void mergeSort(int l, int r, int[] tmp) {
    if (stopRequested.get() || l >= r)
      return;

    int m = (l + r) >>> 1;
    mergeSort(l, m, tmp);
    mergeSort(m + 1, r, tmp);

    int i = l, j = m + 1, k = l;
    while (i <= m || j <= r) {
      if (stopRequested.get())
        return;

      if (j > r || (i <= m && array[i] <= array[j])) {
        tmp[k++] = array[i++];
      } else {
        tmp[k++] = array[j++];
      }
      highlight(i - 1, j - 1);
      compareSafe(i - 1, j - 1);
      stepDelay();
    }

    for (k = l; k <= r; k++) {
      array[k] = tmp[k];
      incrementSwaps();
      repaintBars();
      stepDelay();
    }
  }

  public void quickSort() {
    quickSort(0, array.length - 1);
    clearHighlights();
  }

  private void quickSort(int low, int high) {
    if (stopRequested.get() || low >= high)
      return;

    int i = low, j = high;
    int pivot = array[(low + high) >>> 1];

    while (i <= j && !stopRequested.get()) {
      while (array[i] < pivot && !stopRequested.get()) {
        highlight(i, -1);
        compare(array[i], pivot);
        i++;
        stepDelay();
      }
      while (array[j] > pivot && !stopRequested.get()) {
        highlight(j, -1);
        compare(array[j], pivot);
        j--;
        stepDelay();
      }
      if (i <= j && !stopRequested.get()) {
        swap(i, j);
        i++;
        j--;
      }
    }

    if (low < j)
      quickSort(low, j);
    if (i < high)
      quickSort(i, high);
  }

  public void heapSort() {
    int n = array.length;
    for (int i = n / 2 - 1; i >= 0 && !stopRequested.get(); i--) {
      heapify(n, i);
    }
    for (int i = n - 1; i > 0 && !stopRequested.get(); i--) {
      swap(0, i);
      heapify(i, 0);
    }
    clearHighlights();
  }

  private void heapify(int n, int i) {
    int largest = i;
    while (!stopRequested.get()) {
      int l = 2 * i + 1;
      int r = 2 * i + 2;

      if (l < n) {
        highlight(largest, l);
        compare(array[largest], array[l]);
        if (array[l] > array[largest]) {
          largest = l;
        }
        stepDelay();
      }
      if (r < n) {
        highlight(largest, r);
        compare(array[largest], array[r]);
        if (array[r] > array[largest]) {
          largest = r;
        }
        stepDelay();
      }

      if (largest != i) {
        swap(i, largest);
        i = largest;
      } else {
        break;
      }
    }
  }

  public void shellSort() {
    int n = array.length;
    for (int gap = n / 2; gap > 0 && !stopRequested.get(); gap /= 2) {
      for (int i = gap; i < n && !stopRequested.get(); i++) {
        int temp = array[i];
        int j = i;
        while (j >= gap && array[j - gap] > temp && !stopRequested.get()) {
          highlight(j, j - gap);
          compare(array[j - gap], temp);
          array[j] = array[j - gap];
          incrementSwaps();
          playSwap(array[j], temp);
          repaintBars();
          stepDelay();
          j -= gap;
        }
        array[j] = temp;
        repaintBars();
        stepDelay();
      }
    }
    clearHighlights();
  }

  public void cocktailSort() {
    boolean swapped = true;
    int start = 0;
    int end = array.length - 1;

    while (swapped && !stopRequested.get()) {
      swapped = false;
      for (int i = start; i < end && !stopRequested.get(); i++) {
        highlight(i, i + 1);
        compare(array[i], array[i + 1]);
        if (array[i] > array[i + 1]) {
          swap(i, i + 1);
          swapped = true;
        }
        stepDelay();
      }

      if (!swapped)
        break;
      swapped = false;
      end--;

      for (int i = end - 1; i >= start && !stopRequested.get(); i--) {
        highlight(i, i + 1);
        compare(array[i], array[i + 1]);
        if (array[i] > array[i + 1]) {
          swap(i, i + 1);
          swapped = true;
        }
        stepDelay();
      }
      start++;
    }
    clearHighlights();
  }

  public void combSort() {
    int n = array.length;
    int gap = n;
    boolean swapped = true;
    final double shrink = 1.3;

    while (gap > 1 || swapped) {
      if (stopRequested.get())
        break;

      gap = (int) (gap / shrink);
      if (gap < 1)
        gap = 1;

      swapped = false;
      for (int i = 0; i + gap < n && !stopRequested.get(); i++) {
        highlight(i, i + gap);
        compare(array[i], array[i + gap]);
        if (array[i] > array[i + gap]) {
          swap(i, i + gap);
          swapped = true;
        }
        stepDelay();
      }
    }
    clearHighlights();
  }

  public void gnomeSort() {
    int pos = 0;
    while (pos < array.length && !stopRequested.get()) {
      if (pos == 0 || array[pos] >= array[pos - 1]) {
        pos++;
      } else {
        highlight(pos, pos - 1);
        compare(array[pos], array[pos - 1]);
        swap(pos, pos - 1);
        pos--;
      }
      stepDelay();
    }
    clearHighlights();
  }

  public void radixSort() {
    int max = Arrays.stream(array).max().orElse(0);
    for (int exp = 1; max / exp > 0 && !stopRequested.get(); exp *= 10) {
      countingSortByDigit(exp);
    }
    clearHighlights();
  }

  private void countingSortByDigit(int exp) {
    int n = array.length;
    int[] output = new int[n];
    int[] count = new int[10];

    for (int i = 0; i < n; i++) {
      int digit = (array[i] / exp) % 10;
      count[digit]++;
      highlight(i, -1);
      incrementComparisons();
      stepDelay();
    }

    for (int i = 1; i < 10; i++) {
      count[i] += count[i - 1];
    }

    for (int i = n - 1; i >= 0 && !stopRequested.get(); i--) {
      int digit = (array[i] / exp) % 10;
      output[count[digit] - 1] = array[i];
      count[digit]--;
      highlight(i, count[digit]);
      incrementSwaps();
      stepDelay();
    }

    for (int i = 0; i < n && !stopRequested.get(); i++) {
      array[i] = output[i];
      highlight(i, -1);
      repaintBars();
      stepDelay();
    }
  }

  public void countingSort() {
    int max = Arrays.stream(array).max().orElse(0);
    int min = Arrays.stream(array).min().orElse(0);
    int range = max - min + 1;

    int[] count = new int[range];
    int[] output = new int[array.length];

    for (int i = 0; i < array.length && !stopRequested.get(); i++) {
      count[array[i] - min]++;
      highlight(i, -1);
      incrementComparisons();
      stepDelay();
    }

    for (int i = 1; i < range; i++) {
      count[i] += count[i - 1];
    }

    for (int i = array.length - 1; i >= 0 && !stopRequested.get(); i--) {
      output[count[array[i] - min] - 1] = array[i];
      count[array[i] - min]--;
      highlight(i, -1);
      incrementSwaps();
      stepDelay();
    }

    for (int i = 0; i < array.length && !stopRequested.get(); i++) {
      array[i] = output[i];
      highlight(i, -1);
      repaintBars();
      stepDelay();
    }
    clearHighlights();
  }

  private void swap(int i, int j) {
    int t = array[i];
    array[i] = array[j];
    array[j] = t;
    highlight(i, j);
    incrementSwaps();
    playSwap(array[i], array[j]);
    repaintBars();
    stepDelay();
  }

  private void highlight(int i, int j) {
    if (frameCompare != null) {
      if (isComparisonPanel2) {
        frameCompare.highlight2(i, j);
      } else {
        frameCompare.highlight1(i, j);
      }
    } else if (frameSingle != null) {
      frameSingle.highlight(i, j);
    }
  }

  private void clearHighlights() {
    if (frameCompare != null) {
      if (isComparisonPanel2) {
        frameCompare.clearHighlights2();
      } else {
        frameCompare.clearHighlights1();
      }
    } else if (frameSingle != null) {
      frameSingle.clearHighlights();
    }
  }

  private void repaintBars() {
    if (frameCompare != null) {
      if (isComparisonPanel2) {
        frameCompare.repaintBars2();
      } else {
        frameCompare.repaintBars1();
      }
    } else if (frameSingle != null) {
      frameSingle.repaintBars();
    }
  }

  private void incrementComparisons() {
    if (frameCompare != null) {
      if (isComparisonPanel2) {
        frameCompare.incrementComparisons2();
      } else {
        frameCompare.incrementComparisons1();
      }
    } else if (frameSingle != null) {
      frameSingle.incrementComparisons();
    }
  }

  private void incrementSwaps() {
    if (frameCompare != null) {
      if (isComparisonPanel2) {
        frameCompare.incrementSwaps2();
      } else {
        frameCompare.incrementSwaps1();
      }
    } else if (frameSingle != null) {
      frameSingle.incrementSwaps();
    }
  }

  private void compare(int v1, int v2) {
    incrementComparisons();
    if (soundEnabled) {
      toneGenerator.playCompare(v1, v2);
    }
  }

  private void compareSafe(int i, int j) {
    if (i >= 0 && j >= 0 && i < array.length && j < array.length) {
      compare(array[i], array[j]);
    }
  }

  private void playSwap(int v1, int v2) {
    if (soundEnabled) {
      toneGenerator.playSwap(v1, v2);
    }
  }

  private void stepDelay() {
    if (frameCompare != null) {
      frameCompare.stepDelay();
    } else if (frameSingle != null) {
      frameSingle.stepDelay();
    }
  }
}
