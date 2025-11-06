import javax.swing.*;
import java.awt.*;

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
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
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
