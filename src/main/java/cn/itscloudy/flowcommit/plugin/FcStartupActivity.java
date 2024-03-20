package cn.itscloudy.flowcommit.plugin;

import cn.itscloudy.flowcommit.HomePage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class FcStartupActivity implements StartupActivity{

    private boolean firstRun = true;

    @Override
    public void runActivity(@NotNull Project project) {
        if (firstRun) {
            init();
            firstRun = false;
        }
        FcProjectInfo.afterProjectOpen(project);
    }

    private void init() {
        FcProjectInfo.POST_STARTUP_TASKS.add(p -> {
            HomePage homePage = p.getHomePage();
            if (homePage != null) {
                homePage.afterProjectLoaded();
            }
        });
    }
}
