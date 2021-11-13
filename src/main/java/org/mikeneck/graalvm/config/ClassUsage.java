package org.mikeneck.graalvm.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClassUsage implements Comparable<ClassUsage>, MergeableConfig<ClassUsage> {

  @NotNull public String name = "";

  @NotNull
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public SortedSet<MethodUsage> methods = Collections.emptySortedSet();

  @NotNull
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private FieldUsages fields = new FieldUsages(Collections.emptySortedSet());

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

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean allPublicConstructors;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean allPublicFields;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean allDeclaredClasses;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean allPublicClasses;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean queryAllDeclaredConstructors;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean queryAllPublicConstructors;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean queryAllDeclaredMethods;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean queryAllPublicMethods;

  @SuppressWarnings("unused")
  public ClassUsage() {}

  private ClassUsage(
      @NotNull String name,
      @NotNull SortedSet<MethodUsage> methods,
      @NotNull FieldUsages fields,
      @Nullable Boolean allDeclaredFields,
      @Nullable Boolean allDeclaredMethods,
      @Nullable Boolean allDeclaredConstructors,
      @Nullable Boolean allPublicMethods,
      @Nullable Boolean allPublicConstructors,
      @Nullable Boolean allPublicFields,
      @Nullable Boolean allDeclaredClasses,
      @Nullable Boolean allPublicClasses,
      @Nullable Boolean queryAllDeclaredConstructors,
      @Nullable Boolean queryAllPublicConstructors,
      @Nullable Boolean queryAllDeclaredMethods,
      @Nullable Boolean queryAllPublicMethods) {
    this.name = name;
    this.methods = methods;
    this.fields = fields;
    this.allDeclaredFields = allDeclaredFields;
    this.allDeclaredMethods = allDeclaredMethods;
    this.allDeclaredConstructors = allDeclaredConstructors;
    this.allPublicMethods = allPublicMethods;
    this.allPublicConstructors = allPublicConstructors;
    this.allPublicFields = allPublicFields;
    this.allDeclaredClasses = allDeclaredClasses;
    this.allPublicClasses = allPublicClasses;
    this.queryAllDeclaredConstructors = queryAllDeclaredConstructors;
    this.queryAllPublicConstructors = queryAllPublicConstructors;
    this.queryAllDeclaredMethods = queryAllDeclaredMethods;
    this.queryAllPublicMethods = queryAllPublicMethods;
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
    this.fields = new FieldUsages(fields);
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
    this.fields = new FieldUsages(new TreeSet<>(Arrays.asList(fields)));
  }

  ClassUsage(@NotNull String name, boolean allDeclaredMethods, boolean allDeclaredConstructors) {
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

  @SuppressWarnings("unused")
  public List<FieldUsage> getFields() {
    return StreamSupport.stream(fields.spliterator(), false).collect(Collectors.toList());
  }

  @SuppressWarnings("unused")
  public void setFields(List<FieldUsage> fields) {
    if (fields.isEmpty()) {
      this.fields = new FieldUsages(Collections.emptySortedSet());
      return;
    }
    Iterator<FieldUsage> usages = new TreeSet<>(fields).iterator();
    SortedSet<FieldUsage> newFieldUsages = new TreeSet<>();
    FieldUsage fieldUsage = usages.next();
    while (true) {
      if (!usages.hasNext()) {
        newFieldUsages.add(fieldUsage);
        break;
      }
      FieldUsage next = usages.next();
      if (fieldUsage.name.equals(next.name)) {
        fieldUsage = fieldUsage.mergeWith(next);
      } else {
        newFieldUsages.add(fieldUsage);
        fieldUsage = next;
      }
    }
    this.fields = new FieldUsages(newFieldUsages);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ClassUsage)) return false;
    ClassUsage that = (ClassUsage) o;
    return name.equals(that.name)
        && methods.equals(that.methods)
        && fields.equals(that.fields)
        && Objects.equals(allDeclaredFields, that.allDeclaredFields)
        && Objects.equals(allDeclaredMethods, that.allDeclaredMethods)
        && Objects.equals(allDeclaredConstructors, that.allDeclaredConstructors)
        && Objects.equals(allPublicMethods, that.allPublicMethods)
        && Objects.equals(allPublicConstructors, that.allPublicConstructors)
        && Objects.equals(allPublicFields, that.allPublicFields)
        && Objects.equals(allDeclaredClasses, that.allDeclaredClasses)
        && Objects.equals(allPublicClasses, that.allPublicClasses)
        && Objects.equals(queryAllDeclaredConstructors, that.queryAllDeclaredConstructors)
        && Objects.equals(queryAllPublicConstructors, that.queryAllPublicConstructors)
        && Objects.equals(queryAllDeclaredMethods, that.queryAllDeclaredMethods)
        && Objects.equals(queryAllPublicMethods, that.queryAllPublicMethods);
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
        allPublicMethods,
        allPublicConstructors,
        allPublicFields,
        allDeclaredClasses,
        allPublicClasses,
        queryAllDeclaredConstructors,
        queryAllPublicConstructors,
        queryAllDeclaredMethods,
        queryAllPublicMethods);
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
    sb.append(", allPublicConstructors=").append(allPublicConstructors);
    sb.append(", allPublicFields=").append(allPublicFields);
    sb.append(", allDeclaredClasses=").append(allDeclaredClasses);
    sb.append(", allPublicClasses=").append(allPublicClasses);
    sb.append(", queryAllDeclaredConstructors=").append(queryAllDeclaredConstructors);
    sb.append(", queryAllPublicConstructors=").append(queryAllPublicConstructors);
    sb.append(", queryAllDeclaredMethods=").append(queryAllDeclaredMethods);
    sb.append(", queryAllPublicMethods=").append(queryAllPublicMethods);
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
      throw new IllegalArgumentException(
          "A parameter has the same name with this[" + this.name + "], but [" + other.name + "].");
    }
    TreeSet<MethodUsage> newMethods = new TreeSet<>(this.methods);
    newMethods.addAll(other.methods);
    FieldUsages newFields = this.fields.mergeWith(other.fields);
    return new ClassUsage(
        this.name,
        newMethods,
        newFields,
        BooleanMergeable.mergeBoolean(this.allDeclaredFields, other.allDeclaredFields),
        BooleanMergeable.mergeBoolean(this.allDeclaredMethods, other.allDeclaredMethods),
        BooleanMergeable.mergeBoolean(this.allDeclaredConstructors, other.allDeclaredConstructors),
        BooleanMergeable.mergeBoolean(this.allPublicMethods, other.allPublicMethods),
        BooleanMergeable.mergeBoolean(this.allPublicConstructors, other.allPublicConstructors),
        BooleanMergeable.mergeBoolean(this.allPublicFields, other.allPublicFields),
        BooleanMergeable.mergeBoolean(this.allDeclaredClasses, other.allDeclaredClasses),
        BooleanMergeable.mergeBoolean(this.allPublicClasses, other.allPublicClasses),
        BooleanMergeable.mergeBoolean(this.queryAllDeclaredConstructors, other.queryAllDeclaredConstructors),
        BooleanMergeable.mergeBoolean(this.queryAllPublicConstructors, other.queryAllPublicConstructors),
        BooleanMergeable.mergeBoolean(this.queryAllDeclaredMethods, other.queryAllDeclaredMethods),
        BooleanMergeable.mergeBoolean(this.queryAllPublicMethods, other.queryAllPublicMethods));
  }
}
