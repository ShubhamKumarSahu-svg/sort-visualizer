package constants;

import java.awt.Color;
import java.awt.Font;

public class UIConstants {
  // Color Scheme
  public static final Color BG_COLOR = new Color(241, 245, 249);
  public static final Color PANEL_BG = new Color(255, 255, 255);
  public static final Color TEXT_PRIMARY = new Color(15, 23, 42);
  public static final Color TEXT_SECONDARY = new Color(100, 116, 139);
  public static final Color BORDER_COLOR = new Color(226, 232, 240);

  // Visualization Colors
  public static final Color VIZ_BG_COLOR = new Color(15, 23, 42);
  public static final Color GRID_COLOR = new Color(30, 41, 59, 60);
  public static final Color BAR_START = new Color(99, 102, 241);
  public static final Color BAR_END = new Color(139, 92, 246);
  public static final Color HIGHLIGHT_A_START = new Color(239, 68, 68);
  public static final Color HIGHLIGHT_A_END = new Color(220, 38, 38);
  public static final Color HIGHLIGHT_B_START = new Color(34, 197, 94);
  public static final Color HIGHLIGHT_B_END = new Color(22, 163, 74);
  public static final Color SORTED_START = new Color(16, 185, 129);
  public static final Color SORTED_END = new Color(5, 150, 105);

  // Button Colors
  public static final Color START_BTN_COLOR = new Color(16, 185, 129);
  public static final Color START_BTN_HOVER = new Color(5, 150, 105);
  public static final Color STOP_BTN_COLOR = new Color(239, 68, 68);
  public static final Color STOP_BTN_HOVER = new Color(220, 38, 38);
  public static final Color COMPARE_BTN_COLOR = new Color(59, 130, 246);
  public static final Color COMPARE_BTN_ACTIVE = new Color(139, 92, 246);
  public static final Color BACK_BTN_COLOR = new Color(100, 116, 139);
  public static final Color BACK_BTN_HOVER = new Color(71, 85, 105);

  // Fonts
  public static final Font FONT_REGULAR = new Font("Inter", Font.PLAIN, 13);
  public static final Font FONT_BOLD = new Font("Inter", Font.BOLD, 13);
  public static final Font FONT_SMALL = new Font("Inter", Font.PLAIN, 11);
  public static final Font FONT_LARGE = new Font("Inter", Font.BOLD, 17);

  // Dimensions
  public static final int MIN_WIDTH = 1150;
  public static final int MIN_HEIGHT = 750;
  public static final int BUTTON_WIDTH = 150;
  public static final int BUTTON_HEIGHT = 38;

  private UIConstants() {
    // Prevent instantiation
  }
}
