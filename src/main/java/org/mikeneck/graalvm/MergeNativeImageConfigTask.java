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
import java.nio.file.Path;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.task.ConfigFileConfiguration;
import org.mikeneck.graalvm.config.task.ConfigFileProvider;

public class MergeNativeImageConfigTask extends DefaultTask {

    private static final String JNI_CONFIG_JSON = "jni-config.json";
    private static final String PROXY_CONFIG_JSON = "proxy-config.json";
    private static final String REFLECT_CONFIG_JSON = "reflect-config.json";
    private static final String RESOURCE_CONFIG_JSON = "resource-config.json";

    private final DirectoryProperty destinationDir;

    private final ListProperty<File> jniConfigs;
    private final ListProperty<File> proxyConfigs;
    private final ListProperty<File> reflectConfigs;
    private final ListProperty<File> resourceConfigs;

    @SuppressWarnings("UnstableApiUsage")
    public MergeNativeImageConfigTask(@NotNull Project project) {
        ObjectFactory objects = project.getObjects();
        this.destinationDir = objects.directoryProperty();
        this.jniConfigs = objects.listProperty(File.class);
        this.proxyConfigs = objects.listProperty(File.class);
        this.reflectConfigs = objects.listProperty(File.class);
        this.resourceConfigs = objects.listProperty(File.class);
    }

    @TaskAction
    public void merge() {
        // TODO run merging works
    }

    public void destinationDir(String destinationDir) {
        File dir = getProject().file(destinationDir);
        destinationDir(dir);
    }

    public void destinationDir(File destinationDir) {
        this.destinationDir.fileProvider(OutputDirectoryProvider.ofFile(destinationDir));
    }

    public void destinationDir(Path destinationDir) {
        this.destinationDir.fileProvider(OutputDirectoryProvider.ofPath(destinationDir));
    }

    public void fromDirectories(File... directories) {
        for (File directory : directories) {
            fromDirectory(directory);
        }
    }

    public void fromDirectory(File directory) {
        jniConfigs.add(ConfigFileProvider.fromDirectoryResolving(directory, JNI_CONFIG_JSON));
        proxyConfigs.add(ConfigFileProvider.fromDirectoryResolving(directory, PROXY_CONFIG_JSON));
        reflectConfigs.add(ConfigFileProvider.fromDirectoryResolving(directory, REFLECT_CONFIG_JSON));
        resourceConfigs.add(ConfigFileProvider.fromDirectoryResolving(directory, RESOURCE_CONFIG_JSON));
    }

    public void fromDirectories(Path... directories) {
        for (Path directory : directories) {
            fromDirectory(directory);
        }
    }

    public void fromDirectory(Path directory) {
        jniConfigs.add(ConfigFileProvider.fromDirectoryResolving(directory, JNI_CONFIG_JSON));
        proxyConfigs.add(ConfigFileProvider.fromDirectoryResolving(directory, PROXY_CONFIG_JSON));
        reflectConfigs.add(ConfigFileProvider.fromDirectoryResolving(directory, REFLECT_CONFIG_JSON));
        resourceConfigs.add(ConfigFileProvider.fromDirectoryResolving(directory, RESOURCE_CONFIG_JSON));
    }

    public void fromDirectories(Provider<File>... directories) {
        for (Provider<File> directory : directories) {
            fromDirectory(directory);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public void fromDirectory(Provider<File> directory) {
        jniConfigs.add(directory.flatMap(dir -> ConfigFileProvider.fromDirectoryResolving(dir, JNI_CONFIG_JSON)));
        proxyConfigs.add(directory.flatMap(dir -> ConfigFileProvider.fromDirectoryResolving(dir, PROXY_CONFIG_JSON)));
        reflectConfigs.add(directory.flatMap(dir -> ConfigFileProvider.fromDirectoryResolving(dir, REFLECT_CONFIG_JSON)));
        resourceConfigs.add(directory.flatMap(dir -> ConfigFileProvider.fromDirectoryResolving(dir, RESOURCE_CONFIG_JSON)));
    }

    public void configFiles(Action<ConfigFileConfiguration> action) {
        Configuration config = new Configuration();
        action.execute(config);
    }

    private class Configuration implements ConfigFileConfiguration {

        final MergeNativeImageConfigTask thisObject = MergeNativeImageConfigTask.this;

        @NotNull
        @Override
        public Provider<File> provider(File file) {
            return getProject().provider(() -> file);
        }

        @Override
        public void addJniConfig(Provider<File> jniConfig) {
            thisObject.jniConfigs.add(jniConfig);
        }

        @Override
        public void addProxyConfig(Provider<File> proxyConfig) {
            thisObject.proxyConfigs.add(proxyConfig);
        }

        @Override
        public void addReflectConfig(Provider<File> reflectConfig) {
            thisObject.reflectConfigs.add(reflectConfig);
        }

        @Override
        public void addResourceConfig(Provider<File> resourceConfig) {
            thisObject.resourceConfigs.add(resourceConfig);
        }
    }

    @OutputDirectory
    public DirectoryProperty getDestinationDir() {
        return destinationDir;
    }

    @InputFiles
    public ListProperty<File> getJniConfigs() {
        return jniConfigs;
    }

    @InputFiles
    public ListProperty<File> getProxyConfigs() {
        return proxyConfigs;
    }

    @InputFiles
    public ListProperty<File> getReflectConfigs() {
        return reflectConfigs;
    }

    @InputFiles
    public ListProperty<File> getResourceConfigs() {
        return resourceConfigs;
    }
}
