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
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenerateNativeImageConfigTask extends DefaultTask {

    @NotNull
    private final Property<NativeImageExtension> extension;

    @Internal
    @NotNull
    private final Property<Boolean> exitOnApplicationError;

    @OutputDirectory
    @NotNull
    private final DirectoryProperty temporaryDirectory;

    private final List<JavaExecutionImpl> javaExecutions;

    @SuppressWarnings("UnstableApiUsage")
    @Inject
    public GenerateNativeImageConfigTask(Project project) {
        ObjectFactory objectFactory = project.getObjects();
        this.extension = objectFactory.property(NativeImageExtension.class);
        this.exitOnApplicationError = objectFactory.property(Boolean.class);
        this.temporaryDirectory = objectFactory.directoryProperty();
        this.javaExecutions = new ArrayList<>();

        this.exitOnApplicationError.set(true);
        File buildDir = project.getBuildDir();
        this.temporaryDirectory.set(buildDir.toPath().resolve("tmp/native-image-config").toFile());
    }

    @TaskAction
    public void generateConfig() {
        Project project = getProject();
        Logger logger = getLogger();
        if (javaExecutions.isEmpty()) {
            logger.debug("setting default execution, because no execution configured");
            javaExecutions.add(newJavaExecution(0));
        }

        Path temporaryDirectory = this.temporaryDirectory.get().getAsFile().toPath();
        createDirectory(temporaryDirectory);

        for (JavaExecutionImpl javaExecution : javaExecutions) {
            Path outputDirectory = javaExecution.outputDirectory.get().getAsFile().toPath();
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

    public void setExitOnApplicationError(boolean exitOnApplicationError) {
        this.exitOnApplicationError.set(exitOnApplicationError);
    }

    public boolean getExitOnApplicationError() {
        return this.exitOnApplicationError.getOrElse(true);
    }

    public void resumeOnApplicationError() {
        this.exitOnApplicationError.set(false);
    }

    void setNativeImageExtension(@NotNull NativeImageExtension extension) {
        this.extension.set(extension);
    }

    @Nested
    public List<JavaExecutionImpl> getJavaExecutions() {
        return javaExecutions;
    }

    /**
     * getter of temporary output directory
     * @return output directory
     * @deprecated this is intended to be used by Gradle.
     */
    @Deprecated
    @SuppressWarnings("unused")
    @Nullable
    public File getTemporaryDirectory() {
        return temporaryDirectory.getAsFile().getOrNull();
    }

    Supplier<GraalVmHome> graalVmHome() {
        return () -> extension.get().graalVmHome();
    }

    public void runApplicationWithoutArguments() {
        JavaExecutionImpl javaExecution = newJavaExecution(javaExecutions.size());
        javaExecutions.add(javaExecution);
    }

    public void runApplication(Action<JavaExecution> argumentsConfiguration) {
        JavaExecutionImpl javaExecution = newJavaExecution(javaExecutions.size());
        argumentsConfiguration.execute(javaExecution);
        javaExecutions.add(javaExecution);
    }

    @SuppressWarnings("UnstableApiUsage")
    @NotNull
    private JavaExecutionImpl newJavaExecution(int index) {
        Project project = getProject();
        ObjectFactory objectFactory = project.getObjects();
        DirectoryProperty outputDirectory = objectFactory.directoryProperty();
        Provider<Directory> directoryProvider = temporaryDirectory.map(it -> it.dir("out-" + index));
        outputDirectory.set(directoryProvider);
        Property<byte[]> stdIn = objectFactory.property(byte[].class);
        stdIn.set(new byte[0]);
        return new JavaExecutionImpl(
                index,
                project,
                mainClass(),
                graalVmHome(),
                outputDirectory,
                stdIn);
    }

    @SuppressWarnings("UnstableApiUsage")
    private Provider<String> mainClass() {
        return extension.flatMap(ext -> ext.mainClass);
    }

}
