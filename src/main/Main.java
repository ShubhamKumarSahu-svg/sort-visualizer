package main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ui.SortVisualizerFrame;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }

      SortVisualizerFrame frame = new SortVisualizerFrame();
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }
}
