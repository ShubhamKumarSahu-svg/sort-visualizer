import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;

import javax.swing.JPanel;

/**
 * Modern panel that displays bars with smooth gradients and animations.
 */
public class BarPanel extends JPanel {
  private int[] values = new int[0];
  private int maxVal = 1;
  private volatile int highlightA = -1;
  private volatile int highlightB = -1;
  private volatile boolean sorted = false;
  private int speed = 60;
  private String algorithmName = "Bubble Sort";

  // Modern color scheme
  private static final Color BG_COLOR = new Color(18, 18, 28);
  private static final Color GRID_COLOR = new Color(35, 35, 50, 80);

  // Gradient colors for bars
  private static final Color BAR_START = new Color(99, 110, 250);
  private static final Color BAR_END = new Color(132, 94, 247);

  private static final Color HIGHLIGHT_A_START = new Color(239, 68, 68);
  private static final Color HIGHLIGHT_A_END = new Color(220, 38, 38);

  private static final Color HIGHLIGHT_B_START = new Color(34, 197, 94);
  private static final Color HIGHLIGHT_B_END = new Color(22, 163, 74);

  private static final Color SORTED_START = new Color(168, 85, 247);
  private static final Color SORTED_END = new Color(147, 51, 234);

  public BarPanel() {
    setBackground(BG_COLOR);
    setDoubleBuffered(true);
  }

  public void setValues(int[] v) {
    this.values = Arrays.copyOf(v, v.length);
    this.maxVal = Math.max(1, Arrays.stream(values).max().orElse(1));
    this.sorted = false;
    clearHighlights();
    repaint();
  }

  public int[] getValues() {
    return values;
  }

  public void attachLiveArray(int[] live, int speed) {
    this.values = live;
    this.maxVal = Math.max(1, Arrays.stream(values).max().orElse(1));
    this.speed = speed;
    this.sorted = false;
    repaint();
  }

  public void setSortedState() {
    this.sorted = true;
    repaint();
  }

  public void setHighlights(int a, int b) {
    this.highlightA = a;
    this.highlightB = b;
    repaint();
  }

  public void clearHighlights() {
    this.highlightA = -1;
    this.highlightB = -1;
    repaint();
  }

  public void setAlgorithmName(String name) {
    this.algorithmName = name;
    repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(1000, 550);
  }

  @Override
  protected void paintComponent(Graphics g0) {
    super.paintComponent(g0);
    Graphics2D g = (Graphics2D) g0;

    // Enable anti-aliasing for smooth graphics
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    int w = getWidth();
    int h = getHeight();

    // Draw grid
    drawGrid(g, w, h);

    if (values == null || values.length == 0) {
      drawEmptyMessage(g, w, h);
      return;
    }

    int n = values.length;
    double barW = Math.max(1, (double) w / n);
    int gap = n > 100 ? 0 : 1;

    // Draw bars with gradients
    for (int i = 0; i < n; i++) {
      double ratio = values[i] / (double) maxVal;
      int barHeight = (int) Math.max(3, ratio * (h - 60));
      int x = (int) Math.floor(i * barW);
      int y = h - barHeight - 10;
      int barWidth = (int) Math.ceil(barW) - gap;

      // Determine colors based on state
      Color startColor, endColor;
      if (sorted) {
        startColor = SORTED_START;
        endColor = SORTED_END;
      } else if (i == highlightA) {
        startColor = HIGHLIGHT_A_START;
        endColor = HIGHLIGHT_A_END;
      } else if (i == highlightB) {
        startColor = HIGHLIGHT_B_START;
        endColor = HIGHLIGHT_B_END;
      } else {
        startColor = BAR_START;
        endColor = BAR_END;
      }

      // Create gradient
      GradientPaint gradient = new GradientPaint(
          x, y, startColor,
          x, y + barHeight, endColor);
      g.setPaint(gradient);

      // Draw bar with rounded corners for larger bars
      if (barWidth > 3) {
        g.fillRoundRect(x, y, barWidth, barHeight, 3, 3);
      } else {
        g.fillRect(x, y, barWidth, barHeight);
      }

      // Add highlight effect on top
      if (barHeight > 10) {
        g.setColor(new Color(255, 255, 255, 30));
        if (barWidth > 3) {
          g.fillRoundRect(x, y, barWidth, Math.min(barHeight / 3, 20), 3, 3);
        } else {
          g.fillRect(x, y, barWidth, Math.min(barHeight / 3, 20));
        }
      }
    }

    // Draw info overlay
    drawInfoOverlay(g, n, w, h);
  }

  private void drawGrid(Graphics2D g, int w, int h) {
    g.setColor(GRID_COLOR);
    int gridSpacing = 50;

    // Horizontal lines
    for (int y = 0; y < h; y += gridSpacing) {
      g.drawLine(0, y, w, y);
    }

    // Vertical lines
    for (int x = 0; x < w; x += gridSpacing) {
      g.drawLine(x, 0, x, h);
    }
  }

  private void drawEmptyMessage(Graphics2D g, int w, int h) {
    g.setColor(new Color(148, 163, 184));
    g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    String msg = "Click 'Generate Random' to create data";
    FontMetrics fm = g.getFontMetrics();
    int msgW = fm.stringWidth(msg);
    g.drawString(msg, (w - msgW) / 2, h / 2);
  }

  private void drawInfoOverlay(Graphics2D g, int n, int w, int h) {
    // Create semi-transparent overlay panel
    g.setColor(new Color(0, 0, 0, 150));
    g.fillRoundRect(10, 10, 320, 80, 10, 10);

    // Algorithm name
    g.setColor(new Color(255, 255, 255));
    g.setFont(new Font("Segoe UI", Font.BOLD, 16));
    g.drawString("Algorithm: " + algorithmName, 20, 35);

    // Stats
    g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    g.setColor(new Color(203, 213, 225));
    g.drawString(String.format("Elements: %d", n), 20, 55);
    g.drawString(String.format("Speed: %d", speed), 150, 55);

    // Status with icon
    if (sorted) {
      g.setColor(new Color(34, 197, 94));
      g.fillOval(20, 65, 10, 10);
      g.setColor(new Color(34, 197, 94));
      g.drawString("✓ Sorted", 35, 75);
    } else {
      g.setColor(new Color(251, 191, 36));
      g.fillOval(20, 65, 10, 10);
      g.setColor(new Color(251, 191, 36));
      g.drawString("● Sorting...", 35, 75);
    }

    // Legend in bottom right
    drawLegend(g, w, h);
  }

  private void drawLegend(Graphics2D g, int w, int h) {
    int legendX = w - 250;
    int legendY = h - 100;

    // Legend background
    g.setColor(new Color(0, 0, 0, 150));
    g.fillRoundRect(legendX, legendY, 230, 85, 10, 10);

    g.setFont(new Font("Segoe UI", Font.BOLD, 12));
    g.setColor(new Color(255, 255, 255));
    g.drawString("Legend", legendX + 10, legendY + 20);

    g.setFont(new Font("Segoe UI", Font.PLAIN, 11));

    // Unsorted
    drawLegendItem(g, legendX + 15, legendY + 35, BAR_START, "Unsorted");

    // Comparing A
    drawLegendItem(g, legendX + 15, legendY + 52, HIGHLIGHT_A_START, "Comparing (A)");

    // Comparing B
    drawLegendItem(g, legendX + 15, legendY + 69, HIGHLIGHT_B_START, "Comparing (B)");

    // Sorted
    drawLegendItem(g, legendX + 130, legendY + 35, SORTED_START, "Sorted");
  }

  private void drawLegendItem(Graphics2D g, int x, int y, Color color, String text) {
    g.setColor(color);
    g.fillRoundRect(x, y - 8, 12, 12, 3, 3);
    g.setColor(new Color(203, 213, 225));
    g.drawString(text, x + 18, y + 2);
  }
}
