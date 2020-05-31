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
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.process.JavaExecSpec;
import org.jetbrains.annotations.NotNull;

public class JavaExecutionImpl implements JavaExecution, Action<JavaExecSpec> {

    final int index;
    private final Project project;
    private final Provider<String> mainClass;
    private final Supplier<GraalVmHome> graalVmHome;
    final DirectoryProperty outputDirectory;
    private final Property<InputStream> inputStream;
    final List<String> arguments;
    final Map<String, String> env;

    JavaExecutionImpl(
            int index,
            Project project,
            Provider<String> mainClass,
            Supplier<GraalVmHome> graalVmHome,
            DirectoryProperty outputDirectory,
            Property<InputStream> inputStream) {
        this.index = index;
        this.project = project;
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
    public void stdIn(InputStream input) {
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
                classes(),
                resources(),
                dependencies()
        );
        javaExecSpec.environment(env);
        Path outputDir = outputDirectory.getAsFile().get().toPath();
        javaExecSpec.jvmArgs(String.format("-agentlib:native-image-agent=config-output-dir=%s", outputDir));
        javaExecSpec.setStandardInput(inputStream());
    }

    private FileCollection classes() {
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        SourceSet mainSourceSet = sourceSets.getByName("main");
        return mainSourceSet.getOutput().getClassesDirs();
    }

    private FileCollection resources() {
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        SourceSet mainSourceSet = sourceSets.getByName("main");
        File resourcesDir = mainSourceSet.getOutput().getResourcesDir();
        return project.files(resourcesDir);
    }

    private FileCollection dependencies() {
        return project.getConfigurations()
                .getByName("runtimeClasspath")
                .getAsFileTree();
    }

    private InputStream inputStream() {
        ByteArrayInputStream defaultInputStream = new ByteArrayInputStream(new byte[0]);
        return inputStream.getOrElse(defaultInputStream);
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
