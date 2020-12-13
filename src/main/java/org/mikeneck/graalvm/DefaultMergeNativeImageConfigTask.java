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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.JniConfig;
import org.mikeneck.graalvm.config.MergeableConfig;
import org.mikeneck.graalvm.config.ProxyConfig;
import org.mikeneck.graalvm.config.ReflectConfig;
import org.mikeneck.graalvm.config.ResourceConfig;
import org.mikeneck.graalvm.config.task.ConfigFileConfiguration;
import org.mikeneck.graalvm.config.task.ConfigFileProviders;
import org.mikeneck.graalvm.config.task.DefaultMergeConfigFileWork;
import org.mikeneck.graalvm.config.task.FileInput;
import org.mikeneck.graalvm.config.task.FileOutput;
import org.mikeneck.graalvm.config.task.MergeConfigFileWork;

public class DefaultMergeNativeImageConfigTask extends DefaultTask implements MergeNativeImageConfigTask {

    @NotNull
    private final DirectoryProperty destinationDir;
    @NotNull
    private final ListProperty<File> jniConfigs;
    @NotNull
    private final ListProperty<File> proxyConfigs;
    @NotNull
    private final ListProperty<File> reflectConfigs;
    @NotNull
    private final ListProperty<File> resourceConfigs;

    @Inject
    @SuppressWarnings("UnstableApiUsage")
    public DefaultMergeNativeImageConfigTask(@NotNull Project project) {
        ObjectFactory objects = project.getObjects();
        this.destinationDir = objects.directoryProperty()
                .convention(MergeNativeImageConfigTask.DEFAULT_DESTINATION_DIR.apply(project));
        this.jniConfigs = objects.listProperty(File.class);
        this.proxyConfigs = objects.listProperty(File.class);
        this.reflectConfigs = objects.listProperty(File.class);
        this.resourceConfigs = objects.listProperty(File.class);
    }

    @TaskAction
    public void merge() throws IOException {
        Path destinationDir = this.destinationDir.getAsFile().get().toPath();

        if (!Files.exists(destinationDir)) {
            Files.createDirectories(destinationDir);
        }
        for (MergeConfigFileWork<? extends MergeableConfig<? extends MergeableConfig<?>>> work : createWorks(destinationDir)) {
            getLogger().info("work of {}", work);
            work.run();
        }
    }

    @NotNull
    List<MergeConfigFileWork<? extends MergeableConfig<? extends MergeableConfig<?>>>> createWorks(Path destinationDir) {
        return Arrays.asList(jniConfigs.map(files ->
                            new DefaultMergeConfigFileWork<>(
                                    JniConfig.class,
                                    JniConfig::new,
                                    FileInput.from("jniConfig", files),
                                    FileOutput.to(destinationDir.resolve(JNI_CONFIG_JSON))))
                            .get(),
                    proxyConfigs.map(files ->
                            new DefaultMergeConfigFileWork<>(
                                    ProxyConfig.class,
                                    ProxyConfig::new,
                                    FileInput.from("proxyConfig", files),
                                    FileOutput.to(destinationDir.resolve(PROXY_CONFIG_JSON))))
                            .get(),
                    reflectConfigs.map(files ->
                            new DefaultMergeConfigFileWork<>(
                                    ReflectConfig.class,
                                    ReflectConfig::new,
                                    FileInput.from("reflectConfig", files),
                                    FileOutput.to(destinationDir.resolve(REFLECT_CONFIG_JSON))))
                            .get(),
                    resourceConfigs.map(files ->
                            new DefaultMergeConfigFileWork<>(
                                    ResourceConfig.class,
                                    ResourceConfig::new,
                                    FileInput.from("resourceConfig", files),
                                    FileOutput.to(destinationDir.resolve(RESOURCE_CONFIG_JSON))))
                            .get());
    }

    @Override
    public void destinationDir(@NotNull String destinationDir) {
        File dir = getProject().file(destinationDir);
        destinationDir(dir);
    }

    @Override
    public void destinationDir(@NotNull File destinationDir) {
        this.destinationDir.fileProvider(OutputDirectoryProvider.ofFile(destinationDir));
    }

    @Override
    public void destinationDir(@NotNull Path destinationDir) {
        this.destinationDir.fileProvider(OutputDirectoryProvider.ofPath(destinationDir));
    }

    @Override
    public void destinationDir(@NotNull Provider<Directory> destinationDir) {
        this.destinationDir.fileProvider(destinationDir.map(Directory::getAsFile));
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void fromDirectories(@NotNull Provider<FileCollection> directories) {
        this.jniConfigs.addAll(directories.flatMap(files -> ConfigFileProviders.resolving(files, JNI_CONFIG_JSON)));
        this.proxyConfigs.addAll(directories.flatMap(files -> ConfigFileProviders.resolving(files, PROXY_CONFIG_JSON)));
        this.reflectConfigs.addAll(directories.flatMap(files -> ConfigFileProviders.resolving(files, REFLECT_CONFIG_JSON)));
        this.resourceConfigs.addAll(directories.flatMap(files -> ConfigFileProviders.resolving(files, RESOURCE_CONFIG_JSON)));
    }

    @Override
    public void configFiles(@NotNull Action<ConfigFileConfiguration> action) {
        Configuration config = new Configuration();
        action.execute(config);
    }

    private class Configuration implements ConfigFileConfiguration {

        final DefaultMergeNativeImageConfigTask thisObject = DefaultMergeNativeImageConfigTask.this;

        @Override
        public void addJniConfig(File jniConfig) {
            thisObject.jniConfigs.add(jniConfig);
        }

        @Override
        public void addProxyConfig(File proxyConfig) {
            thisObject.proxyConfigs.add(proxyConfig);
        }

        @Override
        public void addReflectConfig(File reflectConfig) {
            thisObject.reflectConfigs.add(reflectConfig);
        }

        @Override
        public void addResourceConfig(File resourceConfig) {
            thisObject.resourceConfigs.add(resourceConfig);
        }
    }

    @Override
    @OutputDirectory
    public @NotNull DirectoryProperty getDestinationDir() {
        return destinationDir;
    }

    @Override
    @InputFiles
    public @NotNull ListProperty<File> getJniConfigs() {
        return jniConfigs;
    }

    @Override
    @InputFiles
    public @NotNull ListProperty<File> getProxyConfigs() {
        return proxyConfigs;
    }

    @Override
    @InputFiles
    public @NotNull ListProperty<File> getReflectConfigs() {
        return reflectConfigs;
    }

    @Override
    @InputFiles
    public @NotNull ListProperty<File> getResourceConfigs() {
        return resourceConfigs;
    }
}
