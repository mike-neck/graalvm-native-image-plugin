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
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public interface FileInput {

    Reader newReader(Charset charset) throws IOException;

    @NotNull
    static List<FileInput> of(@NotNull List<File> files) {
        return files.stream()
                .map(FileInput::of)
                .collect(Collectors.toList());
    }

    @NotNull
    static FileInput of(@NotNull File file) {
        return of(file.toPath());
    }

    @NotNull
    static FileInput of(@NotNull Path file) {
        return charset -> Files.newBufferedReader(file, charset);
    }
}
