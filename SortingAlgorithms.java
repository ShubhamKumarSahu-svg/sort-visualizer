import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Contains implementations of various sorting algorithms with visualization
 * support.
 */
public class SortingAlgorithms {
  private final int[] array;
  private final SortVisualizerFrame frame;
  private final AtomicBoolean stopRequested;
  private final boolean soundEnabled;
  private final ToneGenerator toneGenerator;

  public SortingAlgorithms(int[] array, SortVisualizerFrame frame,
      AtomicBoolean stopRequested, boolean soundEnabled,
      ToneGenerator toneGenerator) {
    this.array = array;
    this.frame = frame;
    this.stopRequested = stopRequested;
    this.soundEnabled = soundEnabled;
    this.toneGenerator = toneGenerator;
  }

  // ===== Bubble Sort =====
  public void bubbleSort() {
    int n = array.length;
    for (int i = 0; i < n - 1 && !stopRequested.get(); i++) {
      for (int j = 0; j < n - i - 1 && !stopRequested.get(); j++) {
        highlight(j, j + 1);
        compareTone(array[j], array[j + 1]);
        if (array[j] > array[j + 1]) {
          swap(j, j + 1);
        }
        stepDelay();
      }
    }
    frame.clearHighlights();
  }

  // ===== Selection Sort =====
  public void selectionSort() {
    int n = array.length;
    for (int i = 0; i < n - 1 && !stopRequested.get(); i++) {
      int min = i;
      for (int j = i + 1; j < n && !stopRequested.get(); j++) {
        highlight(min, j);
        compareTone(array[min], array[j]);
        if (array[j] < array[min]) {
          min = j;
        }
        stepDelay();
      }
      if (min != i) {
        swap(i, min);
      }
    }
    frame.clearHighlights();
  }

  // ===== Insertion Sort =====
  public void insertionSort() {
    for (int i = 1; i < array.length && !stopRequested.get(); i++) {
      int key = array[i];
      int j = i - 1;
      while (j >= 0 && !stopRequested.get()) {
        highlight(j, j + 1);
        compareTone(array[j], key);
        if (array[j] > key) {
          array[j + 1] = array[j];
          swapTone(array[j], key);
          frame.repaintBars();
          stepDelay();
          j--;
        } else {
          break;
        }
      }
      array[j + 1] = key;
      frame.repaintBars();
      stepDelay();
    }
    frame.clearHighlights();
  }

  // ===== Merge Sort =====
  public void mergeSort() {
    mergeSort(0, array.length - 1, new int[array.length]);
    frame.clearHighlights();
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
      compareToneSafe(i - 1, j - 1);
      stepDelay();
    }

    for (k = l; k <= r; k++) {
      array[k] = tmp[k];
      frame.repaintBars();
      stepDelay();
    }
  }

  // ===== Quick Sort =====
  public void quickSort() {
    quickSort(0, array.length - 1);
    frame.clearHighlights();
  }

  private void quickSort(int low, int high) {
    if (stopRequested.get() || low >= high)
      return;

    int i = low, j = high;
    int pivot = array[(low + high) >>> 1];

    while (i <= j && !stopRequested.get()) {
      while (array[i] < pivot && !stopRequested.get()) {
        highlight(i, -1);
        compareTone(array[i], pivot);
        i++;
        stepDelay();
      }
      while (array[j] > pivot && !stopRequested.get()) {
        highlight(j, -1);
        compareTone(array[j], pivot);
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

  // ===== Heap Sort =====
  public void heapSort() {
    int n = array.length;
    for (int i = n / 2 - 1; i >= 0 && !stopRequested.get(); i--) {
      heapify(n, i);
    }
    for (int i = n - 1; i > 0 && !stopRequested.get(); i--) {
      swap(0, i);
      heapify(i, 0);
    }
    frame.clearHighlights();
  }

  private void heapify(int n, int i) {
    int largest = i;
    while (!stopRequested.get()) {
      int l = 2 * i + 1;
      int r = 2 * i + 2;

      if (l < n) {
        highlight(largest, l);
        compareTone(array[largest], array[l]);
        if (array[l] > array[largest]) {
          largest = l;
        }
        stepDelay();
      }
      if (r < n) {
        highlight(largest, r);
        compareTone(array[largest], array[r]);
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

  // ===== Shell Sort =====
  public void shellSort() {
    int n = array.length;
    for (int gap = n / 2; gap > 0 && !stopRequested.get(); gap /= 2) {
      for (int i = gap; i < n && !stopRequested.get(); i++) {
        int temp = array[i];
        int j = i;
        while (j >= gap && array[j - gap] > temp && !stopRequested.get()) {
          highlight(j, j - gap);
          compareTone(array[j - gap], temp);
          array[j] = array[j - gap];
          swapTone(array[j], temp);
          frame.repaintBars();
          stepDelay();
          j -= gap;
        }
        array[j] = temp;
        frame.repaintBars();
        stepDelay();
      }
    }
    frame.clearHighlights();
  }

  // ===== Cocktail Sort (Bidirectional Bubble Sort) =====
  public void cocktailSort() {
    boolean swapped = true;
    int start = 0;
    int end = array.length - 1;

    while (swapped && !stopRequested.get()) {
      swapped = false;

      // Forward pass
      for (int i = start; i < end && !stopRequested.get(); i++) {
        highlight(i, i + 1);
        compareTone(array[i], array[i + 1]);
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

      // Backward pass
      for (int i = end - 1; i >= start && !stopRequested.get(); i--) {
        highlight(i, i + 1);
        compareTone(array[i], array[i + 1]);
        if (array[i] > array[i + 1]) {
          swap(i, i + 1);
          swapped = true;
        }
        stepDelay();
      }
      start++;
    }
    frame.clearHighlights();
  }

  // ===== Comb Sort =====
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
        compareTone(array[i], array[i + gap]);
        if (array[i] > array[i + gap]) {
          swap(i, i + gap);
          swapped = true;
        }
        stepDelay();
      }
    }
    frame.clearHighlights();
  }

  // ===== Gnome Sort =====
  public void gnomeSort() {
    int pos = 0;
    while (pos < array.length && !stopRequested.get()) {
      if (pos == 0 || array[pos] >= array[pos - 1]) {
        pos++;
      } else {
        highlight(pos, pos - 1);
        compareTone(array[pos], array[pos - 1]);
        swap(pos, pos - 1);
        pos--;
      }
      stepDelay();
    }
    frame.clearHighlights();
  }

  // ===== Radix Sort (LSD) =====
  public void radixSort() {
    int max = Arrays.stream(array).max().orElse(0);

    for (int exp = 1; max / exp > 0 && !stopRequested.get(); exp *= 10) {
      countingSortByDigit(exp);
    }
    frame.clearHighlights();
  }

  private void countingSortByDigit(int exp) {
    int n = array.length;
    int[] output = new int[n];
    int[] count = new int[10];

    // Store count of occurrences
    for (int i = 0; i < n; i++) {
      int digit = (array[i] / exp) % 10;
      count[digit]++;
      highlight(i, -1);
      stepDelay();
    }

    // Change count[i] to actual position
    for (int i = 1; i < 10; i++) {
      count[i] += count[i - 1];
    }

    // Build output array
    for (int i = n - 1; i >= 0 && !stopRequested.get(); i--) {
      int digit = (array[i] / exp) % 10;
      output[count[digit] - 1] = array[i];
      count[digit]--;
      highlight(i, count[digit]);
      stepDelay();
    }

    // Copy output to array
    for (int i = 0; i < n && !stopRequested.get(); i++) {
      array[i] = output[i];
      highlight(i, -1);
      frame.repaintBars();
      stepDelay();
    }
  }

  // ===== Counting Sort =====
  public void countingSort() {
    int max = Arrays.stream(array).max().orElse(0);
    int min = Arrays.stream(array).min().orElse(0);
    int range = max - min + 1;

    int[] count = new int[range];
    int[] output = new int[array.length];

    // Store count
    for (int i = 0; i < array.length && !stopRequested.get(); i++) {
      count[array[i] - min]++;
      highlight(i, -1);
      stepDelay();
    }

    // Cumulative count
    for (int i = 1; i < range; i++) {
      count[i] += count[i - 1];
    }

    // Build output
    for (int i = array.length - 1; i >= 0 && !stopRequested.get(); i--) {
      output[count[array[i] - min] - 1] = array[i];
      count[array[i] - min]--;
      highlight(i, -1);
      stepDelay();
    }

    // Copy to original
    for (int i = 0; i < array.length && !stopRequested.get(); i++) {
      array[i] = output[i];
      highlight(i, -1);
      frame.repaintBars();
      stepDelay();
    }
    frame.clearHighlights();
  }

  // ===== Bogo Sort (Random/Permutation Sort) - For fun! =====
  public void bogoSort() {
    Random rand = new Random();
    int attempts = 0;
    int maxAttempts = 10000; // Prevent infinite loops

    while (!isSorted() && !stopRequested.get() && attempts < maxAttempts) {
      // Shuffle randomly
      for (int i = array.length - 1; i > 0 && !stopRequested.get(); i--) {
        int j = rand.nextInt(i + 1);
        highlight(i, j);
        swap(i, j);
        stepDelay();
      }
      attempts++;
    }
    frame.clearHighlights();
  }

  private boolean isSorted() {
    for (int i = 1; i < array.length; i++) {
      if (array[i] < array[i - 1]) {
        return false;
      }
    }
    return true;
  }

  // ===== Helper methods =====
  private void swap(int i, int j) {
    int t = array[i];
    array[i] = array[j];
    array[j] = t;
    highlight(i, j);
    swapTone(array[i], array[j]);
    frame.repaintBars();
    stepDelay();
  }

  private void highlight(int i, int j) {
    frame.highlight(i, j);
  }

  private void compareTone(int v1, int v2) {
    if (soundEnabled) {
      toneGenerator.playCompare(v1, v2);
    }
  }

  private void compareToneSafe(int i, int j) {
    if (i >= 0 && j >= 0 && i < array.length && j < array.length) {
      compareTone(array[i], array[j]);
    }
  }

  private void swapTone(int v1, int v2) {
    if (soundEnabled) {
      toneGenerator.playSwap(v1, v2);
    }
  }

  private void stepDelay() {
    frame.stepDelay();
  }
}
