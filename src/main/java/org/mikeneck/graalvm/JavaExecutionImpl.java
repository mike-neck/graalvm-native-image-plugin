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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.process.JavaExecSpec;
import org.jetbrains.annotations.NotNull;

public class JavaExecutionImpl implements JavaExecution, Action<JavaExecSpec> {

    final int index;
    final Provider<FileCollection> classes;
    final Provider<FileCollection> resources;
    final Provider<FileCollection> dependencies;
    private final Provider<String> mainClass;
    private final Supplier<GraalVmHome> graalVmHome;
    final File outputDirectory;
    private final Property<byte[]> inputStream;
    final List<String> arguments;
    final Map<String, String> env;

    JavaExecutionImpl(
            int index,
            Project project,
            Provider<String> mainClass,
            Supplier<GraalVmHome> graalVmHome,
            File outputDirectory,
            Property<byte[]> inputStream) {
        this.index = index;
        this.classes = classes(project);
        this.resources = resources(project);
        this.dependencies = dependencies(project);
        this.mainClass = mainClass;
        this.graalVmHome = graalVmHome;
        this.outputDirectory = outputDirectory;
        this.inputStream = inputStream;
        this.arguments = new ArrayList<>();
        this.env = new HashMap<>();
    }

    @Override
    public void arguments(Iterable<String> args) {
        for (String arg : args) {
            arguments.add(arg);
        }
    }

    @Override
    public void stdIn(byte[] input) {
        inputStream.set(input);
    }

    @Override
    public void environment(Map<String, String> env) {
        this.env.putAll(env);
    }

    @Override
    public void execute(@NotNull JavaExecSpec javaExecSpec) {
        javaExecSpec.setIgnoreExitValue(true);
        if (!arguments.isEmpty()) {
            javaExecSpec.args(arguments);
        }
        Path javaExecutable = graalVmHome.get().javaExecutable().orElseThrow(() -> new IllegalStateException("GraalVM Java not found"));
        javaExecSpec.setExecutable(javaExecutable);
        javaExecSpec.setMain(mainClass.get());
        javaExecSpec.classpath(
                classes,
                resources,
                dependencies
        );
        javaExecSpec.environment(env);
        Path outputDir = outputDirectory.toPath();
        javaExecSpec.jvmArgs(String.format("-agentlib:native-image-agent=config-output-dir=%s", outputDir));
        javaExecSpec.setStandardInput(inputStream());
    }

    private static Provider<FileCollection> classes(Project project) {
        return project.provider(() -> {
            SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
            SourceSet mainSourceSet = sourceSets.getByName("main");
            return mainSourceSet.getOutput().getClassesDirs();
        });
    }

    private static Provider<FileCollection> resources(Project project) {
        return project.provider(() -> {
            SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
            SourceSet mainSourceSet = sourceSets.getByName("main");
            File resourcesDir = mainSourceSet.getOutput().getResourcesDir();
            return project.files(resourcesDir);
        });
    }

    private static Provider<FileCollection> dependencies(Project project) {
        return project.provider(() -> project.getConfigurations()
                .getByName("runtimeClasspath")
                .getAsFileTree());
    }

    private InputStream inputStream() {
        ByteArrayInputStream defaultInputStream = new ByteArrayInputStream(new byte[0]);
        return inputStream
                .map(ByteArrayInputStream::new)
                .getOrElse(defaultInputStream);
    }

    @Internal
    @Deprecated
    public int getIndex() {
        return index;
    }

    @InputFiles
    @Deprecated
    public Provider<FileCollection> getClasses() {
        return classes;
    }

    @InputFiles
    @Deprecated
    public Provider<FileCollection> getResources() {
        return resources;
    }

    @InputFiles
    @Deprecated
    public Provider<FileCollection> getDependencies() {
        return dependencies;
    }

    @Input
    @Deprecated
    public Provider<String> getMainClass() {
        return mainClass;
    }

    @Input
    @Deprecated
    public Supplier<GraalVmHome> getGraalVmHome() {
        return graalVmHome;
    }

    @OutputDirectory
    @Deprecated
    public File getOutputDirectory() {
        return outputDirectory;
    }

    @Input
    @Deprecated
    public Property<byte[]> getInputStream() {
        return inputStream;
    }

    @Input
    @Deprecated
    public List<String> getArguments() {
        return arguments;
    }

    @Input
    @Deprecated
    public Map<String, String> getEnv() {
        return env;
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("execution {");
        sb.append("index=").append(index);
        sb.append(", outputDir='").append(outputDirectory).append("'");
        sb.append(", arguments=").append(arguments);
        sb.append('}');
        return sb.toString();
    }
}
