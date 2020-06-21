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

import java.util.Objects;
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
}
