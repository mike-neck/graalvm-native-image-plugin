package org.mikeneck.graalvm.config.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.jetbrains.annotations.NotNull;

public class WriteObjectOperationByObjectMapper<T> implements WriteObjectOperation<T> {

  @NotNull private final ObjectMapper objectMapper;

  public WriteObjectOperationByObjectMapper(@NotNull ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void write(@NotNull UnCloseableOutputStream out, @NotNull T object) throws IOException {
    try (Writer writer = new OutputStreamWriter(out.asOutputStream())) {
      objectMapper.writer().writeValue(writer, object);
    }
  }
}
