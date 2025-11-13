package ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import constants.UIConstants;

public class UIComponentFactory {

  public static JComboBox<String> createStyledComboBox(String[] items) {
    JComboBox<String> combo = new JComboBox<>(items);
    combo.setFont(UIConstants.FONT_REGULAR);
    combo.setBackground(UIConstants.PANEL_BG);
    combo.setForeground(UIConstants.TEXT_PRIMARY);
    combo.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
        BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    return combo;
  }

  public static JSlider createStyledSlider(int min, int max, int value) {
    JSlider slider = new JSlider(min, max, value);
    slider.setBackground(UIConstants.PANEL_BG);
    slider.setForeground(UIConstants.COMPARE_BTN_COLOR);
    return slider;
  }

  public static JCheckBox createStyledCheckBox(String text, boolean selected) {
    JCheckBox checkBox = new JCheckBox(text, selected);
    checkBox.setFont(UIConstants.FONT_REGULAR);
    checkBox.setBackground(UIConstants.PANEL_BG);
    checkBox.setForeground(UIConstants.TEXT_PRIMARY);
    checkBox.setFocusPainted(false);
    return checkBox;
  }

  public static JToggleButton createStyledToggleButton(String text) {
    JToggleButton button = new JToggleButton(text);
    button.setFont(UIConstants.FONT_BOLD);
    button.setBackground(UIConstants.COMPARE_BTN_COLOR);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setOpaque(true);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(UIConstants.BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT));
    button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    return button;
  }

  public static JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
    JButton button = new JButton(text);
    button.setFont(UIConstants.FONT_BOLD);
    button.setBackground(bgColor);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setOpaque(true);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(UIConstants.BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT));
    button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

    button.addMouseListener(new java.awt.event.MouseAdapter() {
      private final Color originalColor = bgColor;
      private final Color hover = hoverColor;

      public void mouseEntered(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          button.setBackground(hover);
        }
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        button.setBackground(originalColor);
      }
    });

    return button;
  }

  public static JLabel createValueLabel() {
    JLabel label = new JLabel();
    label.setFont(new Font("Inter", Font.BOLD, 12));
    label.setForeground(UIConstants.COMPARE_BTN_COLOR);
    label.setPreferredSize(new Dimension(40, 20));
    return label;
  }

  public static JPanel createSliderPanel(String title, JSlider slider, JLabel valueLabel) {
    JPanel panel = new JPanel(new BorderLayout(10, 8));
    panel.setBackground(UIConstants.PANEL_BG);

    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(UIConstants.PANEL_BG);

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(UIConstants.FONT_REGULAR);
    titleLabel.setForeground(UIConstants.TEXT_PRIMARY);

    headerPanel.add(titleLabel, BorderLayout.WEST);
    headerPanel.add(valueLabel, BorderLayout.EAST);

    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(slider, BorderLayout.CENTER);

    return panel;
  }

  private UIComponentFactory() {
    // Prevent instantiation
  }
}
