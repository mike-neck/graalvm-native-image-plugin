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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClassUsage {

    @NotNull
    public String name = "";

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<MethodUsage> methods = Collections.emptyList();

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean allDeclaredFields;

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean allDeclaredMethods;

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean allDeclaredConstructors;

    public ClassUsage() {
    }

    ClassUsage(@NotNull String name) {
        this.name = name;
    }

    ClassUsage(@NotNull String name, MethodUsage... methods) {
        this.name = name;
        this.methods = Arrays.asList(methods);
    }

    ClassUsage(
            @NotNull String name,
            boolean allDeclaredMethods,
            boolean allDeclaredConstructors) {
        this.name = name;
        this.allDeclaredMethods = allDeclaredMethods;
        this.allDeclaredConstructors = allDeclaredConstructors;
    }

    ClassUsage(
            @NotNull String name,
            boolean allDeclaredMethods,
            boolean allDeclaredConstructors,
            boolean allDeclaredFields) {
        this.name = name;
        this.allDeclaredFields = allDeclaredFields;
        this.allDeclaredMethods = allDeclaredMethods;
        this.allDeclaredConstructors = allDeclaredConstructors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassUsage)) return false;
        ClassUsage that = (ClassUsage) o;
        return name.equals(that.name) &&
                methods.equals(that.methods) &&
                Objects.equals(allDeclaredFields, that.allDeclaredFields) &&
                Objects.equals(allDeclaredMethods, that.allDeclaredMethods) &&
                Objects.equals(allDeclaredConstructors, that.allDeclaredConstructors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, methods, allDeclaredFields, allDeclaredMethods, allDeclaredConstructors);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClassUsage{");
        sb.append("name='").append(name).append('\'');
        sb.append(", methods=").append(methods);
        sb.append(", allDeclaredFields=").append(allDeclaredFields);
        sb.append(", allDeclaredMethods=").append(allDeclaredMethods);
        sb.append(", allDeclaredConstructors=").append(allDeclaredConstructors);
        sb.append('}');
        return sb.toString();
    }
}
