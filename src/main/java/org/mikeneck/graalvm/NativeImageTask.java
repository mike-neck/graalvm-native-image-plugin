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

import org.gradle.api.DefaultTask;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class NativeImageTask extends DefaultTask {

    private Property<NativeImageExtension> extension;

    public NativeImageTask() {
        this.extension = getProject().getObjects().property(NativeImageExtension.class);
    }

    public void setExtension(NativeImageExtension extension) {
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

    private File outputDirectory() {
        Project project = getProject();
        return project.getBuildDir().toPath().resolve("native-image").toFile();
    }

    private void createOutputDirectoryIfNotExisting() {
        File outputDir = outputDirectory();
        getLogger().info("create output directory if not exists: {}", outputDir);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    private Collection<File> runtimeClasspath() {
        return extension.get().runtimeClasspath.get().getFiles();
    }

    private File jarFile() {
        return extension.get().jarTask.get().getOutputs().getFiles().getSingleFile();
    }

    private List<String> arguments() {
        List<String> args = new ArrayList<>();
        args.add("-cp");
        args.add(classpath());
        args.add("-H:Path=" + outputDirectory().getAbsolutePath());
        if (extension.get().executableName.isPresent()) {
            args.add("-H:Name=" + extension.get().executableName.get());
        }
        if (extension.get().additionalArguments.isPresent()) {
            args.addAll(extension.get().additionalArguments.get());
        }
        args.add(extension.get().mainClass.get());
        return Collections.unmodifiableList(args);
    }

    private String classpath() {
        List<File> paths = new ArrayList<>(runtimeClasspath());
        paths.add(jarFile());
        return paths.stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(":"));
    }

    @OutputFile
    public File getOutputExecutable() {
        return outputDirectory().toPath().resolve(extension.get().executableName.get()).toFile();
    }
}
