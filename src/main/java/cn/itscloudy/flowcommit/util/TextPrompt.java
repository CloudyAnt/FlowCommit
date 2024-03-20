package cn.itscloudy.flowcommit.util;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * The TextPrompt class will display a prompt over top of a text component when
 * the Document of the text field is empty. The Show property is used to
 * determine the visibility of the prompt.
 * <p>
 * The Font and foreground Color of the prompt will default to those properties
 * of the parent text component. You are free to change the properties after
 * class construction.
 * <a href="http://www.camick.com/java/source/TextPrompt.java">来源</a>
 */
public class TextPrompt extends JLabel implements FocusListener, DocumentListener {
    public enum Show {
        ALWAYS,
        FOCUS_GAINED,
        FOCUS_LOST;


    }
    private final JTextComponent host;
    private final Document document;

    private Show show;
    private boolean showPromptOnce;

    private int focusLost;
    public TextPrompt(String text, JTextComponent host) {
        this(text, host, Show.ALWAYS, true, null);
    }

    public TextPrompt(String text, JTextComponent host, Insets insets) {
        this(text, host, Show.ALWAYS, false, insets);
    }

    public TextPrompt(String text, JTextComponent host, Show show, boolean useCompBorderInsets, Insets insets) {
        this.host = host;
        setShow(show);
        document = host.getDocument();

        setText(text);
        setFont(host.getFont());
        setForeground(host.getForeground());
        if (useCompBorderInsets) {
            setBorder(new EmptyBorder(host.getInsets()));
        } else {
            setBorder(JBUI.Borders.empty(insets.top, insets.left, insets.bottom, insets.right));
        }
        setHorizontalAlignment(JLabel.LEADING);
        setVerticalAlignment(JLabel.TOP);

        host.addFocusListener(this);
        document.addDocumentListener(this);

        host.setLayout(new BorderLayout());
        host.add(this);
        checkForPrompt();
    }

    public void addToHost() {
        host.add(this);
    }

    public void uninstall() {
        host.removeFocusListener(this);
        document.removeDocumentListener(this);
    }

    /**
     * Convenience method to change the alpha value of the current foreground
     * Color to the specifice value.
     *
     * @param alpha value in the range of 0 - 1.0.
     */
    public void changeAlpha(float alpha) {
        changeAlpha((int) (alpha * 255));
    }

    /**
     * Convenience method to change the alpha value of the current foreground
     * Color to the specifice value.
     *
     * @param alpha value in the range of 0 - 255.
     */
    public void changeAlpha(int alpha) {
        alpha = alpha > 255 ? 255 : Math.max(alpha, 0);

        Color foreground = getForeground();
        int red = foreground.getRed();
        int green = foreground.getGreen();
        int blue = foreground.getBlue();

        Color withAlpha = new Color(red, green, blue, alpha);
        super.setForeground(withAlpha);
    }

    /**
     * Convenience method to change the style of the current Font. The style
     * values are found in the Font class. Common values might be:
     * Font.BOLD, Font.ITALIC and Font.BOLD + Font.ITALIC.
     *
     * @param style value representing the the new style of the Font.
     */
    public void changeStyle(int style) {
        setFont(getFont().deriveFont(style));
    }

    /**
     * Get the Show property
     *
     * @return the Show property.
     */
    public Show getShow() {
        return show;
    }

    /**
     * Set the prompt Show property to control when the promt is shown.
     * Valid values are:
     * <p>
     * Show.AWLAYS (default) - always show the prompt
     * Show.Focus_GAINED - show the prompt when the component gains focus
     * (and hide the prompt when focus is lost)
     * Show.Focus_LOST - show the prompt when the component loses focus
     * (and hide the prompt when focus is gained)
     *
     * @param show a valid Show enum
     */
    public void setShow(Show show) {
        this.show = show;
    }

    /**
     * Get the showPromptOnce property
     *
     * @return the showPromptOnce property.
     */
    public boolean getShowPromptOnce() {
        return showPromptOnce;
    }

    /**
     * Show the prompt once. Once the component has gained/lost focus
     * once, the prompt will not be shown again.
     *
     * @param showPromptOnce when true the prompt will only be shown once,
     *                       otherwise it will be shown repeatedly.
     */
    public void setShowPromptOnce(boolean showPromptOnce) {
        this.showPromptOnce = showPromptOnce;
    }

    /**
     * Check whether the prompt should be visible or not. The visibility
     * will change on updates to the Document and on focus changes.
     */
    private void checkForPrompt() {
        //  Text has been entered, remove the prompt

        if (document.getLength() > 0) {
            setVisible(false);
            return;
        }

        //  Prompt has already been shown once, remove it

        if (showPromptOnce && focusLost > 0) {
            setVisible(false);
            return;
        }

        //  Check the Show property and component focus to determine if the
        //  prompt should be displayed.

        if (host.hasFocus()) {
            setVisible(show == Show.ALWAYS
                    || show == Show.FOCUS_GAINED);
        } else {
            setVisible(show == Show.ALWAYS
                    || show == Show.FOCUS_LOST);
        }
    }

//  Implement FocusListener

    public void focusGained(FocusEvent e) {
        checkForPrompt();
    }

    public void focusLost(FocusEvent e) {
        focusLost++;
        checkForPrompt();
    }

//  Implement DocumentListener

    public void insertUpdate(DocumentEvent e) {
        checkForPrompt();
    }

    public void removeUpdate(DocumentEvent e) {
        checkForPrompt();
    }

    public void changedUpdate(DocumentEvent e) {
    }
}