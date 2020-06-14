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

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

class FileInputImpl implements FileInput {

    private final String resourceGroupName;
    private final Path path;

    FileInputImpl(String resourceGroupName, Path path) {
        this.resourceGroupName = resourceGroupName;
        this.path = path;
    }

    @Override
    public Reader newReader(Charset charset) throws IOException {
        return Files.newBufferedReader(path, charset);
    }

    @Override
    public String toString() {
        @SuppressWarnings("StringBufferReplaceableByString")
        final StringBuilder sb = new StringBuilder("FileInput[")
                .append(resourceGroupName)
                .append('(')
                .append(path)
                .append(')')
                .append(']');
        return sb.toString();
    }
}
