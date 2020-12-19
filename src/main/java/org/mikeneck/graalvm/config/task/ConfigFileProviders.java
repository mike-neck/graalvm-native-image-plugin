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
  public static ConfigFileProviders resolving(
      @NotNull FileCollection files, @NotNull String fileName) {
    return new ConfigFileProviders(
        () ->
            files.getFiles().stream()
                .map(File::toPath)
                .map(path -> path.resolve(fileName))
                .map(Path::toFile)
                .collect(Collectors.toList()));
  }
}
