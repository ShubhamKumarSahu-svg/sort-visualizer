package constants;

public class AlgorithmConstants {
  public static final String[] ALGORITHMS = {
      "Bubble Sort", "Selection Sort", "Insertion Sort",
      "Merge Sort", "Quick Sort", "Heap Sort",
      "Shell Sort", "Cocktail Sort", "Comb Sort",
      "Gnome Sort", "Radix Sort", "Counting Sort"
  };

  public static final String[] DESCRIPTIONS = {
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

  private AlgorithmConstants() {
    // Prevent instantiation
  }
}
