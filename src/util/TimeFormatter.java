package util;

public class TimeFormatter {
  public static String formatTime(long seconds) {
    if (seconds < 60) {
      return seconds + "s";
    } else {
      long mins = seconds / 60;
      long secs = seconds % 60;
      return String.format("%dm %ds", mins, secs);
    }
  }

  private TimeFormatter() {
    // Prevent instantiation
  }
}
