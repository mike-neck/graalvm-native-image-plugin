package org.mikeneck.graalvm.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class FieldUsagesTest {

  private static FieldUsages fieldUsages(FieldUsage... usages) {
    return new FieldUsages(new TreeSet<>(Arrays.asList(usages)));
  }

  @Test
  void singleAndEmpty() {
    FieldUsages single = fieldUsages(new FieldUsage("com.example.Foo"));
    FieldUsages empty = fieldUsages();

    FieldUsages left = single.mergeWith(empty);
    FieldUsages right = empty.mergeWith(single);

    assertThat(left).isEqualTo(right).hasSize(1);
  }

  @Test
  void singleAndSingle() {
    FieldUsages first = fieldUsages(new FieldUsage("com.example.Foo"));
    FieldUsages second = fieldUsages(new FieldUsage("com.example.Bar"));

    FieldUsages left = first.mergeWith(second);
    FieldUsages right = second.mergeWith(first);

    assertThat(left)
        .isEqualTo(right)
        .hasSize(2)
        .extracting(usage -> usage.name)
        .containsExactly("com.example.Bar", "com.example.Foo");
  }

  @Test
  void mergeableSingle() {
    FieldUsages first = fieldUsages(new FieldUsage("com.example.Foo"));
    FieldUsages second =
        fieldUsages(new FieldUsage("com.example.Foo").withAllowUnsafeAccess().withAllowWrite());

    FieldUsages left = first.mergeWith(second);
    FieldUsages right = second.mergeWith(first);

    assertThat(left).isEqualTo(right).hasSize(1).isEqualTo(second);
  }

  @Test
  void smallerItemsAndGreaterItems() {
    FieldUsages smaller =
        fieldUsages(
            new FieldUsage("com.example.Bar"),
            new FieldUsage("com.example.Baz"),
            new FieldUsage("com.example.Foo"));
    FieldUsages greater =
        fieldUsages(
            new FieldUsage("com.sample.Bar"),
            new FieldUsage("com.sample.Baz"),
            new FieldUsage("com.sample.Foo"));

    FieldUsages left = smaller.mergeWith(greater);
    FieldUsages right = greater.mergeWith(smaller);

    assertThat(left).isEqualTo(right).hasSize(6);
  }

  @Test
  void manyMergeable() {
    FieldUsages smaller =
        fieldUsages(
            new FieldUsage("com.example.Bar"),
            new FieldUsage("com.example.Baz"),
            new FieldUsage("com.example.Foo"));
    FieldUsages greater =
        fieldUsages(
            new FieldUsage("com.example.Bar").withAllowUnsafeAccess().withoutAllowWrite(),
            new FieldUsage("com.example.Foo").withAllowUnsafeAccess(),
            new FieldUsage("com.example.Qux"));

    FieldUsages left = smaller.mergeWith(greater);
    FieldUsages right = greater.mergeWith(smaller);

    assertThat(left).isEqualTo(right).hasSize(4);
  }
}
