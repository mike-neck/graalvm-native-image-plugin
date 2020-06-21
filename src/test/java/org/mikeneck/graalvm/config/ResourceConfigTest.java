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
import java.util.List;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResourceConfigTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TestJsonReader reader = new TestJsonReader();

    @Test
    public void jsonWithContents() throws IOException {
        try (InputStream inputStream = reader.configJsonResource("config/resource-config-1.json")) {
            ResourceConfig resourceConfig = objectMapper.readValue(inputStream, ResourceConfig.class);
            assertThat(
                    resourceConfig.resources,
                    hasItems(
                            new ResourceUsage("\\QMETA-INF/services/jdk.vm.ci.hotspot.HotSpotJVMCIBackendFactory\\E"),
                            new ResourceUsage("\\QMETA-INF/services/jdk.vm.ci.services.JVMCIServiceLocator\\E")));
            assertThat(
                    resourceConfig.bundles,
                    hasItem(new BundleUsage("usage")));
        }
    }

    @Test
    public void jsonWithoutContents() throws IOException {
        try (InputStream inputStream = reader.configJsonResource("config/resource-config-2.json")) {
            ResourceConfig resourceConfig = objectMapper.readValue(inputStream, ResourceConfig.class);
            assertThat(resourceConfig.resources, is(Collections.emptyList()));
            assertThat(resourceConfig.bundles, is(Collections.emptyList()));
        }
    }

    @Test
    public void merge() {
        ResourceConfig left = new ResourceConfig(
                Arrays.asList("resource-foo", "resource-bar"),
                "bundle-foo", "bundle-bar");
        ResourceConfig right = new ResourceConfig(
                Collections.singletonList("resource-baz"),
                "bundle-baz");

        ResourceConfig resourceConfig = left.mergeWith(right);

        assertThat(resourceConfig.resources, listOf(
                new ResourceUsage("resource-bar"),
                new ResourceUsage("resource-baz"),
                new ResourceUsage("resource-foo")));
        assertThat(resourceConfig.bundles, listOf(
                new BundleUsage("bundle-bar"),
                new BundleUsage("bundle-baz"),
                new BundleUsage("bundle-foo")));
    }

    @SafeVarargs
    private static <T extends Comparable<T>> Matcher<List<T>> listOf(T... items) {
        return is(Arrays.asList(items));
    }

    @Test
    public void mergeConfigWithSharedContents() {
        ResourceConfig left = new ResourceConfig(
                Arrays.asList("resource-foo", "resource-bar"),
                "bundle-foo", "bundle-bar");
        ResourceConfig right = new ResourceConfig(
                Arrays.asList("resource-baz", "resource-bar"),
                "bundle-baz", "bundle-bar");

        ResourceConfig resourceConfig = left.mergeWith(right);

        assertThat(resourceConfig.resources, listOf(
                new ResourceUsage("resource-bar"),
                new ResourceUsage("resource-baz"),
                new ResourceUsage("resource-foo")));
        assertThat(resourceConfig.bundles, listOf(
                new BundleUsage("bundle-bar"),
                new BundleUsage("bundle-baz"),
                new BundleUsage("bundle-foo")));
    }

    @Test
    public void mergeWithSelfBecomesSelf() {
        ResourceConfig resourceConfig = new ResourceConfig(
                Arrays.asList("resource-foo", "resource-bar"),
                "bundle-foo", "bundle-bar");

        ResourceConfig actual = resourceConfig.mergeWith(resourceConfig);

        assertThat(actual, is(resourceConfig));
    }

    @Test
    public void mergeWithEmptyBecomesSelf() {
        ResourceConfig resourceConfig = new ResourceConfig(
                Arrays.asList("resource-foo", "resource-bar"),
                "bundle-foo", "bundle-bar");

        ResourceConfig actual = resourceConfig.mergeWith(new ResourceConfig());

        assertThat(actual, is(resourceConfig));
    }
}
