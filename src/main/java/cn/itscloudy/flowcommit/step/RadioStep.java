package cn.itscloudy.flowcommit.step;

import com.intellij.ui.JBColor;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class RadioStep implements CommitStep {
    private static final Color[] colors = new Color[]{
            JBColor.RED, JBColor.GREEN, JBColor.YELLOW, JBColor.BLUE, JBColor.MAGENTA, JBColor.CYAN, JBColor.ORANGE
    };

    @Getter
    private JPanel root;
    @Getter
    private final String key;
    private final String name;
    private final String defaultValue;
    private final ButtonGroup group;
    private StepSegmentImp stepSegment;

    public RadioStep(String key, String name, String[] radioNames, String defaultValue) {
        this.key = key;
        this.name = name;
        this.defaultValue = defaultValue;
        root.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        group = new ButtonGroup();

        boolean hasDefault = false;
        for (int i = 0; i < radioNames.length; i++) {
            String radioName = radioNames[i];
            JRadioButton radio = addRadio(i, radioName, group);
            if (radioName.equals(defaultValue)) {
                hasDefault = true;
                radio.setSelected(true);
            }
        }
        if (!hasDefault) {
            group.getElements().nextElement().setSelected(true);
        }
    }

    private JRadioButton addRadio(int index, String name, ButtonGroup group) {
        JRadioButton radio = new JRadioButton(name);
        radio.setForeground(colors[index >= colors.length ? colors.length - 1 : index]);
        radio.setActionCommand(name);
        root.add(radio);
        group.add(radio);
        return radio;
    }

    public StepSegment getStepSegment() {
        if (stepSegment == null) {
            stepSegment = new StepSegmentImp();
        }
        return stepSegment;
    }

    private class StepSegmentImp extends StepSegment {

        StepSegmentImp() {
            super(name, root);
        }

        @Override
        public String getSegmentValue() {
            return group.getSelection().getActionCommand();
        }
    }
}
