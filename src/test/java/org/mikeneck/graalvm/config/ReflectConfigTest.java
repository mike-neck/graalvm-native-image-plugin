package org.mikeneck.graalvm.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReflectConfigTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final TestJsonReader reader = new TestJsonReader();

  @Test
  void jsonWithContents() throws IOException {
    try (final InputStream inputStream =
        reader.configJsonResource("config/reflect-config-1.json")) {
      ReflectConfig reflectConfig = objectMapper.readValue(inputStream, ReflectConfig.class);
      assertThat(reflectConfig)
          .contains(
              new ClassUsage(
                  "com.fasterxml.jackson.databind.ext.Java7SupportImpl", new MethodUsage("<init>")),
              new ClassUsage("java.sql.Date"),
              new ClassUsage("java.sql.Timestamp"),
              new ClassUsage("java.util.ArrayList", true, true),
              new ClassUsage("java.util.LinkedHashMap", true, true),
              new ClassUsage("com.example.App", true, true, true));
    }
  }

  @Test
  void jsonWithoutContents() throws IOException {
    try (final InputStream inputStream =
        reader.configJsonResource("config/reflect-config-2.json")) {
      ReflectConfig reflectConfig = objectMapper.readValue(inputStream, ReflectConfig.class);
      assertThat(reflectConfig).isEqualTo(Collections.emptySortedSet());
    }
  }

  @Test
  void mergeWithOther() {
    ReflectConfig left =
        new ReflectConfig(
            new ClassUsage("java.sql.Date", MethodUsage.of("getTime")),
            new ClassUsage("java.sql.Timestamp"));
    ReflectConfig right =
        new ReflectConfig(
            new ClassUsage(ArrayList.class, MethodUsage.ofInit(int.class)),
            new ClassUsage("com.example.App", MethodUsage.of("run")));

    ReflectConfig reflectConfig = left.mergeWith(right);

    assertThat(reflectConfig)
        .contains(
            new ClassUsage("com.example.App", MethodUsage.of("run")),
            new ClassUsage("java.sql.Date", MethodUsage.of("getTime")),
            new ClassUsage("java.sql.Timestamp"),
            new ClassUsage(ArrayList.class, MethodUsage.ofInit(int.class)));
  }

  @Test
  void mergeWithOtherHavingSameClass() {
    ReflectConfig left =
        new ReflectConfig(
            new ClassUsage("java.sql.Date", MethodUsage.of("getTime")),
            new ClassUsage("java.sql.Timestamp"));
    ReflectConfig right =
        new ReflectConfig(
            new ClassUsage(ArrayList.class, MethodUsage.ofInit(int.class)),
            new ClassUsage("java.sql.Date", MethodUsage.of("getTime")),
            new ClassUsage("com.example.App", MethodUsage.of("run")));

    ReflectConfig reflectConfig = left.mergeWith(right);

    assertThat(reflectConfig)
        .contains(
            new ClassUsage("com.example.App", MethodUsage.of("run")),
            new ClassUsage("java.sql.Date", MethodUsage.of("getTime")),
            new ClassUsage("java.sql.Timestamp"),
            new ClassUsage(ArrayList.class, MethodUsage.ofInit(int.class)));
  }

  @Test
  void mergeWithOtherHavingSameClassUsingAnotherMethodsAndFields() {
    ReflectConfig left =
        new ReflectConfig(
            new ClassUsage("java.sql.Date", MethodUsage.of("getTime")),
            new ClassUsage(
                ArrayList.class,
                MethodUsage.ofInit(int.class),
                MethodUsage.of("add", Object.class),
                MethodUsage.of("addAll", Collection.class)),
            new ClassUsage("com.example.Bar", new FieldUsage("baz"), new FieldUsage("qux")),
            new ClassUsage("java.sql.Timestamp"));
    ReflectConfig right =
        new ReflectConfig(
            new ClassUsage(ArrayList.class, MethodUsage.ofInit()),
            new ClassUsage("com.example.Bar", new FieldUsage("quux"), new FieldUsage("baz")),
            new ClassUsage("java.sql.Date", MethodUsage.of("getTime")),
            new ClassUsage("com.example.App", MethodUsage.of("run")));

    ReflectConfig reflectConfig = left.mergeWith(right);

    assertThat(reflectConfig)
        .contains(
            new ClassUsage("com.example.App", MethodUsage.of("run")),
            new ClassUsage(
                "com.example.Bar",
                new FieldUsage("baz"),
                new FieldUsage("quux"),
                new FieldUsage("qux")),
            new ClassUsage("java.sql.Date", MethodUsage.of("getTime")),
            new ClassUsage("java.sql.Timestamp"),
            new ClassUsage(
                ArrayList.class,
                MethodUsage.ofInit(),
                MethodUsage.ofInit(int.class),
                MethodUsage.of("add", Object.class),
                MethodUsage.of("addAll", Collection.class)));
  }

  @Test
  void mergeWithEmptyBecomesSelf() {
    ReflectConfig left =
        new ReflectConfig(
            new ClassUsage("java.sql.Date", MethodUsage.of("getTime")),
            new ClassUsage(
                ArrayList.class,
                MethodUsage.ofInit(int.class),
                MethodUsage.of("add", Object.class),
                MethodUsage.of("addAll", Collection.class)),
            new ClassUsage("java.sql.Timestamp"));
    ReflectConfig right = new ReflectConfig();

    ReflectConfig reflectConfig = left.mergeWith(right);

    assertThat(reflectConfig).isEqualTo(left);
  }

  @Test
  void mergeByEmptyWithEmptyBecomesEmpty() {
    ReflectConfig left = new ReflectConfig();
    ReflectConfig right = new ReflectConfig();

    ReflectConfig reflectConfig = left.mergeWith(right);

    assertThat(reflectConfig).isEqualTo(Collections.emptySortedSet());
  }

  @Test
  void mergeByEmptyWithOtherBecomesOther() {
    ReflectConfig left = new ReflectConfig();
    ReflectConfig right =
        new ReflectConfig(
            new ClassUsage(ArrayList.class, MethodUsage.ofInit(int.class)),
            new ClassUsage("com.example.App", MethodUsage.of("run")));

    ReflectConfig reflectConfig = left.mergeWith(right);

    assertThat(reflectConfig).isEqualTo(right);
  }

  // https://github.com/mike-neck/graalvm-native-image-plugin/issues/46
  @Test
  void parseErrorCase46() throws IOException {
    try (final InputStream inputStream =
        reader.configJsonResource("config/reflect-config-parse-error-46.json")) {
      ReflectConfig reflectConfig = objectMapper.readValue(inputStream, ReflectConfig.class);
      assertThat(reflectConfig).isNotNull();
    }
  }

  @DisplayName("test-case98[https://github.com/mike-neck/graalvm-native-image-plugin/issues/98]")
  @Test
  void case98() {
    try (InputStream inputStream =
        reader.configJsonResource("config/reflect-config-case-98.json")) {
      ReflectConfig reflectConfig = objectMapper.readValue(inputStream, ReflectConfig.class);
      assertThat(reflectConfig).isNotNull();
    } catch (IOException e) {
      fail("failed to parse allPublicMethods", e);
    }
  }
}
