import javax.swing.*;

/**
 * SortVisualizer — Main entry point for the sorting algorithm visualizer.
 *
 * To compile and run:
 * javac *.java
 * java SortVisualizer
 *
 * Requires Java 11+
 */
public class SortVisualizer {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        // Set system look and feel for better appearance
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        // Fall back to default look and feel
      }

      SortVisualizerFrame frame = new SortVisualizerFrame();
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }
}
