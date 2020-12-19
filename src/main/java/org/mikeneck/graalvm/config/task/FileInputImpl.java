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
    final StringBuilder sb =
        new StringBuilder("FileInput[")
            .append(resourceGroupName)
            .append('(')
            .append(path)
            .append(')')
            .append(']');
    return sb.toString();
  }
}
