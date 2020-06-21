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


import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class ProxyUsage implements Comparable<ProxyUsage> {

    @NotNull
    public String canonicalClassName = "";

    public ProxyUsage() {
    }

    ProxyUsage(@NotNull String canonicalClassName) {
        this.canonicalClassName = canonicalClassName;
    }

    ProxyUsage(@NotNull Class<?> klass) {
        this(klass.getCanonicalName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProxyUsage)) return false;
        ProxyUsage that = (ProxyUsage) o;
        return canonicalClassName.equals(that.canonicalClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(canonicalClassName);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProxyTarget{");
        sb.append("canonicalClassName='").append(canonicalClassName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(@NotNull ProxyUsage o) {
        return this.canonicalClassName.compareTo(o.canonicalClassName);
    }
}
