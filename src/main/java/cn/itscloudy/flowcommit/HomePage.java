package cn.itscloudy.flowcommit;

import cn.itscloudy.flowcommit.plugin.FcConst;
import cn.itscloudy.flowcommit.plugin.FcNotifier;
import cn.itscloudy.flowcommit.plugin.FcProjectInfo;
import cn.itscloudy.flowcommit.step.CommitStep;
import cn.itscloudy.flowcommit.step.StepSegment;
import cn.itscloudy.flowcommit.util.CmdUtil;
import cn.itscloudy.flowcommit.util.CollectorElite;
import cn.itscloudy.flowcommit.util.SwingUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.components.panels.VerticalLayout;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePage {
    @Getter
    private JPanel root;
    private JPanel steps;
    private JLabel fatalNoticeLabel;
    private JButton commitButton;
    private JTextArea infoArea;
    private JScrollPane infoScroller;
    private JPanel fatalNoticePage;
    private JButton refreshButton;
    private JButton addAllButton;
    private JPanel extraControls;
    private JPanel content;
    private JButton resetButton;

    private final Project project;
    private final String basePath;
    private final StepCacheResolver stepCacheResolver;
    private final MessagePattern messagePattern;
    @Getter
    private final TerminalHistory terminalHistory;

    public HomePage(Project project) {
        terminalHistory = new TerminalHistory();
        this.project = project;
        this.basePath = project.getBasePath();
        if (basePath == null) {
            throw new IllegalStateException("Project base path is null");
        }
        this.stepCacheResolver = StepCacheResolver.getInstance(basePath);
        messagePattern = new MessagePattern(stepCacheResolver);

        steps.setLayout(new VerticalLayout(10));
        update(false);
        commitButton.addActionListener(e -> commit());
        refreshButton.addActionListener(e -> update(true));
        addAllButton.addActionListener(e -> {
            refreshVfs();
            runGitCommand("add", "-A");
            update(true);
        });
        resetButton.addActionListener(e -> {
            refreshVfs();
            runGitCommand("reset");
            update(true);
        });

        SwingUtil.addButtonGlobalLister(commitButton);
        SwingUtil.addButtonGlobalLister(refreshButton);
        SwingUtil.addButtonGlobalLister(addAllButton);

        content.setVisible(false);
        updateSteps();
    }

    private static void refreshVfs() {
        ApplicationManager.getApplication().invokeAndWait(() -> {
            FileDocumentManager.getInstance().saveAllDocuments();
            VirtualFileManager.getInstance().syncRefresh();
        }, ModalityState.defaultModalityState());
    }

    public void afterProjectLoaded() {
        update(true);
        content.setVisible(true);
        fatalNoticePage.setVisible(false);
    }

    private void update(boolean updateVfs) {
        if (updateVfs) {
            refreshVfs();
        }
        if (isReadyToCommit()) {
            updateSteps();
            CmdUtil.Result statusResult = runGitCommand("status");
            if (StringUtils.isNotBlank(statusResult.getError())) {
                FcNotifier.error(project, statusResult.getError());
            } else {
                infoArea.setText(statusResult.getOutput());
            }
        }
    }

    private void showFatalNotice(String notice) {
        fatalNoticeLabel.setText(notice);
        fatalNoticePage.setVisible(true);
        content.setVisible(false);
    }

    private void updateSteps() {
        steps.removeAll();
        boolean first = true;
        for (CommitStep step : messagePattern.getSteps()) {
            StepSegment stepSegment = step.getStepSegment();
            if (first) {
                stepSegment.hideSeparator();
                first = false;
            }
            steps.add(stepSegment.getRoot());
        }
        root.updateUI();
    }

    private boolean isReadyToCommit() {
        boolean ready = true;
        if (FcProjectInfo.getInstance(project).isLoaded()) {
            CmdUtil.Result insideWorkTreeResult = runGitCommand("rev-parse", "--is-inside-work-tree");
            if (!"true".equals(insideWorkTreeResult.getOutput())) {
                infoArea.setText(FcConst.get("notice.notAGitRepo"));
                ready = false;
            }
            if (ready) {
                String obstacle = findObstacle(basePath);
                if (obstacle != null) {
                    showFatalNotice(FcConst.get("notice.obstacle").replace("{process}", obstacle));
                    ready = false;
                }

            }
            if (ready && hasNothingToCommit()) {
                infoArea.setText(FcConst.get("notice.nothingToCommit"));
                ready = false;
            }
        } else {
            showFatalNotice(FcConst.get("notice.projectLoading"));
            ready = false;
        }
        return ready;
    }

    private boolean hasNothingToCommit() {
        CmdUtil.Result diff = runGitCommand("diff", "--cached", "--quiet", "--exit-code");
        return diff.getExitValue() == 0;
    }

    private String findObstacle(String basePath) {
        String process = null;
        Path gitDirPath = Path.of(basePath, ".git");
        if (gitDirPath.resolve("MERGE_HEAD").toFile().isFile()) {
            process = "Merge";
        }
        if (gitDirPath.resolve("rebase-apply").toFile().isDirectory()
                || gitDirPath.resolve("rebase-merge").toFile().isDirectory()
                || gitDirPath.resolve("rebasing").toFile().isDirectory()) {
            process = "Rebase";
        }
        if (gitDirPath.resolve("CHERRY_PICK_HEAD").toFile().isFile()) {
            process = "Cherry-pick";
        }
        if (gitDirPath.resolve("REVERT_HEAD").toFile().isFile()) {
            process = "Revert";
        }
        return process;
    }

    private void commit() {
        if (hasNothingToCommit()) {
            FcNotifier.info(project, FcConst.get("notice.nothingToCommit"));
            return;
        }
        String message = messagePattern.toString();
        CmdUtil.Result result = runGitCommand("commit", "-m", message);
        if (StringUtils.isNotBlank(result.getError())) {
            FcNotifier.error(project, result.getError());
        } else {
            FcNotifier.info(project, FcConst.get("commitSuccess"), message);
            stepCacheResolver.update(messagePattern.getSteps());
        }
        update(true);
    }

    private CmdUtil.Result runGitCommand(String... params) {
        try {
            List<String> parts = new ArrayList<>();
            parts.add("git");
            parts.add("-C");
            parts.add(basePath);
            parts.addAll(Arrays.asList(params));
            terminalHistory.addCommand(CollectorElite.join(parts, " "));
            CmdUtil.Result result = CmdUtil.run(parts);
            if (StringUtils.isNotBlank(result.getOutput())) {
                terminalHistory.addOutput(result.getOutput());
            }
            if (StringUtils.isNotBlank(result.getError())) {
                terminalHistory.addError(result.getError());
            }
            return result;
        } catch (IOException e) {
            return new CmdUtil.Result(null, e.getMessage(), 1);
        }
    }
}
