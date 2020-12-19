package org.mikeneck.graalvm.config.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.MergeableConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMergeConfigFileWork<C extends MergeableConfig<C>>
    implements MergeConfigFileWork<C> {

  private static final Logger logger = LoggerFactory.getLogger(DefaultMergeConfigFileWork.class);

  @NotNull private final ObjectMapper objectMapper;
  @NotNull private final Class<C> klass;
  @NotNull private final Supplier<? extends C> empty;
  @NotNull private final List<FileInput> inputFiles;
  @NotNull private final FileOutput outputFile;

  public DefaultMergeConfigFileWork(
      @NotNull Class<C> klass,
      @NotNull Supplier<? extends C> empty,
      @NotNull List<FileInput> inputFiles,
      @NotNull FileOutput outputFile) {
    this(new ObjectMapper(), klass, empty, inputFiles, outputFile);
  }

  private DefaultMergeConfigFileWork(
      @NotNull ObjectMapper objectMapper,
      @NotNull Class<C> klass,
      @NotNull Supplier<? extends C> empty,
      @NotNull List<FileInput> inputFiles,
      @NotNull FileOutput outputFile) {
    this.objectMapper = objectMapper;
    this.klass = klass;
    this.empty = empty;
    this.inputFiles = inputFiles;
    this.outputFile = outputFile;
  }

  @Override
  public void run() throws IOException {
    List<C> entries = readAllFromInputFiles();
    C merged = merge(entries);
    logger.info("merged config file: {}", merged);
    writeToOutput(merged);
  }

  List<C> readAllFromInputFiles() throws IOException {
    List<C> configs = new ArrayList<>(inputFiles.size());
    for (FileInput inputFile : inputFiles) {
      try (Reader reader = inputFile.newReader(StandardCharsets.UTF_8)) {
        C config = objectMapper.readValue(reader, klass);
        configs.add(config);
      }
    }
    return configs;
  }

  C merge(List<C> entries) {
    return entries.stream().reduce(empty.get(), MergeableConfig::mergeWith);
  }

  void writeToOutput(C merged) throws IOException {
    logger.info("writing json to {}", outputFile);
    try (OutputStream outputStream = outputFile.newOutputStream();
        Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
      objectMapper.writeValue(writer, merged);
    }
  }

  @SuppressWarnings("StringBufferReplaceableByString")
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("MergeConfigFileWork{");
    sb.append("klass=").append(klass);
    sb.append('}');
    return sb.toString();
  }
}
