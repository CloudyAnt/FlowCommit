package cn.itscloudy.flowcommit.plugin;

import cn.itscloudy.flowcommit.TerminalHistory;
import cn.itscloudy.flowcommit.HomePage;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.*;
import org.jetbrains.annotations.NotNull;

public class FcToolWindowFactory implements DumbAware, ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setAnchor(ToolWindowAnchor.BOTTOM, null);

        ContentFactory contentFactory = ContentFactory.getInstance();
        HomePage homePage = FcProjectInfo.getInstance(project).getHomePage();
        Content home = contentFactory.createContent(homePage.getRoot(), FcConst.get("tab.home.title"), true);
        home.setCloseable(false);
        toolWindow.getContentManager().addContent(home);

        TerminalHistory terminalHistory = homePage.getTerminalHistory();
        Content history = contentFactory.createContent(terminalHistory.getRoot(), FcConst.get("tab.history.title"),
                true);
        history.setCloseable(false);
        toolWindow.getContentManager().addContent(history);
    }
}
