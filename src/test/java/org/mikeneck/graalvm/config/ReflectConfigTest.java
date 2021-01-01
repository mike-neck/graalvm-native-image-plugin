package org.mikeneck.graalvm.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

  @ParameterizedTest(name = "{index}: {0} + {1} -> {2}")
  @MethodSource("mergeBooleanParams")
  void mergeBoolean(
      @Nullable Boolean thisBoolean, @Nullable Boolean otherBoolean, @Nullable Boolean expected) {
    Boolean actual = ClassUsage.mergeBoolean(thisBoolean, otherBoolean);
    assertThat(actual).isEqualTo(expected);
  }

  static Iterable<Arguments> mergeBooleanParams() {
    return Arrays.asList(
        Arguments.arguments(null, null, null),
        Arguments.arguments(null, Boolean.TRUE, Boolean.TRUE),
        Arguments.arguments(null, Boolean.FALSE, Boolean.FALSE),
        Arguments.arguments(Boolean.FALSE, null, Boolean.FALSE),
        Arguments.arguments(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE),
        Arguments.arguments(Boolean.FALSE, Boolean.TRUE, Boolean.TRUE),
        Arguments.arguments(Boolean.TRUE, null, Boolean.TRUE),
        Arguments.arguments(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE),
        Arguments.arguments(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE));
  }

  @DisplayName("test-case109(https://github.com/mike-neck/graalvm-native-image-plugin/issues/109)")
  @TestFactory
  Iterable<DynamicTest> case109() {
    try (InputStream inputStream =
        reader.configJsonResource("config/reflect-config-case-109.json")) {
      ReflectConfig reflectConfig = objectMapper.readValue(inputStream, ReflectConfig.class);

      return Arrays.asList(
          DynamicTest.dynamicTest(
              "reflectConfig is not null", () -> assertThat(reflectConfig).isNotNull()),
          DynamicTest.dynamicTest(
              "There are 7 allPublicConstructors.",
              () ->
                  assertThat(
                          reflectConfig.stream()
                              .filter(
                                  cu ->
                                      cu.allPublicConstructors != null
                                          && cu.allPublicConstructors.equals(Boolean.TRUE)))
                      .hasSize(7)),
          DynamicTest.dynamicTest(
              "There are 2 allPublicFields",
              () ->
                  assertThat(
                          reflectConfig.stream()
                              .filter(
                                  cu ->
                                      cu.allPublicFields != null
                                          && cu.allPublicFields.equals(Boolean.TRUE)))
                      .hasSize(2)));
    } catch (IOException e) {
      return Collections.singleton(
          DynamicTest.dynamicTest(
              "failed to parse allPublicConstructors",
              () -> fail("failed to parse allPublicConstructors", e)));
    }
  }

  @TestFactory
  Iterable<DynamicTest> case111() throws IOException {
    try (InputStream inputStream =
        reader.configJsonResource("config/reflect-config-case-111.json")) {
      ReflectConfig reflectConfig = objectMapper.readValue(inputStream, ReflectConfig.class);

      List<DynamicTest> tests = new ArrayList<>();
      tests.add(
          DynamicTest.dynamicTest("has 3 elements", () -> assertThat(reflectConfig).hasSize(3)));
      tests.add(
          DynamicTest.dynamicTest(
              "has String",
              () ->
                  assertThat(reflectConfig)
                      .anySatisfy(
                          classUsage ->
                              assertThat(classUsage.name).isEqualTo("java.lang.String"))));
      return tests;
    }
  }
}
