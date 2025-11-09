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
  private volatile boolean sorting = false;
  private volatile boolean sorted = false;
  private int speed = 60;
  private String algorithmName = "Bubble Sort";
  private volatile long comparisons = 0;
  private volatile long swaps = 0;
  private volatile long elapsedSeconds = 0;
  private boolean isComparisonMode = false;
  private String panelLabel = "";

  // Modern color scheme - Dark theme
  private static final Color BG_COLOR = new Color(15, 23, 42);
  private static final Color GRID_COLOR = new Color(30, 41, 59, 60);

  // Gradient colors for bars
  private static final Color BAR_START = new Color(99, 102, 241);
  private static final Color BAR_END = new Color(139, 92, 246);

  private static final Color HIGHLIGHT_A_START = new Color(239, 68, 68);
  private static final Color HIGHLIGHT_A_END = new Color(220, 38, 38);

  private static final Color HIGHLIGHT_B_START = new Color(34, 197, 94);
  private static final Color HIGHLIGHT_B_END = new Color(22, 163, 74);

  private static final Color SORTED_START = new Color(16, 185, 129);
  private static final Color SORTED_END = new Color(5, 150, 105);

  public BarPanel() {
    setBackground(BG_COLOR);
    setDoubleBuffered(true);
  }

  public void setComparisonMode(boolean mode, String label) {
    this.isComparisonMode = mode;
    this.panelLabel = label;
  }

  public void setValues(int[] v) {
    this.values = Arrays.copyOf(v, v.length);
    this.maxVal = Math.max(1, Arrays.stream(values).max().orElse(1));
    this.sorted = false;
    this.sorting = false;
    clearHighlights();
    resetStats();
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
    this.sorting = true;
    resetStats();
    repaint();
  }

  public void setSortingState(boolean sorting) {
    this.sorting = sorting;
    repaint();
  }

  public void setSortedState() {
    this.sorted = true;
    this.sorting = false;
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

  public void incrementComparisons() {
    this.comparisons++;
  }

  public void incrementSwaps() {
    this.swaps++;
  }

  public void setElapsedTime(long seconds) {
    this.elapsedSeconds = seconds;
  }

  public void resetStats() {
    this.comparisons = 0;
    this.swaps = 0;
    this.elapsedSeconds = 0;
  }

  public long getComparisons() {
    return comparisons;
  }

  public long getSwaps() {
    return swaps;
  }

  public long getElapsedSeconds() {
    return elapsedSeconds;
  }

  @Override
  public Dimension getPreferredSize() {
    if (isComparisonMode) {
      return new Dimension(500, 420);
    }
    return new Dimension(1000, 600);
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

    // Draw subtle grid
    drawGrid(g, w, h);

    if (values == null || values.length == 0) {
      drawEmptyMessage(g, w, h);
      return;
    }

    int n = values.length;
    double barW = Math.max(1, (double) w / n);
    int gap = n > 100 ? 0 : 1;

    int bottomMargin = isComparisonMode ? 70 : 80;

    // Draw bars with gradients
    for (int i = 0; i < n; i++) {
      double ratio = values[i] / (double) maxVal;
      int barHeight = (int) Math.max(3, ratio * (h - bottomMargin));
      int x = (int) Math.floor(i * barW);
      int y = h - barHeight - (isComparisonMode ? 15 : 20);
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
        g.fillRoundRect(x, y, barWidth, barHeight, 4, 4);
      } else {
        g.fillRect(x, y, barWidth, barHeight);
      }

      // Add highlight effect on top
      if (barHeight > 10) {
        g.setColor(new Color(255, 255, 255, 35));
        if (barWidth > 3) {
          g.fillRoundRect(x, y, barWidth, Math.min(barHeight / 3, 20), 4, 4);
        } else {
          g.fillRect(x, y, barWidth, Math.min(barHeight / 3, 20));
        }
      }
    }

    // Draw info overlay
    if (isComparisonMode) {
      drawComparisonInfo(g, n, w, h);
    } else {
      drawInfoOverlay(g, n, w, h);
    }
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
    g.setFont(new Font("Inter", Font.PLAIN, 16));
    String msg = isComparisonMode ? "Ready to compare" : "Ready to start";
    FontMetrics fm = g.getFontMetrics();
    int msgW = fm.stringWidth(msg);
    g.drawString(msg, (w - msgW) / 2, h / 2);
  }

  private void drawComparisonInfo(Graphics2D g, int n, int w, int h) {
    // Compact info panel for comparison mode
    g.setColor(new Color(30, 41, 59, 230));
    g.fillRoundRect(10, h - 60, w - 20, 50, 10, 10);

    g.setColor(new Color(71, 85, 105, 100));
    g.drawRoundRect(10, h - 60, w - 20, 50, 10, 10);

    // Algorithm name with status
    g.setFont(new Font("Inter", Font.BOLD, 14));
    if (sorted) {
      g.setColor(new Color(16, 185, 129));
      g.drawString("✓ " + algorithmName, 20, h - 38);
    } else if (sorting) {
      g.setColor(new Color(251, 191, 36));
      g.drawString("● " + algorithmName, 20, h - 38);
    } else {
      g.setColor(new Color(148, 163, 184));
      g.drawString("○ " + algorithmName, 20, h - 38);
    }

    // Stats
    g.setFont(new Font("Inter", Font.PLAIN, 11));
    g.setColor(new Color(203, 213, 225));

    String stats = String.format("C: %d | S: %d | T: %s",
        comparisons, swaps, formatTime(elapsedSeconds));
    g.drawString(stats, 20, h - 20);
  }

  private void drawInfoOverlay(Graphics2D g, int n, int w, int h) {
    // Create semi-transparent overlay panel
    g.setColor(new Color(30, 41, 59, 230));
    g.fillRoundRect(15, 15, 380, 110, 12, 12);

    // Add subtle border
    g.setColor(new Color(71, 85, 105, 100));
    g.drawRoundRect(15, 15, 380, 110, 12, 12);

    // Algorithm name
    g.setColor(new Color(248, 250, 252));
    g.setFont(new Font("Inter", Font.BOLD, 17));
    g.drawString(algorithmName, 30, 42);

    // Stats section
    g.setFont(new Font("Inter", Font.PLAIN, 13));
    g.setColor(new Color(203, 213, 225));

    int statY = 67;
    int lineHeight = 20;

    g.drawString("Elements: " + n, 30, statY);
    g.drawString("Speed: " + speed, 180, statY);

    g.drawString("Comparisons: " + comparisons, 30, statY + lineHeight);
    g.drawString("Swaps: " + swaps, 180, statY + lineHeight);

    g.drawString("Time: " + formatTime(elapsedSeconds), 30, statY + lineHeight * 2);

    // Status indicator with proper states
    int statusX = 290;
    int statusY = statY + lineHeight * 2 - 12;

    if (sorted) {
      g.setColor(new Color(16, 185, 129));
      g.fillOval(statusX, statusY, 12, 12);
      g.setColor(new Color(16, 185, 129));
      g.setFont(new Font("Inter", Font.BOLD, 13));
      g.drawString("✓ Sorted", statusX + 18, statusY + 10);
    } else if (sorting) {
      g.setColor(new Color(251, 191, 36));
      g.fillOval(statusX, statusY, 12, 12);
      g.setColor(new Color(251, 191, 36));
      g.setFont(new Font("Inter", Font.BOLD, 13));
      g.drawString("● Sorting", statusX + 18, statusY + 10);
    } else {
      g.setColor(new Color(100, 116, 139));
      g.fillOval(statusX, statusY, 12, 12);
      g.setColor(new Color(148, 163, 184));
      g.setFont(new Font("Inter", Font.BOLD, 13));
      g.drawString("○ Ready", statusX + 18, statusY + 10);
    }

    // Legend in bottom right
    drawLegend(g, w, h);
  }

  private String formatTime(long seconds) {
    if (seconds < 60) {
      return seconds + "s";
    } else {
      long mins = seconds / 60;
      long secs = seconds % 60;
      return String.format("%dm %ds", mins, secs);
    }
  }

  private void drawLegend(Graphics2D g, int w, int h) {
    int legendX = w - 260;
    int legendY = h - 115;

    // Legend background
    g.setColor(new Color(30, 41, 59, 230));
    g.fillRoundRect(legendX, legendY, 245, 100, 12, 12);

    // Add subtle border
    g.setColor(new Color(71, 85, 105, 100));
    g.drawRoundRect(legendX, legendY, 245, 100, 12, 12);

    g.setFont(new Font("Inter", Font.BOLD, 13));
    g.setColor(new Color(248, 250, 252));
    g.drawString("Legend", legendX + 15, legendY + 25);

    g.setFont(new Font("Inter", Font.PLAIN, 12));

    // Unsorted
    drawLegendItem(g, legendX + 20, legendY + 45, BAR_START, "Unsorted");

    // Comparing A
    drawLegendItem(g, legendX + 20, legendY + 65, HIGHLIGHT_A_START, "Comparing A");

    // Comparing B
    drawLegendItem(g, legendX + 20, legendY + 85, HIGHLIGHT_B_START, "Comparing B");

    // Sorted
    drawLegendItem(g, legendX + 140, legendY + 45, SORTED_START, "Sorted");
  }

  private void drawLegendItem(Graphics2D g, int x, int y, Color color, String text) {
    g.setColor(color);
    g.fillRoundRect(x, y - 9, 14, 14, 4, 4);
    g.setColor(new Color(203, 213, 225));
    g.drawString(text, x + 20, y + 2);
  }
}
