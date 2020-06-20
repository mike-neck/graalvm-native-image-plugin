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

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Provider;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.task.ConfigFileConfiguration;

public interface NativeImageConfigurationFiles extends ConfigFileConfiguration {

    default void fromMergeTask(@NotNull MergeNativeImageConfigTask mergeNativeImageConfigTask) {
        DirectoryProperty directory = mergeNativeImageConfigTask.getDestinationDir();

        addJniConfig(traverse(directory.file(MergeNativeImageConfigTask.JNI_CONFIG_JSON)));
        addProxyConfig(traverse(directory.file(MergeNativeImageConfigTask.PROXY_CONFIG_JSON)));
        addReflectConfig(traverse(directory.file(MergeNativeImageConfigTask.REFLECT_CONFIG_JSON)));
        addResourceConfig(traverse(directory.file(MergeNativeImageConfigTask.RESOURCE_CONFIG_JSON)));
    }

    void addJniConfig(@NotNull RegularFileProperty file);
    void addProxyConfig(@NotNull RegularFileProperty file);
    void addReflectConfig(@NotNull RegularFileProperty file);
    void addResourceConfig(@NotNull RegularFileProperty file);

    RegularFileProperty traverse(@NotNull Provider<RegularFile> provider); 
}
