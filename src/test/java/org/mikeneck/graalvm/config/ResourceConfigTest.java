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
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ResourceConfigTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TestJsonReader reader = new TestJsonReader();

    @Nested
    class FirstDataModel {

        @Test
        void jsonWithContents() throws IOException {
            try (InputStream inputStream = reader.configJsonResource("config/resource-config-1.json")) {
                ResourceConfig resourceConfig = objectMapper.readValue(inputStream, ResourceConfig.class);
                assertThat(
                        resourceConfig.resources)
                        .contains(
                                new ResourceUsage("\\QMETA-INF/services/jdk.vm.ci.hotspot.HotSpotJVMCIBackendFactory\\E"),
                                new ResourceUsage("\\QMETA-INF/services/jdk.vm.ci.services.JVMCIServiceLocator\\E"));
                assertThat(resourceConfig.bundles)
                        .contains(new BundleUsage("usage"));
            }
        }

        @Test
        void jsonWithoutContents() throws IOException {
            try (InputStream inputStream = reader.configJsonResource("config/resource-config-2.json")) {
                ResourceConfig resourceConfig = objectMapper.readValue(inputStream, ResourceConfig.class);
                assertThat(resourceConfig.resources).isEqualTo(Collections.emptyList());
                assertThat(resourceConfig.bundles).isEqualTo(Collections.emptyList());
            }
        }

        @Test
        void merge() {
            ResourceConfig left = new ResourceConfig(
                    Arrays.asList("resource-foo", "resource-bar"),
                    "bundle-foo", "bundle-bar");
            ResourceConfig right = new ResourceConfig(
                    Collections.singletonList("resource-baz"),
                    "bundle-baz");

            ResourceConfig resourceConfig = left.mergeWith(right);

            assertThat(resourceConfig.resources).contains(
                    new ResourceUsage("resource-bar"),
                    new ResourceUsage("resource-baz"),
                    new ResourceUsage("resource-foo"));
            assertThat(resourceConfig.bundles).contains(
                    new BundleUsage("bundle-bar"),
                    new BundleUsage("bundle-baz"),
                    new BundleUsage("bundle-foo"));
        }

        @Test
        void mergeConfigWithSharedContents() {
            ResourceConfig left = new ResourceConfig(
                    Arrays.asList("resource-foo", "resource-bar"),
                    "bundle-foo", "bundle-bar");
            ResourceConfig right = new ResourceConfig(
                    Arrays.asList("resource-baz", "resource-bar"),
                    "bundle-baz", "bundle-bar");

            ResourceConfig resourceConfig = left.mergeWith(right);

            assertThat(resourceConfig.resources).contains(
                    new ResourceUsage("resource-bar"),
                    new ResourceUsage("resource-baz"),
                    new ResourceUsage("resource-foo"));
            assertThat(resourceConfig.bundles).contains(
                    new BundleUsage("bundle-bar"),
                    new BundleUsage("bundle-baz"),
                    new BundleUsage("bundle-foo"));
        }

        @Test
        void mergeWithSelfBecomesSelf() {
            ResourceConfig resourceConfig = new ResourceConfig(
                    Arrays.asList("resource-foo", "resource-bar"),
                    "bundle-foo", "bundle-bar");

            ResourceConfig actual = resourceConfig.mergeWith(resourceConfig);

            assertThat(actual).isEqualTo(resourceConfig);
        }

        @Test
        void mergeWithEmptyBecomesSelf() {
            ResourceConfig resourceConfig = new ResourceConfig(
                    Arrays.asList("resource-foo", "resource-bar"),
                    "bundle-foo", "bundle-bar");

            ResourceConfig actual = resourceConfig.mergeWith(new ResourceConfig());

            assertThat(actual).isEqualTo(resourceConfig);
        }
    }

    @Nested
    class $20$3Test {

        @Test
        void jsonWithInclude1() throws IOException {
            try (InputStream inputStream = reader.configJsonResource("config/resource-config-2020.3-1.json")) {
                ResourceConfig.$20$3 resourceConfig =  objectMapper.readValue(inputStream, ResourceConfig.$20$3.class);
                assertAll(
                        () -> assertThat(resourceConfig).isNotNull(),
                        () -> assertThat(resourceConfig.resources).isNotNull(),
                        () -> assertThat(resourceConfig.bundles).isEmpty(),
                        () -> assertThat(resourceConfig.resources.includes).containsOnly(new ResourceUsage("\\QMETA-INF/services/com.example.App\\E")),
                        () -> assertThat(resourceConfig.resources.excludes).isEmpty()
                );
            }
        }

        @Test
        void jsonBothData() throws IOException {
            try (InputStream inputStream = reader.configJsonResource("config/resource-config-2020.3-2.json")) {
                ResourceConfig.$20$3 resourceConfig =  objectMapper.readValue(inputStream, ResourceConfig.$20$3.class);
                assertAll(
                        () -> assertThat(resourceConfig).isNotNull(),
                        () -> assertThat(resourceConfig.resources).isNotNull(),
                        () -> assertThat(resourceConfig.bundles).hasSize(3),
                        () -> assertThat(resourceConfig.resources.excludes).containsOnly(new ResourceUsage("\\QMETA-INF/services/com.example.App\\E")),
                        () -> assertThat(resourceConfig.resources.includes).hasSize(7)
                );
            }
        }

        @Test
        void merge() {
            ResourceConfig.$20$3 left = new ResourceConfig.$20$3(Collections.emptyList(), Collections.emptyList(), "bundle-foo");
            ResourceConfig.$20$3 right = new ResourceConfig.$20$3(Collections.emptyList(), Collections.emptyList(), "bundle-bar");

            ResourceConfig.$20$3 actual = left.mergeWith(right);

            assertThat(actual)
                    .isEqualTo(new ResourceConfig.$20$3(Collections.emptyList(), Collections.emptyList(), "bundle-bar", "bundle-foo"));
        }

        @Test
        void mergeIncludes() {
            ResourceConfig.$20$3 left = new ResourceConfig.$20$3(Arrays.asList("includes-foo", "includes-baz"), Collections.emptyList());
            ResourceConfig.$20$3 right = new ResourceConfig.$20$3(Arrays.asList("includes-qux", "includes-bar", "includes-quux"), Collections.emptyList());

            ResourceConfig.$20$3 actual = left.mergeWith(right);

            assertThat(actual)
                    .isEqualTo(new ResourceConfig.$20$3(
                            Arrays.asList(
                                    "includes-bar",
                                    "includes-baz",
                                    "includes-foo",
                                    "includes-quux",
                                    "includes-qux"),
                            Collections.emptyList()));
        }

        @Test
        void mergeExcludes() {
            ResourceConfig.$20$3 left = new ResourceConfig.$20$3(Collections.emptyList(), Arrays.asList("excludes-foo", "excludes-baz"));
            ResourceConfig.$20$3 right = new ResourceConfig.$20$3(Collections.emptyList(), Arrays.asList("excludes-qux", "excludes-bar", "excludes-quux"));

            ResourceConfig.$20$3 actual = left.mergeWith(right);

            assertThat(actual)
                    .isEqualTo(new ResourceConfig.$20$3(
                            Collections.emptyList(),
                            Arrays.asList(
                                    "excludes-bar",
                                    "excludes-baz",
                                    "excludes-foo",
                                    "excludes-quux",
                                    "excludes-qux")));
        }
    }
}
