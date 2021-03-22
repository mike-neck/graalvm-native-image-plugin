package org.mikeneck.graalvm.config;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConstructorUsage implements Comparable<ConstructorUsage> {

  @NotNull public String name = "";

  @Nullable public String customTargetConstructorClass;

  public ConstructorUsage() {}

  public ConstructorUsage(@NotNull String name) {
    this.name = name;
  }

  public ConstructorUsage(@NotNull String name, @NotNull String customTargetConstructorClass) {
    this.name = name;
    this.customTargetConstructorClass = customTargetConstructorClass;
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("ConstructorUsage{");
    sb.append("name='").append(name).append('\'');
    sb.append(", customTargetConstructorClass='").append(customTargetConstructorClass).append('\'');
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ConstructorUsage)) return false;
    ConstructorUsage that = (ConstructorUsage) o;
    return name.equals(that.name)
        && Objects.equals(customTargetConstructorClass, that.customTargetConstructorClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, customTargetConstructorClass);
  }

  @Override
  public int compareTo(@NotNull ConstructorUsage o) {
    int nameComparison = this.name.compareTo(o.name);
    if (nameComparison != 0) {
      return nameComparison;
    }
    if (this.customTargetConstructorClass == null && o.customTargetConstructorClass == null) {
      return 0;
    } else if (this.customTargetConstructorClass == null) {
      return -1;
    } else if (o.customTargetConstructorClass == null) {
      return 1;
    } else {
      return this.customTargetConstructorClass.compareTo(o.customTargetConstructorClass);
    }
  }
}
