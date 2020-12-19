package org.mikeneck.graalvm;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import org.gradle.api.InvalidUserDataException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class OutputDirectoryProviderTest {

    private static void assertThrowsInvalidUserDataException(String description, Callable<Object> operation) {
        try {
            operation.call();
            fail(description);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(InvalidUserDataException.class);
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
        assertThat(file).isEqualTo(src);
    }

    @Test void existingDirectoryPath() {
        Path src = Paths.get("src");
        OutputDirectoryProvider provider = OutputDirectoryProvider.ofPath(src);
        File file = provider.calculateValue().get();
        assertThat(file).isEqualTo(src.toFile());
    }

    @Test void notExistingFile() {
        File foo = new File("foo");
        OutputDirectoryProvider provider = OutputDirectoryProvider.ofFile(foo);
        File file = provider.calculateValue().get();
        assertThat(file).isEqualTo(foo);
    }

    @Test void notExistingPath() {
        Path foo = Paths.get("foo");
        OutputDirectoryProvider provider = OutputDirectoryProvider.ofPath(foo);
        File file = provider.calculateValue().get();
        assertThat(file).isEqualTo(foo.toFile());
    }
}
