package com.study.gradle.tooling.api;


import org.junit.Test;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

public class GradleConnectorTest {
    public static final String CURRENT_PROJECT_DIR = ".";
    private String gradleInstallationDir = "C:\\softwares\\gradle-2.1-all\\gradle-2.1";
    private String projectDir = "./src/main/resources/gradleModule";
    private GradleConnector connector = new GradleConnector(gradleInstallationDir, projectDir);

    @Test
    public void getGradleVersion() throws IOException, InterruptedException {
        String version = connector.getGradleVersion();

        assertEquals("2.1",version);
    }


    @Test
    public void buildProject(){
        boolean builtSuccessfully = connector.buildProject();
        assertTrue(builtSuccessfully);
    }


    @Test
    public void buildProjectWithSpecificTasks(){
        boolean builtSuccessfully = connector.buildProject("clean", "build", "war");
        assertTrue(builtSuccessfully);
    }


    @Test
    public void getGradleTasksForProject(){
        List<String> tasks = connector.getGradleTaskNames();
        List<String> expected = Arrays.asList("assemble","build","buildDependents",
                "buildNeeded","check","classes","clean","cleanIdea","cleanIdeaModule",
                "cleanIdeaProject","cleanIdeaWorkspace","compileJava","compileTestJava",
                "copyDepJars","idea","ideaModule","ideaProject","ideaWorkspace","jar","javadoc"
                ,"processResources","processTestResources","test","testClasses","war");

         assertEquals(expected, tasks);
    }


    @Test
    public void getDependenciesForProject(){
        List<String> dependencies = connector.getProjectDependencyNames();
        List<String> expected = Arrays.asList("spring-core-4.2.3.RELEASE.jar",
                "spring-rabbit-1.5.2.RELEASE.jar","commons-logging-1.2.jar",
                "spring-messaging-4.2.2.RELEASE.jar","spring-retry-1.1.2.RELEASE.jar",
                "spring-amqp-1.5.2.RELEASE.jar","spring-tx-4.2.2.RELEASE.jar",
                "http-client-1.0.0.RELEASE.jar","spring-web-4.2.2.RELEASE.jar",
                "amqp-client-3.5.6.jar","spring-context-4.2.2.RELEASE.jar",
                "spring-beans-4.2.2.RELEASE.jar","httpclient-4.3.6.jar",
                "jackson-databind-2.5.1.jar","spring-aop-4.2.2.RELEASE.jar",
                "spring-expression-4.2.2.RELEASE.jar","httpcore-4.3.3.jar",
                "commons-codec-1.6.jar","jackson-annotations-2.5.0.jar",
                "jackson-core-2.5.1.jar","aopalliance-1.0.jar");

        assertEquals(expected, dependencies);
    }

    @Test
    public void communicateWithCurrentProject() {
        GradleConnector connector = new GradleConnector(gradleInstallationDir, CURRENT_PROJECT_DIR);

        assertTrue(connector.buildProject());
        assertTrue(connector.buildProject("clean", "compileJava"));
        assertEquals(Arrays.asList("junit-4.4.jar", "gradle-core-2.1.jar",
                "gradle-tooling-api-2.1.jar", "gradle-resources-2.1.jar",
                "gradle-messaging-2.1.jar", "gradle-base-services-2.1.jar",
                "gradle-wrapper-2.1.jar", "guava-jdk5-17.0.jar",
                "slf4j-api-1.7.5.jar"), connector.getProjectDependencyNames());

        List<String> expected = Arrays.asList("assemble", "build", "buildDependents",
                "buildNeeded", "check", "classes", "clean", "cleanIdea", "cleanIdeaModule",
                "cleanIdeaProject", "cleanIdeaWorkspace", "compileJava", "compileTestJava",
                "idea", "ideaModule", "ideaProject", "ideaWorkspace", "jar", "javadoc"
                , "processResources", "processTestResources", "test", "testClasses");

        assertEquals(expected, connector.getGradleTaskNames());
    }

}
