package cn.itscloudy.flowcommit.step;

import javax.swing.*;

public interface CommitStep {

    StepSegment getStepSegment();

    JPanel getRoot();

    String getKey();
}
