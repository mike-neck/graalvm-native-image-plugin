package org.mikeneck.graalvm;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
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
        "GRAAL_20_3_0_JAVA_11"
      })
  void loading(GraalVmVersion graalVmVersion) throws IOException {
    try (Reader reader = new InputStreamReader(loadPropertiesFile(graalVmVersion))) {
      GraalVmVersion version = GraalVmVersion.findFromReader(reader);
      assertThat(version).isEqualTo(graalVmVersion);
    }
  }

  @Test
  void mandrel() throws IOException {
    try (Reader reader = new InputStreamReader(loadPropertiesFile("mandrel", "20.2.0.0"))) {
      GraalVmVersion version = GraalVmVersion.findFromReader(reader);
      assertThat(version).isEqualTo(GraalVmVersion.MANDREL_20_2_0_0);
    }
  }
}
