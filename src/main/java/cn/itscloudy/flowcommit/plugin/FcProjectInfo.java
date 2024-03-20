package cn.itscloudy.flowcommit.plugin;

import cn.itscloudy.flowcommit.HomePage;
import com.intellij.openapi.project.Project;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class FcProjectInfo {
    private static final Map<Project, FcProjectInfo> MAP = new HashMap<>();
    static final List<Consumer<FcProjectInfo>> POST_STARTUP_TASKS = new ArrayList<>();
    private final Project project;

    static void afterProjectOpen(Project project) {
        removeDeadProjectInfos();
        getInstance(project).loaded = true;
        POST_STARTUP_TASKS.forEach(task -> task.accept(getInstance(project)));
    }

    private static synchronized void removeDeadProjectInfos() {
        MAP.entrySet().removeIf(entry -> entry.getKey().isDisposed());
    }

    public static synchronized FcProjectInfo getInstance(Project project) {
        return MAP.computeIfAbsent(project, k -> new FcProjectInfo(project));
    }

    private HomePage homePage;
    private boolean loaded;

    private FcProjectInfo(Project project) {
        this.project = project;
        loaded = false;
    }

    public synchronized HomePage getHomePage() {
        if (homePage == null) {
            homePage = new HomePage(project);
        }
        return homePage;
    }
}
