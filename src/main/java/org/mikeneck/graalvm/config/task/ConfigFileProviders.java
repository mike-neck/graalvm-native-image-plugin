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
package org.mikeneck.graalvm.config.task;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.provider.DefaultProvider;
import org.jetbrains.annotations.NotNull;

public class ConfigFileProviders extends DefaultProvider<List<File>> {

    ConfigFileProviders(Callable<? extends List<File>> value) {
        super(value);
    }

    @NotNull
    public static ConfigFileProviders resolving(@NotNull FileCollection files, @NotNull String fileName) {
        return new ConfigFileProviders(() -> files.getFiles()
                .stream()
                .map(File::toPath)
                .map(path -> path.resolve(fileName))
                .map(Path::toFile)
                .collect(Collectors.toList()));
    }
}
