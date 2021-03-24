package org.mikeneck.graalvm;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFiles;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.task.ConfigFileConfiguration;

public interface NativeImageConfigurationFiles extends ConfigFileConfiguration {

  void fromMergeTask(@NotNull String mergeNativeImageConfigTask);

  default void fromMergeTask(@NotNull MergeNativeImageConfigTask mergeNativeImageConfigTask) {
    getJniConfigs().builtBy(mergeNativeImageConfigTask);
    getProxyConfigs().builtBy(mergeNativeImageConfigTask);
    getReflectConfigs().builtBy(mergeNativeImageConfigTask);
    getResourceConfigs().builtBy(mergeNativeImageConfigTask);
    getSerializationConfigs().builtBy(mergeNativeImageConfigTask);

    DirectoryProperty directory = mergeNativeImageConfigTask.getDestinationDir();

    addJniConfig(traverse(directory.file(MergeNativeImageConfigTask.JNI_CONFIG_JSON)));
    addProxyConfig(traverse(directory.file(MergeNativeImageConfigTask.PROXY_CONFIG_JSON)));
    addReflectConfig(traverse(directory.file(MergeNativeImageConfigTask.REFLECT_CONFIG_JSON)));
    addResourceConfig(traverse(directory.file(MergeNativeImageConfigTask.RESOURCE_CONFIG_JSON)));
    addSerializationConfig(
        traverse(directory.file(MergeNativeImageConfigTask.SERIALIZATION_CONFIG_JSON)));
  }

  void addJniConfig(@NotNull RegularFileProperty file);

  void addProxyConfig(@NotNull RegularFileProperty file);

  void addReflectConfig(@NotNull RegularFileProperty file);

  void addResourceConfig(@NotNull RegularFileProperty file);

  void addSerializationConfig(@NotNull RegularFileProperty file);

  RegularFileProperty traverse(@NotNull Provider<RegularFile> provider);

  @InputFiles
  ConfigurableFileCollection getJniConfigs();

  @InputFiles
  ConfigurableFileCollection getProxyConfigs();

  @InputFiles
  ConfigurableFileCollection getReflectConfigs();

  @InputFiles
  ConfigurableFileCollection getResourceConfigs();

  @InputFiles
  ConfigurableFileCollection getSerializationConfigs();
}
