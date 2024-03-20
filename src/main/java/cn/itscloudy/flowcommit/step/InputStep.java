package cn.itscloudy.flowcommit.step;

import cn.itscloudy.flowcommit.util.TextPrompt;
import com.intellij.ui.JBColor;
import lombok.Getter;

import javax.swing.*;

public class InputStep implements CommitStep {
    private JTextField field;
    @Getter
    private JPanel root;
    @Getter
    private final String key;
    private final String name;
    private final String defaultValue;
    private StepSegmentImp stepSegment;

    public InputStep(String key, String name, String defaultValue) {
        this.key = key;
        this.name = name;
        this.defaultValue = defaultValue;
        TextPrompt tp = new TextPrompt(" " + defaultValue, field);
        tp.setForeground(JBColor.GRAY);
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
            String text = field.getText();
            if (text.isEmpty()) {
                return defaultValue;
            }
            return text;
        }
    }
}
