package org.mikeneck.graalvm;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class GraalVmVersionTest {

    private static InputStream loadPropertiesFile(GraalVmVersion graalVmVersion) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        GraalVmVersion.Matcher matcher = graalVmVersion.matcher;
        InputStream stream = classLoader.getResourceAsStream(String.format("release/%s/%s.properties", matcher.getJavaVersion(), matcher.getGraalVmVersion()));
        return Objects.requireNonNull(stream, () -> String.format("no resource found for %s-%s", matcher.getJavaVersion(), matcher.getGraalVmVersion()));
    }

    @ParameterizedTest
    @EnumSource(value = GraalVmVersion.class, names = { "GRAAL_19_3_4_JAVA_8", "GRAAL_19_3_4_JAVA_11", "GRAAL_20_2_0_JAVA_8", "GRAAL_20_2_0_JAVA_11", "GRAAL_20_3_0_JAVA_8", "GRAAL_20_3_0_JAVA_11" })
    void loading(GraalVmVersion graalVmVersion) {
        try (Reader reader = new InputStreamReader(loadPropertiesFile(graalVmVersion))) {
            GraalVmVersion version = GraalVmVersion.findFromReader(reader);
            assertThat(version).isEqualTo(graalVmVersion);
        } catch (IOException e) {
        }
    }
}