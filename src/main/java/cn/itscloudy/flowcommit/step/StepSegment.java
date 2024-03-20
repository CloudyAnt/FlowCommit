package cn.itscloudy.flowcommit.step;

import cn.itscloudy.flowcommit.Segment;
import com.intellij.ui.JBColor;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public abstract class StepSegment implements Segment {
    private static final Color NAME_COLOR = new JBColor(JBColor.decode("#CA8265"), JBColor.decode("#4E94DA"));
    @Getter
    private JPanel root;
    private JLabel nameLabel;
    private JPanel contentPlaceholder;
    private JSeparator separator;
    private final JPanel content;

    StepSegment(String name, JPanel content) {
        this.content = content;
        this.nameLabel.setText(name);
        nameLabel.setForeground(NAME_COLOR);
    }

    private void createUIComponents() {
        contentPlaceholder = content;
    }

    public void hideSeparator() {
        separator.setVisible(false);
    }
}
