package org.mikeneck.graalvm.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FieldUsage implements Comparable<FieldUsage> {

  @NotNull public String name = "";

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public Boolean allowUnsafeAccess;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public Boolean allowWrite;

  public FieldUsage() {}

  public FieldUsage(@NotNull String name) {
    this.name = name;
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
    return this.name.compareTo(o.name);
  }
}
