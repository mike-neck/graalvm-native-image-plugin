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
package org.mikeneck.graalvm.nativeimage;

import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFiles;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.NativeImageConfigurationFiles;

@SuppressWarnings("UnstableApiUsage")
public class ConfigurationFiles implements NativeImageConfigurationFiles {

    private final Project project;

    private final ConfigurableFileCollection jniConfigs;
    private final ConfigurableFileCollection proxyConfigs;
    private final ConfigurableFileCollection reflectConfigs;
    private final ConfigurableFileCollection resourceConfigs;

    public ConfigurationFiles(Project project) {
        this.project = project;
        ObjectFactory objects = project.getObjects();
        this.jniConfigs = objects.fileCollection();
        this.proxyConfigs = objects.fileCollection();
        this.reflectConfigs = objects.fileCollection();
        this.resourceConfigs = objects.fileCollection();
    }

    @Override
    public void addJniConfig(@NotNull RegularFileProperty file) {
        jniConfigs.from(file);
    }

    @Override
    public void addProxyConfig(@NotNull RegularFileProperty file) {
        proxyConfigs.from(file);
    }

    @Override
    public void addReflectConfig(@NotNull RegularFileProperty file) {
        reflectConfigs.from(file);
    }

    @Override
    public void addResourceConfig(@NotNull RegularFileProperty file) {
        resourceConfigs.from(file);
    }

    @Override
    public RegularFileProperty traverse(@NotNull Provider<RegularFile> provider) {
        RegularFileProperty file = project.getObjects().fileProperty();
        return file.convention(provider);
    }

    @Override
    public void addJniConfig(File jniConfig) {
        RegularFileProperty file = project.getObjects().fileProperty();
        file.set(jniConfig);
        addJniConfig(file);
    }

    @Override
    public void addProxyConfig(File proxyConfig) {
        RegularFileProperty file = project.getObjects().fileProperty();
        file.set(proxyConfig);
        addJniConfig(file);
    }

    @Override
    public void addReflectConfig(File reflectConfig) {
        RegularFileProperty file = project.getObjects().fileProperty();
        file.set(reflectConfig);
        addJniConfig(file);
    }

    @Override
    public void addResourceConfig(File resourceConfig) {
        RegularFileProperty file = project.getObjects().fileProperty();
        file.set(resourceConfig);
        addJniConfig(file);
    }

    @InputFiles
    public ConfigurableFileCollection getJniConfigs() {
        return jniConfigs;
    }

    @InputFiles
    public ConfigurableFileCollection getProxyConfigs() {
        return proxyConfigs;
    }

    @InputFiles
    public ConfigurableFileCollection getReflectConfigs() {
        return reflectConfigs;
    }

    @InputFiles
    public ConfigurableFileCollection getResourceConfigs() {
        return resourceConfigs;
    }
}
