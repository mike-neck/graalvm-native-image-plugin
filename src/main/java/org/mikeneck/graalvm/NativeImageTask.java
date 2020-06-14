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
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;

public class NativeImageTask extends DefaultTask {

    public static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "native-image";

    @Internal
    private Property<NativeImageExtension> extension;

    @NotNull
    @Input
    private final Provider<String> graalVmHome;

    @NotNull
    @Internal
    private final Provider<Jar> jarTask;

    @NotNull
    @Input
    private final Provider<String> mainClass;

    @NotNull
    @Input
    private final Provider<String> executableName;

    @NotNull
    @Input
    private final Provider<Configuration> runtimeClasspath;

    @NotNull
    @Input
    private final ListProperty<String> additionalArguments;

    @NotNull
    @OutputDirectory
    private final DirectoryProperty outputDirectory;

    

    @SuppressWarnings("UnstableApiUsage")
    @Inject
    public NativeImageTask(
            @NotNull Project project,
            @NotNull Provider<String> graalVmHome) {
        ObjectFactory objectFactory = project.getObjects();
        ProviderFactory providerFactory = project.getProviders();
        ProjectLayout projectLayout = project.getLayout();
        this.graalVmHome = graalVmHome;
        this.jarTask = 
                project.provider(() -> 
                        project.getTasks()
                                .withType(Jar.class)
                                .findByName("jar"));
        this.mainClass = objectFactory.property(String.class);
        this.executableName = objectFactory.property(String.class);
        this.runtimeClasspath = 
                project.provider(() -> 
                        project.getConfigurations()
                                .getByName("runtimeClasspath"));
        this.additionalArguments = objectFactory.listProperty(String.class);
        this.outputDirectory =
                objectFactory.directoryProperty()
                .convention(projectLayout.getBuildDirectory().dir(DEFAULT_OUTPUT_DIRECTORY_NAME));
        
    }

    void setExtension(NativeImageExtension extension) {
        this.extension.set(extension);
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
        return extension.get().graalVmHome();
    }

    @OutputDirectory
    public Provider<Directory> getOutputDirectory() {
        return extension.get().outputDirectory;
    }

    public File outputDirectory() {
        return outputDirectoryPath().toFile();
    }

    private Path outputDirectoryPath() {
        return extension.get().outputDirectory.getAsFile().map(File::toPath).get();
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
        Provider<List<String>> provider = extension
                .map(ext -> NativeImageArguments.create(project, ext))
                .map(NativeImageArguments::getArguments);
        return project.getObjects().listProperty(String.class).convention(provider);
    }

    @InputFile
    public Provider<File> getJarFile() {
        return jarTask
                .map(Task::getOutputs)
                .map(TaskOutputs::getFiles)
                .map(FileCollection::getSingleFile);
    }

    @OutputFile
    public Provider<RegularFile> getOutputExecutable() {
        return outputDirectory.file(executableName);
    }
}
