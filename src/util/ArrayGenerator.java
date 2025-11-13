package util;

import java.util.Random;

public class ArrayGenerator {
  private static final Random RANDOM = new Random();

  public static int[] generateRandomArray(int size) {
    int[] arr = new int[size];
    for (int i = 0; i < size; i++) {
      arr[i] = 5 + RANDOM.nextInt(995);
    }
    return arr;
  }

  public static int[] copyArray(int[] source) {
    if (source == null) {
      return new int[0];
    }
    int[] copy = new int[source.length];
    System.arraycopy(source, 0, copy, 0, source.length);
    return copy;
  }

  private ArrayGenerator() {
    // Prevent instantiation
  }
}
