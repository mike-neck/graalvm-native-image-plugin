package org.mikeneck.graalvm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JniConfigTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TestJsonReader reader = new TestJsonReader();

    @Test
    void jsonWithContents() throws IOException {
        try (InputStream inputStream = reader.configJsonResource("config/jni-config-1.json")) {
            JniConfig jniConfig = objectMapper.readValue(inputStream, JniConfig.class);
            assertThat(jniConfig).contains(
                    new ClassUsage(
                            IllegalArgumentException.class, 
                            new MethodUsage("<init>", "java.lang.String")),
                    new ClassUsage(
                            ArrayList.class, 
                            new MethodUsage("<init>"), 
                            MethodUsage.of("add", Object.class))
            );
        }
    }

    @Test
    void jsonWithoutContents() throws IOException {
        try (InputStream inputStream = reader.configJsonResource("config/jni-config-2.json")) {
            JniConfig jniConfig = objectMapper.readValue(inputStream, JniConfig.class);
            assertThat(jniConfig).isEqualTo(Collections.emptySortedSet());
        }
    }

    @Test
    void mergeWithOther() {
        JniConfig left = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage("com.example.App", new MethodUsage("<init>")));
        JniConfig right = new JniConfig(
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)));

        JniConfig jniConfig = left.mergeWith(right);

        assertThat(jniConfig).contains(
                new ClassUsage("com.example.App", new MethodUsage("<init>")),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)),
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)));
    }


    @Test
    void mergeWithOtherHavingSameClass() {
        JniConfig left = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage("com.example.App", new MethodUsage("<init>")));
        JniConfig right = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)),
                new ClassUsage("com.example.App", MethodUsage.of("run")));

        JniConfig jniConfig = left.mergeWith(right);

        assertThat(jniConfig).contains(
                new ClassUsage("com.example.App", new MethodUsage("<init>"), MethodUsage.of("run")),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)),
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)));
    }

    @Test
    void mergeWithAlreadyMerged() {
        JniConfig left = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage("com.example.App", new MethodUsage("<init>")));
        JniConfig right = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class), MethodUsage.of("getCause")),
                new ClassUsage("com.example.App", new TreeSet<>(
                        Arrays.asList(MethodUsage.of("run"), MethodUsage.of("start", int.class))),
                        Collections.emptySortedSet(),
                        null, Boolean.TRUE, Boolean.TRUE));

        JniConfig jniConfig = left.mergeWith(right);

        assertThat(jniConfig).contains(
                new ClassUsage("com.example.App", new TreeSet<>(
                        Arrays.asList(MethodUsage.of("run"), MethodUsage.of("<init>"), MethodUsage.of("start", int.class))), 
                        Collections.emptySortedSet(),
                        null, Boolean.TRUE, Boolean.TRUE),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class), MethodUsage.of("getCause")),
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)));
    }
}
