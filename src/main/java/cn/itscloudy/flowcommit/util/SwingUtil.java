package cn.itscloudy.flowcommit.util;

import javax.swing.*;
import java.awt.*;

public class SwingUtil {
    private SwingUtil() {
    }

    public static String fontToCssStyles(Font font, Color color) {
        String fontName = font.getName();
        String fontStyleStr = "";
        int fontStyle = font.getStyle();
        if (fontStyle == Font.BOLD) {
            fontStyleStr = "bold";
        } else if (fontStyle == Font.ITALIC) {
            fontStyleStr = "italic";
        } else if (fontStyle == (Font.BOLD + Font.ITALIC)) {
            fontStyleStr = "bold italic";
        }
        int fontSize = font.getSize();
        String style = "font-family: " + fontName + "; font-style: " + fontStyleStr + "; font-size: " + fontSize + "pt;";
        if (color != null) {
            style += "color: " + color2Hex(color) + ";";
        }
        return style;
    }

    public static String color2Hex(Color color) {
        if (color != null) {
            return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        }
        return "";
    }

    public static void addButtonGlobalLister(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
}
