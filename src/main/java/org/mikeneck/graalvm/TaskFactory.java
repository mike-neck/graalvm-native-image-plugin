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

import java.nio.file.Paths;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.jetbrains.annotations.NotNull;

public class TaskFactory {

    @NotNull
    private final Project project;
    @NotNull
    private final Property<GraalVmHome> graalVmHome;
    @NotNull
    private final Property<Configuration> runtimeClasspath;
    @NotNull
    private final Property<SourceSet> mainSourceSet;

    @SuppressWarnings("UnstableApiUsage")
    TaskFactory(@NotNull Project project) {
        ObjectFactory objectFactory = project.getObjects();
        ProviderFactory providerFactory = project.getProviders();

        this.project = project;
        this.graalVmHome = objectFactory
                .property(GraalVmHome.class)
                .convention(
                        providerFactory
                                .environmentVariable("JAVA_HOME")
                                .map(Paths::get)
                                .map(GraalVmHome::new));
        this.runtimeClasspath = objectFactory
                .property(Configuration.class)
                .convention(project.provider(
                        () -> project
                                .getConfigurations()
                                .getByName("runtimeClasspath")));
        this.mainSourceSet = 
                objectFactory.property(SourceSet.class)
                        .convention(project.provider(
                                () -> project.getExtensions()
                                        .getByType(SourceSetContainer.class)
                                        .getByName("main")));
    }
}
