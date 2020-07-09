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
package org.mikeneck.graalvm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JniConfigTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TestJsonReader reader = new TestJsonReader();

    @Test
    public void jsonWithContents() throws IOException {
        try (InputStream inputStream = reader.configJsonResource("config/jni-config-1.json")) {
            JniConfig jniConfig = objectMapper.readValue(inputStream, JniConfig.class);
            assertThat(jniConfig, hasItems(
                    new ClassUsage(
                            IllegalArgumentException.class, 
                            new MethodUsage("<init>", "java.lang.String")),
                    new ClassUsage(
                            ArrayList.class, 
                            new MethodUsage("<init>"), 
                            MethodUsage.of("add", Object.class))
            ));
        }
    }

    @Test
    public void jsonWithoutContents() throws IOException {
        try (InputStream inputStream = reader.configJsonResource("config/jni-config-2.json")) {
            JniConfig jniConfig = objectMapper.readValue(inputStream, JniConfig.class);
            assertThat(jniConfig, is(Collections.emptySortedSet()));
        }
    }

    @Test
    public void mergeWithOther() {
        JniConfig left = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage("com.example.App", new MethodUsage("<init>")));
        JniConfig right = new JniConfig(
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)));

        JniConfig jniConfig = left.mergeWith(right);

        assertThat(jniConfig, hasItems(
                new ClassUsage("com.example.App", new MethodUsage("<init>")),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)),
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class))));
    }


    @Test
    public void mergeWithOtherHavingSameClass() {
        JniConfig left = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage("com.example.App", new MethodUsage("<init>")));
        JniConfig right = new JniConfig(
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class)),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)),
                new ClassUsage("com.example.App", MethodUsage.of("run")));

        JniConfig jniConfig = left.mergeWith(right);

        assertThat(jniConfig, hasItems(
                new ClassUsage("com.example.App", new MethodUsage("<init>"), MethodUsage.of("run")),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class)),
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class))));
    }

    @Test
    public void mergeWithAlreadyMerged() {
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

        assertThat(jniConfig, hasItems(
                new ClassUsage("com.example.App", new TreeSet<>(
                        Arrays.asList(MethodUsage.of("run"), MethodUsage.of("<init>"), MethodUsage.of("start", int.class))), 
                        Collections.emptySortedSet(),
                        null, Boolean.TRUE, Boolean.TRUE),
                new ClassUsage(IllegalArgumentException.class, MethodUsage.of("<init>", String.class), MethodUsage.of("getCause")),
                new ClassUsage(ArrayList.class, MethodUsage.of("<init>", int.class))));
    }
}
