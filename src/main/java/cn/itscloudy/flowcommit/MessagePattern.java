package cn.itscloudy.flowcommit;

import cn.itscloudy.flowcommit.step.CommitStep;
import cn.itscloudy.flowcommit.step.InputStep;
import cn.itscloudy.flowcommit.step.RadioStep;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessagePattern {
    private static final String[] DEF_SEGMENTS = new String[]{
            "[NAME]Name/姓名:Unknown",
            " ",
            "[TYPE]Type/类型:refactor fix feat chore doc test style",
            ": ",
            "[MESSAGE]Message/信息:Unknown",
    };

    private final List<Segment> segments = new ArrayList<>();
    @Getter
    private final List<CommitStep> steps = new ArrayList<>();
    private final StepCacheResolver cacheResolver;

    MessagePattern(StepCacheResolver cacheResolver) {
        this.cacheResolver = cacheResolver;
        for (String seg : DEF_SEGMENTS) {
            String segKey = findSegKey(seg);
            if (segKey != null) {
                resolveStep(segKey, seg.substring(segKey.length() + 2));
            } else {
                segments.add(new TextSegment(seg));
            }
        }
    }

    private String findSegKey(String seg) {
        if (seg.startsWith("[")) {
            int segKeyEnd = seg.indexOf("]");
            if (segKeyEnd > 0) {
                return seg.substring(1, segKeyEnd);
            }
        }
        return null;
    }

    private void resolveStep(String key, String seg) {
        String defaultValue = cacheResolver.get(key);
        String[] nameOptionSplit = seg.split(":");
        String[] nameSplit = nameOptionSplit[0].split("/");
        String lang = Locale.getDefault().getLanguage().equals("zh") ? "zh" : "en";
        String name = nameSplit[0];
        if (lang.equals("zh")) {
            name = nameSplit.length > 1 ? nameSplit[1] : nameSplit[0];
        }

        String[] options = nameOptionSplit[1].split(" ");
        CommitStep commitStep;
        if (options.length > 1) {
            commitStep = new RadioStep(key, name, options, defaultValue == null ? options[0] : defaultValue);
        } else if (options.length == 1) {
            commitStep = new InputStep(key, name, defaultValue == null ? options[0] : defaultValue);
        } else {
            commitStep = new InputStep(key, name, defaultValue == null ? "Unknown" : defaultValue);
        }
        steps.add(commitStep);
        segments.add(commitStep.getStepSegment());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Segment segment : segments) {
            sb.append(segment.getSegmentValue());
        }
        return sb.toString().trim();
    }

    private static class TextSegment implements Segment {
        private final String value;

        TextSegment(String value) {
            this.value = value;
        }

        @Override
        public String getSegmentValue() {
            return value;
        }
    }
}
