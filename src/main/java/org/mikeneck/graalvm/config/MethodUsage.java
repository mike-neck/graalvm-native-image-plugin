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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class MethodUsage implements Comparable<MethodUsage> {

    @NotNull
    public String name = "";

    @NotNull
    public List<String> parameterTypes = new ArrayList<>();

    MethodUsage() {
    }

    MethodUsage(@NotNull String name, @NotNull String... parameterTypes) {
        this.name = name;
        this.parameterTypes = Arrays.asList(parameterTypes);
    }

    static MethodUsage of(@NotNull String name, @NotNull Class<?>... parameterTypes) {
        return new MethodUsage(
                name, 
                Arrays.stream(parameterTypes)
                        .map(Class::getCanonicalName)
                        .toArray(String[]::new));
    }

    public static MethodUsage ofInit(@NotNull Class<?>... parameterTypes) {
        return new MethodUsage(
                "<init>",
                Arrays.stream(parameterTypes)
                        .map(Class::getCanonicalName)
                        .toArray(String[]::new));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodUsage)) return false;
        MethodUsage that = (MethodUsage) o;
        return name.equals(that.name) &&
                parameterTypes.equals(that.parameterTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameterTypes);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Method{");
        sb.append("name='").append(name).append('\'');
        sb.append(", parameterTypes=").append(parameterTypes);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(@NotNull MethodUsage o) {
        int nameResult = this.name.compareTo(o.name);
        if (nameResult != 0) {
            return nameResult;
        }
        Iterator<String> iterator = o.parameterTypes.iterator();
        for (String parameterType : parameterTypes) {
            if (!iterator.hasNext()) {
                return 1;
            }
            String other = iterator.next();
            int current = parameterType.compareTo(other);
            if (current != 0) {
                return current;
            }
        }
        if (iterator.hasNext()) {
            return -1;
        }
        return 0;
    }
}
