/*
 * Copyright 2020 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.graalvm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum GraalVmVersion {
    GRAAL_19_3_3_JAVA_8("19.3.3-java8", new GraalVm19Matcher("19.3.3", "java8")),
    GRAAL_19_3_3_JAVA_11("19.3.3-java11", new GraalVm19Matcher("19.3.3", "java11")),
    GRAAL_19_3_4_JAVA_8("19.3.4-java8", new GraalVm19Matcher("19.3.4", "java8")),
    GRAAL_19_3_4_JAVA_11("19.3.4-java11", new GraalVm19Matcher("19.3.4", "java11")),
    GRAAL_20_0_0_JAVA_8("20.0.0-java8", new GraalVm20Matcher("20.0.0", "java8")),
    GRAAL_20_0_0_JAVA_11("20.0.0-java11", new GraalVm20Matcher("20.0.0", "java11")),
    GRAAL_20_1_0_JAVA_8("20.1.0-java8", new GraalVm20Matcher("20.1.0", "java8")),
    GRAAL_20_1_0_JAVA_11("20.1.0-java11", new GraalVm20Matcher("20.1.0", "java11")),
    GRAAL_20_2_0_JAVA_8("20.2.0-java8", new GraalVm20Matcher("20.2.0", "java8")),
    GRAAL_20_2_0_JAVA_11("20.2.0-java11", new GraalVm20Matcher("20.2.0", "java11")),
    GRAAL_20_3_0_JAVA_8("20.3.0-java8", new GraalVm20Matcher("20.3.0", "java8")),
    GRAAL_20_3_0_JAVA_11("20.3.0-java11", new GraalVm20Matcher("20.3.0", "java11")),
    ;

    @NotNull final String version;
    final @NotNull Matcher matcher;

    GraalVmVersion(@NotNull String version, @NotNull Matcher matcher) {
        this.version = version;
        this.matcher = matcher;
    }

    static final String JAVA_VERSION = "JAVA_VERSION";
    static final String GRAALVM_VERSION = "GRAALVM_VERSION";
    static final String COMPONENT_CATALOG = "component_catalog";

    boolean matchesVersion(@NotNull Map<String, String> properties) {
        return this.matcher.matchesVersion(properties);
    }

    public boolean isJava8() {
        return "java8".equals(this.matcher.getJavaVersion());
    }

    public boolean isJava11() {
        return "java11".equals(this.matcher.getJavaVersion());
    }

    int halfOrdinal() {
        return ordinal() / 2;
    }

    public boolean lessThan(@NotNull GraalVmVersion another) {
        return this.halfOrdinal() < another.halfOrdinal();
    }

    public boolean greaterThan(@NotNull GraalVmVersion another) {
        return this.halfOrdinal() > another.halfOrdinal();
    }

    @NotNull
    static GraalVmVersion findFromPath(@NotNull Path graalVmHome) {
        Path release = graalVmHome.resolve("release");
        try (BufferedReader reader = Files.newBufferedReader(release, StandardCharsets.UTF_8)) {
            return findFromReader(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("release file(%s) is not found\nPlease report the issue at https://github.com/mike-neck/graalvm-native-image-plugin/issues/new?template=bug_report.md .", release), e);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(String.format("cannot read version from %s\nPlease report the issue at https://github.com/mike-neck/graalvm-native-image-plugin/issues/new?template=bug_report.md .", release), e);
        }
    }

    @NotNull
    static GraalVmVersion findFromReader(@NotNull Reader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        Map<String, String> map = new HashMap<>(properties.size());
        for (String propertyName : properties.stringPropertyNames()) {
            map.put(propertyName, properties.getProperty(propertyName));
        }
        return Arrays.stream(values())
                .filter(version -> version.matchesVersion(map))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format(
                                "cannot found appropriate GraalVM version[java=%s,graalvm=%s,catalog=%s]",
                                map.get(JAVA_VERSION),
                                map.get(GRAALVM_VERSION),
                                map.get(COMPONENT_CATALOG))));
    }

    interface Matcher {
        boolean matchesVersion(@NotNull Map<String, String> properties);

        String getGraalVmVersion();
        String getJavaVersion();
    }

    static class GraalVm19Matcher implements Matcher {
        final String graalVmVersion;
        final String javaVersion;

        GraalVm19Matcher(String graalVmVersion, String javaVersion) {
            this.graalVmVersion = graalVmVersion;
            this.javaVersion = javaVersion;
        }

        @Override
        public String toString() {
            @SuppressWarnings("StringBufferReplaceableByString") 
            final StringBuilder sb = new StringBuilder("GraalVm19Matcher{");
            sb.append("graalVmVersion='").append(graalVmVersion).append('\'');
            sb.append(", javaVersion='").append(javaVersion).append('\'');
            sb.append('}');
            return sb.toString();
        }

        @Override
        public boolean matchesVersion(@NotNull Map<String, String> properties) {
            @Nullable String graalVmVersion = properties.get(GRAALVM_VERSION);
            @Nullable String catalogVersion = properties.get(COMPONENT_CATALOG);
            if (catalogVersion == null) {
                return false;
            }
            return this.graalVmVersion.equals(graalVmVersion) && catalogVersion.contains(this.javaVersion);
        }

        @Override
        public String getGraalVmVersion() {
            return graalVmVersion;
        }

        @Override
        public String getJavaVersion() {
            return javaVersion;
        }
    }

    static class GraalVm20Matcher implements Matcher {
        final String graalVmVersion;
        final String simplifiedJavaVersion;
        final String javaVersion;

        GraalVm20Matcher(String graalVmVersion, String javaVersion) {
            this.graalVmVersion = graalVmVersion;
            this.simplifiedJavaVersion = javaVersion;
            this.javaVersion = "java8".equals(javaVersion)? "1.8.0": "11.0";
        }

        @Override
        public String toString() {
            @SuppressWarnings("StringBufferReplaceableByString")
            final StringBuilder sb = new StringBuilder("GraalVm20Matcher{");
            sb.append("graalVmVersion='").append(graalVmVersion).append('\'');
            sb.append(", simplifiedJavaVersion='").append(simplifiedJavaVersion).append('\'');
            sb.append(", javaVersion='").append(javaVersion).append('\'');
            sb.append('}');
            return sb.toString();
        }

        @Override
        public boolean matchesVersion(@NotNull Map<String, String> properties) {
            @Nullable String javaVersion = properties.get(JAVA_VERSION);
            @Nullable String graalVmVersion = properties.get(GRAALVM_VERSION);
            if (javaVersion == null || graalVmVersion == null) {
                return false;
            }
            return javaVersion
                    .contains(this.javaVersion) &&
                    graalVmVersion.contains(this.graalVmVersion);
        }

        @Override
        public String getGraalVmVersion() {
            return graalVmVersion;
        }

        @Override
        public String getJavaVersion() {
            return simplifiedJavaVersion;
        }
    }
}
