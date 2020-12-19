package org.mikeneck.graalvm.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClassUsage implements Comparable<ClassUsage>, MergeableConfig<ClassUsage> {

    @NotNull
    public String name = "";

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<MethodUsage> methods = Collections.emptySortedSet();

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<FieldUsage> fields = Collections.emptySortedSet();

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean allDeclaredFields;

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean allDeclaredMethods;

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean allDeclaredConstructors;

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean allPublicMethods;

    public ClassUsage() {
    }

    public ClassUsage(
            @NotNull String name, 
            @NotNull SortedSet<MethodUsage> methods, 
            @NotNull SortedSet<FieldUsage> fields,
            @Nullable Boolean allDeclaredFields,
            @Nullable Boolean allDeclaredMethods,
            @Nullable Boolean allDeclaredConstructors,
            @Nullable Boolean allPublicMethods) {
        this.name = name;
        this.methods = methods;
        this.fields = fields;
        this.allDeclaredFields = allDeclaredFields;
        this.allDeclaredMethods = allDeclaredMethods;
        this.allDeclaredConstructors = allDeclaredConstructors;
        this.allPublicMethods = allPublicMethods;
    }

    public ClassUsage(
            @NotNull String name,
            @NotNull SortedSet<MethodUsage> methods,
            @NotNull SortedSet<FieldUsage> fields, 
            @Nullable Boolean allDeclaredFields,
            @Nullable Boolean allDeclaredMethods,
            @Nullable Boolean allDeclaredConstructors) {
        this.name = name;
        this.methods = methods;
        this.fields = fields;
        this.allDeclaredFields = allDeclaredFields;
        this.allDeclaredMethods = allDeclaredMethods;
        this.allDeclaredConstructors = allDeclaredConstructors;
    }

    ClassUsage(@NotNull String name) {
        this.name = name;
    }

    ClassUsage(@NotNull Class<?> klass, MethodUsage... methods) {
        this(klass.getCanonicalName(), methods);
    }

    ClassUsage(@NotNull String name, MethodUsage... methods) {
        this.name = name;
        this.methods = new TreeSet<>(Arrays.asList(methods));
    }

    ClassUsage(@NotNull String name, FieldUsage... fields) {
        this.name = name;
        this.fields = new TreeSet<>(Arrays.asList(fields));
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
                fields.equals(that.fields) &&
                Objects.equals(allDeclaredFields, that.allDeclaredFields) &&
                Objects.equals(allDeclaredMethods, that.allDeclaredMethods) &&
                Objects.equals(allDeclaredConstructors, that.allDeclaredConstructors) &&
                Objects.equals(allPublicMethods, that.allPublicMethods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                methods,
                fields,
                allDeclaredFields,
                allDeclaredMethods,
                allDeclaredConstructors,
                allPublicMethods);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClassUsage{");
        sb.append("name='").append(name).append('\'');
        sb.append(", methods=").append(methods);
        sb.append(", fields=").append(fields);
        sb.append(", allDeclaredFields=").append(allDeclaredFields);
        sb.append(", allDeclaredMethods=").append(allDeclaredMethods);
        sb.append(", allDeclaredConstructors=").append(allDeclaredConstructors);
        sb.append(", allPublicMethods=").append(allPublicMethods);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(@NotNull ClassUsage o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public ClassUsage mergeWith(ClassUsage other) {
        if (!this.name.equals(other.name)) {
            throw new IllegalArgumentException("A parameter has the same name with this[" + this.name + "], but [" + other.name + "].");
        }
        Boolean declaredFields = this.allDeclaredFields == null ? other.allDeclaredFields : this.allDeclaredFields;
        Boolean declaredConstructors = this.allDeclaredConstructors == null ? other.allDeclaredConstructors : this.allDeclaredConstructors;
        Boolean declaredMethods = this.allDeclaredMethods == null ? other.allDeclaredMethods : this.allDeclaredMethods;
        Boolean publicMethods = this.allPublicMethods == null ? other.allPublicMethods : this.allPublicMethods;
        TreeSet<MethodUsage> newMethods = new TreeSet<>(this.methods);
        newMethods.addAll(other.methods);
        TreeSet<FieldUsage> newFields = new TreeSet<>(this.fields);
        newFields.addAll(other.fields);
        return new ClassUsage(this.name, newMethods, newFields, declaredFields, declaredMethods, declaredConstructors, publicMethods);
    }
}
