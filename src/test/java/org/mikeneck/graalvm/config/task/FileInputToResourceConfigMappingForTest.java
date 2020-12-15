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
package org.mikeneck.graalvm.config.task;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.mikeneck.graalvm.config.BundleUsage;
import org.mikeneck.graalvm.config.ResourceConfig;
import org.mikeneck.graalvm.config.ResourceUsage;
import org.mikeneck.graalvm.config.SelectableMergeableConfig;

@TestOnly
public class FileInputToResourceConfigMappingForTest implements MappingCandidate<FileInput, SelectableMergeableConfig<?>> {

    @Override
    public @NotNull Optional<@NotNull Outcome<FileInput, SelectableMergeableConfig<?>>> examine(@NotNull FileInput input) {
        @SuppressWarnings("Convert2MethodRef")
        Function<FileInput, Optional<ResourceConfig>> mapper =
                fileInput ->
                        fileInput.makeUtf8String()
                                .flatMap(string -> makeResourceConfig(string));

        return mapper.apply(input)
                .map(resourceConfig ->
                        Outcome.forTest(
                                resourceConfig,
                                file -> mapper.apply(file).orElseThrow(IOException::new)));
    }

    @NotNull
    static FileInput oldResourceConfig(@NotNull String resources, @NotNull String bundles) {
        return (cs) ->
                new StringReader(new StringJoiner("/", "old:", "").add(resources).add(bundles).toString());
    }

    @NotNull
    private static Optional<ResourceConfig> makeResourceConfig(@NotNull String string) {
        if (!string.startsWith("old:")) {
            return Optional.empty();
        }
        String input = string.replace("old:", "");
        String[] parts = input.split("/", 2);
        if (parts.length != 2) {
            return Optional.empty();
        }
        String[] resources = parts[0].split(",");
        String[] bundles = parts[1].split(",");
        return Optional.of(resourceConfigBuilder
                .resources(resources)
                .bundles(bundles));
    }

    private interface ResourceConfigBuilderResources {
        @NotNull
        ResourceConfigBuilderBundles resources(@NotNull String... resources);
    }

    private interface ResourceConfigBuilderBundles {
        @NotNull
        ResourceConfig bundles(@NotNull String... bundles);
    }

    @NotNull
    private static List<@NotNull ResourceUsage> resourceUsages(@NotNull String... patterns) {
        Transformer<String, ResourceUsage> transformer = pattern -> {
            ResourceUsage resourceUsage = new ResourceUsage();
            resourceUsage.pattern = pattern;
            return resourceUsage;
        };
        return transformer.makeList(patterns);
    }

    @NotNull
    private static List<@NotNull BundleUsage> bundleUsages(@NotNull String... names) {
        Transformer<String, BundleUsage> transformer = name -> {
            BundleUsage bundleUsage = new BundleUsage();
            bundleUsage.name = name;
            return bundleUsage;
        };
        return transformer.makeList(names);
    }

    @NotNull
    private static final ResourceConfigBuilderResources resourceConfigBuilder =
            resources -> bundles -> new ResourceConfig(resourceUsages(resources), bundleUsages(bundles));
}
