/*
 * Copyright 2020 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.graalvm;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;
import org.jetbrains.annotations.NotNull;

public class DefaultGenerateNativeImageConfigTask extends DefaultTask implements GenerateNativeImageConfigTask {

    @NotNull
    @Nested
    private final Property<GraalVmHome> graalVmHome;

    @Internal
    @NotNull
    private final Property<Boolean> exitOnApplicationError;

    @NotNull
    @Input
    private final Property<String> mainClass;

    @OutputDirectory
    @NotNull
    private final File temporaryDirectory;

    private final List<JavaExecutionImpl> javaExecutions;

    @NotNull
    private final Property<Configuration> runtimeClasspath;

    @NotNull
    private final ConfigurableFileCollection jarFile;

    @Inject
    public DefaultGenerateNativeImageConfigTask(
            @NotNull Project project,
            @NotNull Property<GraalVmHome> graalVmHome,
            @NotNull Property<String> mainClass,
            @NotNull Property<Configuration> runtimeClasspath,
            @NotNull ConfigurableFileCollection jarFile) {
        ObjectFactory objectFactory = project.getObjects();
        this.graalVmHome = objectFactory.property(GraalVmHome.class);
        this.exitOnApplicationError = objectFactory.property(Boolean.class);
        this.runtimeClasspath = runtimeClasspath;
        this.jarFile = jarFile;
        this.mainClass = mainClass;
        this.javaExecutions = new ArrayList<>();

        this.graalVmHome.set(graalVmHome);
        this.exitOnApplicationError.set(true);
        File buildDir = project.getBuildDir();
        this.temporaryDirectory = buildDir.toPath().resolve("tmp/native-image-config").toFile();
    }

    @TaskAction
    public void generateConfig() {
        Project project = getProject();
        Logger logger = getLogger();
        if (javaExecutions.isEmpty()) {
            logger.debug("setting default execution, because no execution configured");
            javaExecutions.add(newJavaExecution(0));
        }

        Path temporaryDirectory = this.temporaryDirectory.toPath();
        createDirectory(temporaryDirectory);

        for (JavaExecutionImpl javaExecution : javaExecutions) {
            Path outputDirectory = javaExecution.outputDirectory.toPath();
            createDirectory(outputDirectory);

            logger.debug("execution java: {}", javaExecution);
            ExecResult execResult = project.javaexec(javaExecution);
            if (execResult.getExitValue() == 0) {
                logger.lifecycle(
                        "({})succeeded execution for argument: {}, environment: {}",
                        javaExecution.index, javaExecution.arguments, javaExecution.env);
            } else {
                logger.lifecycle(
                        "({})failed execution for argument: {}, environment: {}",
                        javaExecution.index, javaExecution.arguments, javaExecution.env);
            }
        }
    }

    private void createDirectory(Path directory) {
        Logger logger = getLogger();
        logger.debug("creating temporary directory: {}", directory);
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("failed to create directory: %s.", directory), e);
        }
    }

    @Override
    public void setGraalVmHome(@NotNull Path path) {
        Project project = getProject();
        graalVmHome.set(project.provider(() -> new GraalVmHome(path)));
    }

    @Override
    public void setGraalVmHome(@NotNull Provider<GraalVmHome> graalVmHome) {
        this.graalVmHome.set(graalVmHome);
    }

    @Override
    @Nested
    @NotNull
    public Property<GraalVmHome> getGraalVmHome() {
        return graalVmHome;
    }

    @Override
    public void setExitOnApplicationError(boolean exitOnApplicationError) {
        this.exitOnApplicationError.set(exitOnApplicationError);
    }

    @Override
    public boolean getExitOnApplicationError() {
        return this.exitOnApplicationError.getOrElse(true);
    }

    private Provider<String> mainClass() {
        return this.mainClass;
    }

    @Override
    @Input
    @NotNull
    public Provider<String> getMainClass() {
        return mainClass;
    }

    public void resumeOnApplicationError() {
        this.exitOnApplicationError.set(false);
    }

    @Override
    @NotNull
    @Nested
    public List<JavaExecutionImpl> getJavaExecutions() {
        return javaExecutions;
    }

    /**
     * getter of temporary output directory
     * @return output directory
     * @deprecated this is intended to be used by Gradle.
     */
    @Override
    @Deprecated
    @OutputDirectory
    @NotNull
    public File getTemporaryDirectory() {
        return temporaryDirectory;
    }

    Supplier<GraalVmHome> graalVmHome() {
        return graalVmHome::get;
    }

    public void byRunningApplicationWithoutArguments() {
        JavaExecutionImpl javaExecution = newJavaExecution(javaExecutions.size());
        javaExecutions.add(javaExecution);
    }

    public void byRunningApplication(Action<JavaExecution> argumentsConfiguration) {
        JavaExecutionImpl javaExecution = newJavaExecution(javaExecutions.size());
        argumentsConfiguration.execute(javaExecution);
        javaExecutions.add(javaExecution);
    }

    @NotNull
    private JavaExecutionImpl newJavaExecution(int index) {
        Project project = getProject();
        ObjectFactory objectFactory = project.getObjects();
        File outputDirectory = temporaryDirectory.toPath().resolve("out-" + index).toFile();
        Property<byte[]> stdIn = objectFactory.property(byte[].class);
        stdIn.set(new byte[0]);
        return new JavaExecutionImpl(
                index,
                jarFile,
                runtimeClasspath, mainClass(),
                graalVmHome(),
                outputDirectory,
                stdIn);
    }
}
