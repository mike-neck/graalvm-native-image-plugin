/*
 * Copyright 2019 Shinya Mochida
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.nativeimage.NativeImageArguments;
import org.mikeneck.graalvm.nativeimage.NativeImageArgumentsFactory;

public class DefaultNativeImageTask extends DefaultTask implements NativeImageConfig {

    public static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "native-image";

    @NotNull
    @Nested
    private final Property<GraalVmHome> graalVmHome;

    @NotNull
    @Input
    private final Property<String> executableName;

    @NotNull
    @OutputDirectory
    private final DirectoryProperty outputDirectory;

    @NotNull
    @Nested
    private final NativeImageArguments nativeImageArguments;

    @SuppressWarnings("UnstableApiUsage")
    @Inject
    public DefaultNativeImageTask(
            @NotNull Project project,
            @NotNull Property<GraalVmHome> graalVmHome) {
        ObjectFactory objectFactory = project.getObjects();
        ProjectLayout projectLayout = project.getLayout();
        this.graalVmHome = graalVmHome;
        RegularFileProperty jarFile =
                objectFactory.fileProperty()
                .fileProvider(
                project.provider(() ->
                project.getTasks()
                        .withType(Jar.class)
                        .findByName("jar"))
                .flatMap(jar ->
                        project.provider(() ->
                                jar.getOutputs()
                                        .getFiles()
                                        .getSingleFile())));
        Property<String> mainClass = objectFactory.property(String.class);
        this.executableName = objectFactory.property(String.class);
        Property<Configuration>  runtimeClasspath = 
                objectFactory.property(Configuration.class)
                .convention(
                        project.provider(() -> 
                                project.getConfigurations()
                                        .getByName("runtimeClasspath")));
        @NotNull ListProperty<String> additionalArguments = objectFactory.listProperty(String.class);
        this.outputDirectory =
                objectFactory.directoryProperty()
                .value(projectLayout.getBuildDirectory().dir(DEFAULT_OUTPUT_DIRECTORY_NAME));
        NativeImageArgumentsFactory nativeImageArgumentsFactory = NativeImageArgumentsFactory.getInstance();
        this.nativeImageArguments = nativeImageArgumentsFactory.create(
                runtimeClasspath,
                mainClass,
                jarFile,
                this.outputDirectory,
                executableName,
                additionalArguments);
    }

    @TaskAction
    public void createNativeImage() {
        createOutputDirectoryIfNotExisting();
        Path nativeImageCommand = nativeImageCommand();
        getProject().exec(execSpec -> {
            getLogger().info("run native-image binary.");
            execSpec.setExecutable(nativeImageCommand);
            execSpec.args(arguments());
        });
    }

    private Path nativeImageCommand() {
        GraalVmHome graalVmHome = graalVmHome();
        Optional<Path> nativeImage = graalVmHome.nativeImage();
        if (!nativeImage.isPresent()) {
            getLogger().warn("native-image not found in graalVmHome({})", graalVmHome);
            throw new InvalidUserDataException("native-image not found in graalVmHome(" + graalVmHome + ")");
        }
        return nativeImage.get();
    }

    private GraalVmHome graalVmHome() {
        return graalVmHome.get();
    }

    public File outputDirectory() {
        return outputDirectoryPath().toFile();
    }

    private Path outputDirectoryPath() {
        return outputDirectory.map(Directory::getAsFile).map(File::toPath).get();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createOutputDirectoryIfNotExisting() {
        File outputDir = outputDirectory();
        getLogger().info("create output directory if not exists: {}", outputDir);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    private List<String> arguments() {
        return getArguments().getOrElse(Collections.emptyList());
    }

    @Input
    public ListProperty<String> getArguments() {
        Project project = getProject();
        ObjectFactory objects = project.getObjects();
        ListProperty<String> listProperty = objects.listProperty(String.class);
        listProperty.set(project.provider(nativeImageArguments::getArguments));
        return listProperty;
    }

    @NotNull
    @OutputFile
    public Provider<RegularFile> getOutputExecutable() {
        return outputDirectory.file(executableName);
    }

    @Override
    public void setGraalVmHome(String graalVmHome) {
        this.graalVmHome.set(
                getProject().provider(
                        () -> new GraalVmHome(Paths.get(graalVmHome))));
    }

    @Override
    public void setJarTask(Task jarTask) {
        Project project = getProject();
        ProjectLayout projectLayout = project.getLayout();
        Provider<File> jar = 
                project.provider(() ->
                        jarTask.getOutputs().getFiles().getSingleFile());
        Provider<RegularFile> jarFile = projectLayout.file(jar);
        nativeImageArguments.setJarFile(jarFile);
    }

    @Override
    public void setMainClass(String mainClass) {
        Project project = getProject();
        nativeImageArguments.setMainClass(project.provider(() -> mainClass));
    }

    @Override
    public void setExecutableName(String name) {
        this.executableName.set(name);
    }

    @Override
    public void setRuntimeClasspath(Configuration configuration) {
        Project project = getProject();
        nativeImageArguments.setRuntimeClasspath(project.provider(() -> configuration));
    }

    @Override
    public void setOutputDirectory(File directory) {
        Project project = getProject();
        ProjectLayout layout = project.getLayout();
        nativeImageArguments.setOutputDirectory(layout.dir(project.provider(() -> directory)));
    }

    @Override
    public void setOutputDirectory(Path directory) {
        setOutputDirectory(directory.toFile());
    }

    @Override
    public void setOutputDirectory(String directory) {
        Project project = getProject();
        setOutputDirectory(project.file(directory));
    }

    @Override
    public void arguments(String... arguments) {
        Project project = getProject();
        nativeImageArguments.addArguments(project.provider(() -> Arrays.asList(arguments)));
    }
}
