package org.mikeneck.graalvm.config;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class FieldUsageTest {

  private static <T extends SelectableMergeableConfig<T>>
      CanBeMergeWithOther<FieldUsage, T> fieldUsage(@NotNull FieldUsage fieldUsage) {
    return other -> expected -> new CanBeMergeWithDatum<>(fieldUsage, other, expected);
  }

  private interface CanBeMergeWithOther<
      S extends SelectableMergeableConfig<S>, T extends SelectableMergeableConfig<T>> {
    CanBeMergeWithExpectedResult<S, T> mergeWith(T other);
  }

  private interface CanBeMergeWithExpectedResult<
      S extends SelectableMergeableConfig<S>, T extends SelectableMergeableConfig<T>> {
    default CanBeMergeWithDatum<S, T> expectsToBeTrue() {
      return expectsToBe(true);
    }

    default CanBeMergeWithDatum<S, T> expectsToBeFalse() {
      return expectsToBe(false);
    }

    CanBeMergeWithDatum<S, T> expectsToBe(boolean expected);
  }

  static Iterable<CanBeMergeWithDatum<FieldUsage, ?>> testData() {
    CanBeMergeWithOther<FieldUsage, FieldUsage> test1 =
        fieldUsage(new FieldUsage("com.example.Foo"));
    CanBeMergeWithOther<FieldUsage, ResourceConfig> test2 =
        fieldUsage(new FieldUsage("com.example.Foo"));
    return Arrays.asList(
        test1
            .mergeWith(new FieldUsage("com.example.Foo").withAllowUnsafeAccess().withAllowWrite())
            .expectsToBeTrue(),
        test1.mergeWith(new FieldUsage("com.example.Bar")).expectsToBeFalse(),
        test2.mergeWith(new ResourceConfig()).expectsToBeFalse());
  }

  @ParameterizedTest
  @MethodSource("testData")
  void canBeMergeWith(CanBeMergeWithDatum<FieldUsage, ?> canBeMergeWith) {
    canBeMergeWith.runTest();
  }

  @ParameterizedTest
  @EnumSource(FieldUsageCompareTo.class)
  void compareTo(FieldUsageCompareTo compareTo) {
    compareTo.runTest();
  }
}
