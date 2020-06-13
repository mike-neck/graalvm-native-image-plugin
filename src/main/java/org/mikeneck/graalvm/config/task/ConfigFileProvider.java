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
import java.util.Optional;
import java.util.function.Supplier;
import org.gradle.api.internal.provider.DefaultProvider;
import org.jetbrains.annotations.NotNull;

public class ConfigFileProvider extends DefaultProvider<File> {

    private ConfigFileProvider(@NotNull Supplier<Optional<File>> fileSupplier) {
        super(() -> fileSupplier.get().orElse(null));
    }

    @NotNull
    public static ConfigFileProvider fromFile(@NotNull File file) {
        return new ConfigFileProvider(() -> Optional.of(file));
    }

    @NotNull
    public static ConfigFileProvider fromSupplier(@NotNull Supplier<Optional<File>> fileSupplier) {
        return new ConfigFileProvider(fileSupplier);
    }

    @NotNull
    public static ConfigFileProvider fromSupplierNullable(@NotNull Supplier<File> fileSupplier) {
        return new ConfigFileProvider(() -> Optional.ofNullable(fileSupplier.get()));
    }

    @NotNull
    public static ConfigFileProvider fromDirectoryResolving(@NotNull Path mayDirectory, @NotNull String fileName) {
        return new ConfigFileProvider(() -> {
            File file = mayDirectory.resolve(fileName).toFile();
            if (!file.exists()) {
                return Optional.empty();
            }
            return Optional.of(file);
        });
    }

    @NotNull
    public static ConfigFileProvider fromDirectoryResolving(@NotNull File mayDirectory, @NotNull String fileName) {
        return fromDirectoryResolving(mayDirectory.toPath(), fileName);
    }
}
