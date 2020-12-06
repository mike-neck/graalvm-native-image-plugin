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

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FieldUsage implements Comparable<FieldUsage> {

    @NotNull
    public String name = "";

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Boolean allowUnsafeAccess;

    public FieldUsage() {
    }

    public FieldUsage(@NotNull String name) {
        this.name = name;
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FieldUsage{");
        sb.append("name='").append(name).append('\'');
        sb.append(", allowUnsafeAccess=").append(allowUnsafeAccess);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldUsage)) return false;
        FieldUsage that = (FieldUsage) o;
        return name.equals(that.name) &&
                Objects.equals(allowUnsafeAccess, that.allowUnsafeAccess);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, allowUnsafeAccess);
    }

    @Override
    public int compareTo(@NotNull FieldUsage o) {
        return this.name.compareTo(o.name);
    }
}
