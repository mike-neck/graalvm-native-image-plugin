package org.mikeneck.graalvm.config.task;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public interface FileOutput {

  OutputStream newOutputStream() throws IOException;

  default void withOutputStream(@NotNull OutputStreamOperation operation) throws IOException {
    try (OutputStream outputStream = newOutputStream()) {
      UnCloseableOutputStream unCloseableOutputStream =
          UnCloseableOutputStream.delegateTo(outputStream);
      operation.consume(unCloseableOutputStream);
    }
  }

  @NotNull
  static FileOutput to(@NotNull File file) {
    return to(file.toPath());
  }

  @NotNull
  static FileOutput to(@NotNull Supplier<Path> directory, @NotNull String fileName) {
    return () ->
        Files.newOutputStream(
            directory.get().resolve(fileName),
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING);
  }

  @NotNull
  static FileOutput to(@NotNull Path file) {
    return new FileOutputImpl(file);
  }
}
