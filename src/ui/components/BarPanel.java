package ui.components;

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

import constants.UIConstants;
import util.TimeFormatter;

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

  public BarPanel() {
    setBackground(UIConstants.VIZ_BG_COLOR);
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
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    int w = getWidth();
    int h = getHeight();
    drawGrid(g, w, h);

    if (values == null || values.length == 0) {
      drawEmptyMessage(g, w, h);
      return;
    }

    drawBars(g, w, h);

    if (isComparisonMode) {
      drawComparisonInfo(g, values.length, w, h);
    } else {
      drawInfoOverlay(g, values.length, w, h);
    }
  }

  private void drawGrid(Graphics2D g, int w, int h) {
    g.setColor(UIConstants.GRID_COLOR);
    int gridSpacing = 50;
    for (int y = 0; y < h; y += gridSpacing) {
      g.drawLine(0, y, w, y);
    }
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

  private void drawBars(Graphics2D g, int w, int h) {
    int n = values.length;
    double barW = Math.max(1, (double) w / n);
    int gap = n > 100 ? 0 : 1;
    int bottomMargin = isComparisonMode ? 70 : 80;

    for (int i = 0; i < n; i++) {
      double ratio = values[i] / (double) maxVal;
      int barHeight = (int) Math.max(3, ratio * (h - bottomMargin));
      int x = (int) Math.floor(i * barW);
      int y = h - barHeight - (isComparisonMode ? 15 : 20);
      int barWidth = (int) Math.ceil(barW) - gap;

      Color startColor = getBarStartColor(i);
      Color endColor = getBarEndColor(i);

      GradientPaint gradient = new GradientPaint(x, y, startColor, x, y + barHeight, endColor);
      g.setPaint(gradient);

      if (barWidth > 3) {
        g.fillRoundRect(x, y, barWidth, barHeight, 4, 4);
      } else {
        g.fillRect(x, y, barWidth, barHeight);
      }

      if (barHeight > 10) {
        g.setColor(new Color(255, 255, 255, 35));
        if (barWidth > 3) {
          g.fillRoundRect(x, y, barWidth, Math.min(barHeight / 3, 20), 4, 4);
        } else {
          g.fillRect(x, y, barWidth, Math.min(barHeight / 3, 20));
        }
      }
    }
  }

  private Color getBarStartColor(int index) {
    if (sorted) {
      return UIConstants.SORTED_START;
    } else if (index == highlightA) {
      return UIConstants.HIGHLIGHT_A_START;
    } else if (index == highlightB) {
      return UIConstants.HIGHLIGHT_B_START;
    } else {
      return UIConstants.BAR_START;
    }
  }

  private Color getBarEndColor(int index) {
    if (sorted) {
      return UIConstants.SORTED_END;
    } else if (index == highlightA) {
      return UIConstants.HIGHLIGHT_A_END;
    } else if (index == highlightB) {
      return UIConstants.HIGHLIGHT_B_END;
    } else {
      return UIConstants.BAR_END;
    }
  }

  private void drawComparisonInfo(Graphics2D g, int n, int w, int h) {
    g.setColor(new Color(30, 41, 59, 230));
    g.fillRoundRect(10, h - 60, w - 20, 50, 10, 10);
    g.setColor(new Color(71, 85, 105, 100));
    g.drawRoundRect(10, h - 60, w - 20, 50, 10, 10);

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

    g.setFont(new Font("Inter", Font.PLAIN, 11));
    g.setColor(new Color(203, 213, 225));
    String stats = String.format("C: %d | S: %d | T: %s", comparisons, swaps,
        TimeFormatter.formatTime(elapsedSeconds));
    g.drawString(stats, 20, h - 20);
  }

  private void drawInfoOverlay(Graphics2D g, int n, int w, int h) {
    g.setColor(new Color(30, 41, 59, 230));
    g.fillRoundRect(15, 15, 380, 110, 12, 12);
    g.setColor(new Color(71, 85, 105, 100));
    g.drawRoundRect(15, 15, 380, 110, 12, 12);

    g.setColor(new Color(248, 250, 252));
    g.setFont(new Font("Inter", Font.BOLD, 17));
    g.drawString(algorithmName, 30, 42);

    g.setFont(new Font("Inter", Font.PLAIN, 13));
    g.setColor(new Color(203, 213, 225));

    int statY = 67;
    int lineHeight = 20;
    g.drawString("Elements: " + n, 30, statY);
    g.drawString("Speed: " + speed, 180, statY);
    g.drawString("Comparisons: " + comparisons, 30, statY + lineHeight);
    g.drawString("Swaps: " + swaps, 180, statY + lineHeight);
    g.drawString("Time: " + TimeFormatter.formatTime(elapsedSeconds), 30, statY + lineHeight * 2);

    drawStatusIndicator(g, 290, statY + lineHeight * 2 - 12);
    drawLegend(g, w, h);
  }

  private void drawStatusIndicator(Graphics2D g, int x, int y) {
    if (sorted) {
      g.setColor(new Color(16, 185, 129));
      g.fillOval(x, y, 12, 12);
      g.setFont(new Font("Inter", Font.BOLD, 13));
      g.drawString("✓ Sorted", x + 18, y + 10);
    } else if (sorting) {
      g.setColor(new Color(251, 191, 36));
      g.fillOval(x, y, 12, 12);
      g.setFont(new Font("Inter", Font.BOLD, 13));
      g.drawString("● Sorting", x + 18, y + 10);
    } else {
      g.setColor(new Color(100, 116, 139));
      g.fillOval(x, y, 12, 12);
      g.setColor(new Color(148, 163, 184));
      g.setFont(new Font("Inter", Font.BOLD, 13));
      g.drawString("○ Ready", x + 18, y + 10);
    }
  }

  private void drawLegend(Graphics2D g, int w, int h) {
    int legendX = w - 260;
    int legendY = h - 115;
    g.setColor(new Color(30, 41, 59, 230));
    g.fillRoundRect(legendX, legendY, 245, 100, 12, 12);
    g.setColor(new Color(71, 85, 105, 100));
    g.drawRoundRect(legendX, legendY, 245, 100, 12, 12);

    g.setFont(new Font("Inter", Font.BOLD, 13));
    g.setColor(new Color(248, 250, 252));
    g.drawString("Legend", legendX + 15, legendY + 25);

    g.setFont(new Font("Inter", Font.PLAIN, 12));
    drawLegendItem(g, legendX + 20, legendY + 45, UIConstants.BAR_START, "Unsorted");
    drawLegendItem(g, legendX + 20, legendY + 65, UIConstants.HIGHLIGHT_A_START, "Comparing A");
    drawLegendItem(g, legendX + 20, legendY + 85, UIConstants.HIGHLIGHT_B_START, "Comparing B");
    drawLegendItem(g, legendX + 140, legendY + 45, UIConstants.SORTED_START, "Sorted");
  }

  private void drawLegendItem(Graphics2D g, int x, int y, Color color, String text) {
    g.setColor(color);
    g.fillRoundRect(x, y - 9, 14, 14, 4, 4);
    g.setColor(new Color(203, 213, 225));
    g.drawString(text, x + 20, y + 2);
  }
}
