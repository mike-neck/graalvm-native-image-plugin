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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class ResourceUsage implements Comparable<ResourceUsage> {

    @NotNull
    public String pattern = "";

    public ResourceUsage() {
    }

    ResourceUsage(@NotNull String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceUsage)) return false;
        ResourceUsage that = (ResourceUsage) o;
        return pattern.equals(that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResourceFileUsage{");
        sb.append("pattern='").append(pattern).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(@NotNull ResourceUsage o) {
        return this.pattern.compareTo(o.pattern);
    }

    @FunctionalInterface
    public interface   $20$3Builder {
        default  $20$3 excludes(String... excludes) {
            return excludes(Arrays.asList(excludes));
        }

        $20$3 excludes(@NotNull List<String> excludes);
    }

    public static class $20$3 implements Comparable<$20$3>, MergeableConfig<$20$3> {

        public static $20$3Builder includes(@NotNull List<String> includes) {
            return excludes -> new $20$3(
                    includes.stream().map(ResourceUsage::new).collect(Collectors.toList()), 
                    excludes.stream().map(ResourceUsage::new).collect(Collectors.toList()));
        }

        public static $20$3Builder includes(String... includes) {
            return excludes ->
                    new $20$3(
                            Arrays.stream(includes).map(ResourceUsage::new).collect(Collectors.toList()),
                            excludes.stream().map(ResourceUsage::new).collect(Collectors.toList()));
        }

        public List<ResourceUsage> includes = Collections.emptyList();

        public List<ResourceUsage> excludes = Collections.emptyList();

        $20$3(@NotNull List<ResourceUsage> includes, @NotNull List<ResourceUsage> excludes) {
            this.includes = includes;
            this.excludes = excludes;
        }

        public $20$3() {
        }

        @Override
        public int compareTo(@NotNull ResourceUsage.$20$3 o) {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof $20$3)) return false;
            $20$3 $2020$3 = ($20$3) o;
            return includes.equals($2020$3.includes) && excludes.equals($2020$3.excludes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(includes, excludes);
        }

        @SuppressWarnings("StringBufferReplaceableByString")
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("$2020$3{");
            sb.append("includes=").append(includes);
            sb.append(", excludes=").append(excludes);
            sb.append('}');
            return sb.toString();
        }

        @NotNull
        @Override
        public $20$3 mergeWith(@NotNull $20$3 other) {
            Set<ResourceUsage> includes = new TreeSet<>(this.includes);
            includes.addAll(other.includes);

            $20$3 newValue = new $20$3();
            newValue.includes = new ArrayList<>(includes);

            return newValue;
        }
    }
}
