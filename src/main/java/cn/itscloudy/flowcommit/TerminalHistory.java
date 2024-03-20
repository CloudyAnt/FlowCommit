package cn.itscloudy.flowcommit;

import cn.itscloudy.flowcommit.util.SwingUtil;
import cn.itscloudy.flowcommit.util.WrapEditorKit;
import com.intellij.ui.JBColor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TerminalHistory {
    private static final Font DEF_FONT = new Font(null, Font.PLAIN, 11);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Getter
    private JPanel root;
    private JTextPane historyArea;
    private JScrollPane scroller;
    private JButton clear;
    private String currentText = "";

    TerminalHistory() {
        historyArea.setEditorKit(WrapEditorKit.getInstance());
        historyArea.setContentType("text/html");
        historyArea.setEditable(false);
        historyArea.setOpaque(false);

        clear.addActionListener(e -> {
            historyArea.setText("");
            currentText = "";
        });

        historyArea.setBorder(null);
        SwingUtil.addButtonGlobalLister(clear);
    }

    private void appendNewText(Text text) {
        String content = text.getContent()
                .replace("\n", "<br>")
                .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
                .replace(" ", "&nbsp;")
                .replace("\r", "");
        String t = "<p style=\"" + text.getType().getDefaultCss() + "\">" + content + "</p>";
        currentText += t;
        historyArea.setText("<html><body>" + currentText + "</body></html>");
        scrollToBottom();
    }

    private void scrollToBottom() {
        JViewport viewport = scroller.getViewport();
        Rectangle viewRect = viewport.getViewRect();
        viewRect.y = historyArea.getPreferredSize().height - viewRect.height;
        viewport.scrollRectToVisible(viewRect);
    }

    public void addCommand(String command) {
        add("(" + DTF.format(LocalDateTime.now()) + ") $ " + command, TextType.COMMAND);
    }

    public void addOutput(String output) {
        add(output, TextType.OUTPUT);
    }

    public void addError(String error) {
        add(error, TextType.ERROR);
    }

    private synchronized void add(String content, TextType type) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        Text text = new Text(type, content);
        appendNewText(text);
    }

    @Getter
    public static class Text {
        private final TextType type;
        private final String content;

        Text(TextType type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    @Getter
    public enum TextType {
        COMMAND(JBColor.GREEN),
        OUTPUT(new JBColor(Color.BLACK, Color.WHITE)),
        ERROR(JBColor.RED);

        private final String defaultCss;

        TextType(Color defaultColor) {
            defaultCss = SwingUtil.fontToCssStyles(DEF_FONT, defaultColor);
        }
    }
}
