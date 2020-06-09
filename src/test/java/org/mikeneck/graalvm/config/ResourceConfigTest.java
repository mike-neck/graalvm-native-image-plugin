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
import java.util.Collections;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResourceConfigTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ClassLoader classLoader = getClass().getClassLoader();

    private InputStream configJsonResource(String name) {
        InputStream inputStream = classLoader.getResourceAsStream(name);
        if (inputStream == null) {
            throw new IllegalStateException(String.format("%s not found", name));
        }
        return inputStream;
    }

    @Test
    public void jsonWithContents() throws IOException {
        try (InputStream inputStream = configJsonResource("config/resource-config-1.json")) {
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
        try (InputStream inputStream = configJsonResource("config/resource-config-2.json")) {
            ResourceConfig resourceConfig = objectMapper.readValue(inputStream, ResourceConfig.class);
            assertThat(resourceConfig.resources, is(Collections.emptyList()));
            assertThat(resourceConfig.bundles, is(Collections.emptyList()));
        }
    }
}
