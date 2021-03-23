package org.mikeneck.graalvm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

public class SerializationConfigTest {

  private static String extractGraalVmVersion(String versionString) {
    Pattern pattern = Pattern.compile("GraalVM.+(\\d{2}\\.\\d\\.\\d(\\.\\d)?)");
    Matcher matcher = pattern.matcher(versionString);
    if (matcher.find()) {
      return matcher.group(1);
    } else {
      return "";
    }
  }

  private static String graalVmVersionString() {
    String vendorVersion = System.getProperty("java.vendor.version");
    if (vendorVersion != null) {
      return extractGraalVmVersion(vendorVersion);
    }
    String javaVmName = System.getProperty("java.vm.name");
    if (javaVmName == null) {
      return "";
    }
    return extractGraalVmVersion(javaVmName);
  }

  private static boolean hasSerializationConfig() {
    String graalVmVersion = graalVmVersionString();
    return "21".compareTo(graalVmVersion) < 0;
  }

  @TestFactory
  Iterable<DynamicTest> graalVmVersion() {
    return Arrays.asList(
        dynamicTest(
            "normal java(OpenJDK 64-Bit Server VM) has no graalVmVersion",
            () -> assertThat(extractGraalVmVersion("OpenJDK 64-Bit Server VM")).isEqualTo("")),
        dynamicTest(
            "zulu java 11(Zulu11.45+27-CA) has no graalVmVersion",
            () -> assertThat(extractGraalVmVersion("Zulu11.45+27-CA")).isEqualTo("")),
        dynamicTest(
            "GraalVM 20.0.0(GraalVM CE 20.0.0) has graalVmVersion 20.0.0",
            () -> assertThat(extractGraalVmVersion("GraalVM CE 20.0.0")).isEqualTo("20.0.0")),
        dynamicTest(
            "GraalVM 21.0.0.2(GraalVM CE 21.0.0.2) has graalVmVersion 21.0.0.2",
            () -> assertThat(extractGraalVmVersion("GraalVM CE 21.0.0.2")).isEqualTo("21.0.0.2")),
        dynamicTest("21.0.0 is greater than 21", () -> assertThat("21.0").isGreaterThan("21")),
        dynamicTest("20.3.0 is less than 21", () -> assertThat("20.3.0").isLessThan("21")));
  }

  @Test
  @ExtendWith(TestProjectSetup.class)
  @TestProject(value = "serialization-config")
  void outputConfig(@NotNull final Gradlew gradlew, @NotNull FunctionalTestContext context) {
    if (!hasSerializationConfig()) {
      System.out.printf(
          "'outputConfig' test is skipped because of unsupported graalvm version(%s)\n",
          graalVmVersionString());
      return;
    } else {
      System.out.printf(
          "running 'outputConfig' test because of supported graalvm version(%s)\n",
          graalVmVersionString());
    }
    BuildResult result = gradlew.invoke("clean", "generateNativeImageConfig");
    assertAll(
        () ->
            assertThat(result.task(":generateNativeImageConfig"))
                .satisfies(
                    buildTask -> assertThat(buildTask.getOutcome()).isEqualTo(TaskOutcome.SUCCESS)),
        () ->
            assertThat(
                    context.rootDir.resolve(
                        "build/tmp/native-image-config/out-0/serialization-config.json"))
                .exists(),
        () ->
            assertThat(
                    context.rootDir.resolve("build/native-image-config/serialization-config.json"))
                .exists());
  }
}
