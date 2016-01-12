package com.study.gradle.tooling.api;


import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.GradleTask;
import org.gradle.tooling.model.idea.IdeaDependency;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency;
import org.gradle.util.GradleVersion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GradleConnector {
    private org.gradle.tooling.GradleConnector connector;

    public GradleConnector(String gradleInstallationDir, String projectDir) {
        File gradleInstallationDir1 = new File(gradleInstallationDir);
        connector = org.gradle.tooling.GradleConnector.newConnector();
        connector.useInstallation(gradleInstallationDir1);
        connector.forProjectDirectory(new File(projectDir));
    }

    public String getGradleVersion() {
        return GradleVersion.current().getVersion();
    }

    public List<String> getGradleTaskNames() {
        List<String> taskNames = new ArrayList<>();
        List<GradleTask> tasks = getGradleTasks();
        return tasks.stream().map(task->task.getName()).collect(Collectors.toList());
    }

    public List<GradleTask> getGradleTasks() {
        List<GradleTask> tasks = new ArrayList<>();
        ProjectConnection connection = connector.connect();
        try {
            GradleProject project = connection.getModel(GradleProject.class);
                for(GradleTask task : project.getTasks()) {
                    tasks.add(task);
                }
        } finally {
            connection.close();
        }
        return tasks;
    }

    public boolean buildProject(String... tasks) {
        ProjectConnection connection = connector.connect();
        BuildLauncher build = connection.newBuild();

        if(executeSpecificTasks(tasks)) {
            build.forTasks(tasks);
        }

        buildProject(connection, build);

        return true;
    }

    private void buildProject(ProjectConnection connection, BuildLauncher build) {
        try {
            build.run();
        }finally {
            connection.close();
        }
    }

    private boolean executeSpecificTasks(String[] tasks) {
        return tasks.length >0;
    }

    public List<String> getProjectDependencies() {
        return getDependencies().stream()
                                .map(file -> file.getName())
                                .collect(Collectors.toList());

    }

    public List<File> getDependencies() {
        List<File> dependencyFiles = new ArrayList<>();
        ProjectConnection connection = connector.connect();
        try{
            IdeaProject project = connection.getModel(IdeaProject.class);
            for(IdeaModule module : project.getModules()){
                for(IdeaDependency dependency:   module.getDependencies()){
                    IdeaSingleEntryLibraryDependency ideaDependency = (IdeaSingleEntryLibraryDependency) dependency;
                    File file = ideaDependency.getFile();
                    dependencyFiles.add(file);
                }
            }
        }finally {
            connection.close();
        }
        return dependencyFiles;
    }


}
