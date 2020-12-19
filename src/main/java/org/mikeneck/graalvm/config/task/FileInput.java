package org.mikeneck.graalvm.config.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.LoggerFactory;

public interface FileInput {

    Reader newReader(Charset charset) throws IOException;

    @TestOnly
    default Optional<String> makeUtf8String() {
        try (BufferedReader reader = new BufferedReader(newReader(StandardCharsets.UTF_8))) {
            return Optional.of(reader.lines().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @NotNull
    static List<FileInput> from(@NotNull String resourceGroupName, @NotNull Iterable<File> files) {
        LoggerFactory.getLogger(FileInput.class).info("input files: {}", files);
        return StreamSupport
                .stream(files.spliterator(), false)
                .map(file -> FileInput.from(resourceGroupName, file))
                .collect(Collectors.toList());
    }

    @NotNull
    static FileInput from(@NotNull String resourceGroupName, @NotNull File file) {
        return from(resourceGroupName, file.toPath());
    }

    @NotNull
    static FileInput from(@NotNull String resourceGroupName, @NotNull Path file) {
        return new FileInputImpl(resourceGroupName, file);
    }
}
