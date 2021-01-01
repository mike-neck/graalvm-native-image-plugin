package org.mikeneck.graalvm.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Comparator;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public class FieldUsage implements Comparable<FieldUsage>, SelectableMergeableConfig<FieldUsage> {

  @NotNull public String name = "";

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public Boolean allowUnsafeAccess;

  @TestOnly
  FieldUsage withAllowUnsafeAccess() {
    return new FieldUsage(this.name, true, allowWrite);
  }

  @TestOnly
  FieldUsage withoutAllowUnsafeAccess() {
    return new FieldUsage(this.name, false, allowWrite);
  }

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public Boolean allowWrite;

  @TestOnly
  FieldUsage withAllowWrite() {
    return new FieldUsage(this.name, allowUnsafeAccess, true);
  }

  @TestOnly
  FieldUsage withoutAllowWrite() {
    return new FieldUsage(this.name, allowUnsafeAccess, false);
  }

  public FieldUsage() {}

  public FieldUsage(@NotNull String name) {
    this.name = name;
  }

  private FieldUsage(
      @NotNull String name, @Nullable Boolean allowUnsafeAccess, @Nullable Boolean allowWrite) {
    this.name = name;
    this.allowUnsafeAccess = allowUnsafeAccess;
    this.allowWrite = allowWrite;
  }

  @SuppressWarnings("StringBufferReplaceableByString")
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("FieldUsage{");
    sb.append("name='").append(name).append('\'');
    sb.append(", allowUnsafeAccess=").append(allowUnsafeAccess);
    sb.append(", allowWrite=").append(allowWrite);
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FieldUsage)) return false;
    FieldUsage that = (FieldUsage) o;
    return name.equals(that.name)
        && Objects.equals(allowUnsafeAccess, that.allowUnsafeAccess)
        && Objects.equals(allowWrite, that.allowWrite);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, allowUnsafeAccess, allowWrite);
  }

  @Override
  public int compareTo(@NotNull FieldUsage o) {
    Comparator<FieldUsage> comparator = Comparator.comparing(fieldUsage -> fieldUsage.name);
    Comparator<Boolean> nullFirst = Comparator.nullsFirst(Boolean::compareTo);

    int nameCompare = comparator.compare(this, o);
    if (nameCompare != 0) {
      return nameCompare;
    }
    int unsafe = nullFirst.compare(this.allowUnsafeAccess, o.allowUnsafeAccess);
    if (unsafe != 0) {
      return unsafe;
    }
    return nullFirst.compare(this.allowWrite, o.allowWrite);
  }

  @Override
  public FieldUsage mergeWith(FieldUsage other) {
    return new FieldUsage(
        this.name,
        BooleanMergeable.mergeBoolean(this.allowUnsafeAccess, other.allowUnsafeAccess),
        BooleanMergeable.mergeBoolean(this.allowWrite, other.allowWrite));
  }

  @Override
  public <T extends SelectableMergeableConfig<T>> boolean canBeMergeWith(@NotNull T other) {
    if (!(other instanceof FieldUsage)) {
      return false;
    }
    FieldUsage that = (FieldUsage) other;
    return this.name.equals(that.name);
  }
}
