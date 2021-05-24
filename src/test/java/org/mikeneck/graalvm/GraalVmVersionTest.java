package org.mikeneck.graalvm;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class GraalVmVersionTest {

  private static InputStream loadPropertiesFile(GraalVmVersion graalVmVersion) {
    GraalVmVersion.Matcher matcher = graalVmVersion.matcher;
    String javaVersion = matcher.getJavaVersion();
    String graalVersion = matcher.getGraalVmVersion();
    return loadPropertiesFile(javaVersion, graalVersion);
  }

  @NotNull
  private static InputStream loadPropertiesFile(String javaVersion, String graalVersion) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStream stream =
        classLoader.getResourceAsStream(
            String.format("release/%s/%s.properties", javaVersion, graalVersion));
    return Objects.requireNonNull(
        stream, () -> String.format("no resource found for %s-%s", javaVersion, graalVersion));
  }

  @ParameterizedTest
  @EnumSource(
      value = GraalVmVersion.class,
      names = {
        "GRAAL_19_3_4_JAVA_8",
        "GRAAL_19_3_4_JAVA_11",
        "GRAAL_20_2_0_JAVA_8",
        "GRAAL_20_2_0_JAVA_11",
        "GRAAL_20_3_0_JAVA_8",
        "GRAAL_20_3_0_JAVA_11",
        "GRAAL_21_0_0_JAVA_8",
        "GRAAL_21_0_0_JAVA_11",
        "GRAAL_21_1_0_JAVA_8",
        "GRAAL_21_1_0_JAVA_11",
        "GRAAL_21_1_0_JAVA_16"
      })
  void loading(GraalVmVersion graalVmVersion) throws IOException {
    try (Reader reader = new InputStreamReader(loadPropertiesFile(graalVmVersion))) {
      GraalVmVersion version = GraalVmVersion.findFromReader(reader);
      assertThat(version).isEqualTo(graalVmVersion);
    }
  }

  static class SpecificProperties {
    private final String javaVersion;
    private final String graalVersion;
    private final GraalVmVersion expected;

    SpecificProperties(String javaVersion, String graalVersion, GraalVmVersion expected) {
      this.javaVersion = javaVersion;
      this.graalVersion = graalVersion;
      this.expected = expected;
    }

    DynamicTest toDynamicTest() {
      return DynamicTest.dynamicTest(
          String.format("file %s/%s is properties file of %s", javaVersion, graalVersion, expected),
          () -> {
            try (Reader reader =
                new InputStreamReader(loadPropertiesFile(javaVersion, graalVersion))) {
              GraalVmVersion version = GraalVmVersion.findFromReader(reader);
              assertThat(version).isEqualTo(expected);
            }
          });
    }

    static ToBeSpecificProperties of(String javaVersion, String graalVersion) {
      return expected -> new SpecificProperties(javaVersion, graalVersion, expected);
    }
  }

  @FunctionalInterface
  interface ToBeSpecificProperties {
    SpecificProperties toBe(GraalVmVersion expected);
  }

  @TestFactory
  Stream<DynamicTest> specificPropertiesFiles() {
    return Stream.of(
            SpecificProperties.of("mandrel", "20.2.0.0").toBe(GraalVmVersion.MANDREL_20_2_0_0),
            SpecificProperties.of("java16", "21.1.0").toBe(GraalVmVersion.GRAAL_21_1_0_JAVA_16),
            SpecificProperties.of("java8", "21.1.0").toBe(GraalVmVersion.GRAAL_21_1_0_JAVA_8))
        .map(SpecificProperties::toDynamicTest);
  }
}
