package org.mikeneck.graalvm.config;

import static org.assertj.core.api.Assertions.assertThat;

class CanBeMergeWithDatum<
    S extends SelectableMergeableConfig<S>, T extends SelectableMergeableConfig<T>> {
  final S target;
  final T other;
  final boolean expected;

  CanBeMergeWithDatum(S target, T other, boolean expected) {
    this.target = target;
    this.other = other;
    this.expected = expected;
  }

  void runTest() {
    boolean actual = target.canBeMergeWith(other);
    assertThat(actual).isEqualTo(expected);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("CanBeMergeWith{");
    if (target instanceof FieldUsage) {
      FieldUsage fu = (FieldUsage) target;
      sb.append("[field-usage:").append(fu.name).append("],");
    } else if (target instanceof ResourceConfig) {
      sb.append("[resource-config:").append(target.getClass().getSimpleName()).append("],");
    }
    if (other instanceof FieldUsage) {
      FieldUsage fu = (FieldUsage) other;
      sb.append("canMergeWith[field-usage:").append(fu.name).append(']');
    } else {
      sb.append("canMergeWith[other:").append(other.getClass().getSimpleName()).append(']');
    }
    sb.append('}');
    return sb.toString();
  }
}
