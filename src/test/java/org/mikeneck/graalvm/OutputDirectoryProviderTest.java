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
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import org.gradle.api.InvalidUserDataException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

class OutputDirectoryProviderTest {

    private static void assertThrowsInvalidUserDataException(String description, Callable<Object> operation) {
        try {
            operation.call();
            fail(description);
        } catch (Exception e) {
            assertThat(description, e, instanceOf(InvalidUserDataException.class));
        }
    }

    @Test void nullDirectory() {
        OutputDirectoryProvider file = OutputDirectoryProvider.ofFile(null);
        assertThrowsInvalidUserDataException("null File object", file::calculateValue);
    }

    @Test void nullPath() {
        OutputDirectoryProvider path = OutputDirectoryProvider.ofPath(null);
        assertThrowsInvalidUserDataException("null Path object", path::calculateValue);
    }

    @Test void existingFileFile() {
        OutputDirectoryProvider file = OutputDirectoryProvider.ofFile(new File("README.md"));
        assertThrowsInvalidUserDataException("existing file File", file::calculateValue);
    }

    @Test void existingFilePath() {
        OutputDirectoryProvider path = OutputDirectoryProvider.ofPath(Paths.get("README.md"));
        assertThrowsInvalidUserDataException("existing file Path", path::calculateValue);
    }

    @Test void existingDirectoryFile() {
        File src = new File("src");
        OutputDirectoryProvider provider = OutputDirectoryProvider.ofFile(src);
        File file = provider.calculateValue().get();
        assertThat(file, is(src));
    }

    @Test void existingDirectoryPath() {
        Path src = Paths.get("src");
        OutputDirectoryProvider provider = OutputDirectoryProvider.ofPath(src);
        File file = provider.calculateValue().get();
        assertThat(file, is(src.toFile()));
    }

    @Test void notExistingFile() {
        File foo = new File("foo");
        OutputDirectoryProvider provider = OutputDirectoryProvider.ofFile(foo);
        File file = provider.calculateValue().get();
        assertThat(file, is(foo));
    }

    @Test void notExistingPath() {
        Path foo = Paths.get("foo");
        OutputDirectoryProvider provider = OutputDirectoryProvider.ofPath(foo);
        File file = provider.calculateValue().get();
        assertThat(file, is(foo.toFile()));
    }
}
