package org.mikeneck.graalvm.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

class SerializationConfigTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void emptyJson() throws IOException {
    SerializationConfig config = objectMapper.readValue("[]", SerializationConfig.class);
    assertThat(config).isEmpty();
  }

  @Test
  @ExtendWith(TestJsonReader.class)
  @JsonFile("serialization-config-1.json")
  void singleNameOnlyJson(InputStream inputStream) throws IOException {
    SerializationConfig config = objectMapper.readValue(inputStream, SerializationConfig.class);
    assertThat(config).hasSize(1).containsOnly(new ConstructorUsage("com.example.Foo"));
  }

  @Test
  @ExtendWith(TestJsonReader.class)
  @JsonFile("serialization-config-2.json")
  void singleNameWithCustomCtorJson(InputStream inputStream) throws IOException {
    SerializationConfig config = objectMapper.readValue(inputStream, SerializationConfig.class);
    assertThat(config)
        .hasSize(1)
        .containsOnly(new ConstructorUsage("com.example.Foo", "com.example.Bar"));
  }

  @Test
  @ExtendWith(TestJsonReader.class)
  @JsonFile("serialization-config-3.json")
  void multipleNamedJson(InputStream inputStream) throws IOException {
    SerializationConfig config = objectMapper.readValue(inputStream, SerializationConfig.class);
    assertThat(config)
        .hasSize(2)
        .containsOnly(
            new ConstructorUsage("com.example.Foo"), new ConstructorUsage("com.example.Bar"));
  }

  @Test
  @ExtendWith(TestJsonReader.class)
  @JsonFile("serialization-config-4.json")
  void multipleNamedJsonAndNameWithCustomCtorJson(InputStream inputStream) throws IOException {
    SerializationConfig config = objectMapper.readValue(inputStream, SerializationConfig.class);
    assertThat(config)
        .hasSize(2)
        .containsOnly(
            new ConstructorUsage("com.example.Foo"),
            new ConstructorUsage("com.example.Bar", "com.example.Baz"));
  }

  private final ConstructorUsage foo = new ConstructorUsage("com.example.Foo");
  private final ConstructorUsage bar = new ConstructorUsage("com.example.Bar");
  private final ConstructorUsage baz = new ConstructorUsage("com.example.Baz");

  @Test
  void mergeWithSingleJson() {
    SerializationConfig foo = new SerializationConfig(this.foo);
    SerializationConfig bar = new SerializationConfig(this.bar);
    SerializationConfig merged = foo.mergeWith(bar);

    assertThat(merged).hasSize(2).containsExactly(this.bar, this.foo);
  }

  @Test
  void mergeWithSelf() {
    SerializationConfig foo = new SerializationConfig(this.foo);
    SerializationConfig another = new SerializationConfig(this.foo);
    SerializationConfig merged = foo.mergeWith(another);

    assertThat(merged).isEqualTo(foo);
  }

  @Test
  void mergeWithEmpty() {
    SerializationConfig foo = new SerializationConfig(this.foo);
    SerializationConfig another = new SerializationConfig();
    SerializationConfig merged = foo.mergeWith(another);

    assertThat(merged).isEqualTo(foo);
  }

  @Test
  void mergeWithMultiple() {
    SerializationConfig left = new SerializationConfig(this.foo, this.bar);
    SerializationConfig right = new SerializationConfig(this.bar, this.baz);
    SerializationConfig merged = left.mergeWith(right);

    assertThat(merged).hasSize(3).containsExactly(this.bar, this.baz, this.foo);
  }
}
