package org.mikeneck.graalvm.config.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.SelectableMergeableConfig;

public class MappingCandidateByObjectMapper<@NotNull C extends SelectableMergeableConfig<C>>
    implements MappingCandidate<FileInput, C>, Mapper<FileInput, C> {

  private final Class<C> klass;
  private final ObjectMapper objectMapper;

  MappingCandidateByObjectMapper(Class<C> klass, ObjectMapper objectMapper) {
    this.klass = klass;
    this.objectMapper = objectMapper;
  }

  @Override
  public @NotNull Optional<@NotNull Outcome<FileInput, C>> examine(@NotNull FileInput input) {
    try {
      C product = work(input);
      return Optional.of(new DefaultOutcome<>(product, this));
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  @Override
  public @NotNull C work(@NotNull FileInput input) throws IOException {
    try (Reader reader = input.newReader(StandardCharsets.UTF_8)) {
      return objectMapper.readValue(reader, klass);
    }
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("MappingCandidateByObjectMapper{");
    sb.append("klass=").append(klass);
    sb.append('}');
    return sb.toString();
  }
}
