package org.mikeneck.graalvm.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mikeneck.graalvm.config.comparable.Assert.comparable;

import java.util.Arrays;
import org.assertj.core.api.GenericComparableAssert;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class ConstructorUsageTest {

  private static final String CLASS_FOO = "com.example.Foo";
  private final ConstructorUsage foo = new ConstructorUsage(CLASS_FOO);

  @Test
  void equalsToBeTrue() {
    ConstructorUsage other = new ConstructorUsage(CLASS_FOO);
    assertThat(foo).isEqualTo(other);
  }

  @Test
  void equalsNotToBeTrue() {
    ConstructorUsage fooWithCustomCtor = new ConstructorUsage(CLASS_FOO, "com.example.Bar");
    assertThat(foo).isNotEqualTo(fooWithCustomCtor);
  }

  @TestFactory
  Iterable<DynamicTest> compareTo() {
    ConstructorUsage fooWithBar = new ConstructorUsage(CLASS_FOO, "com.example.Bar");
    ConstructorUsage fooWithBaz = new ConstructorUsage(CLASS_FOO, "com.example.Baz");
    ConstructorUsage bar = new ConstructorUsage("com.example.Bar");
    ConstructorUsage qux = new ConstructorUsage("com.example.Qux");
    GenericComparableAssert<ConstructorUsage> assertFoo = comparable(foo);

    return Arrays.asList(
        dynamicTest("foo is less than fooWithCustomCtor", () -> assertFoo.isLessThan(fooWithBar)),
        dynamicTest("foo is greater than bar", () -> assertFoo.isGreaterThan(bar)),
        dynamicTest("foo is less than qux", () -> assertFoo.isLessThan(qux)),
        dynamicTest(
            "fooWithBar is less than fooWithBaz",
            () -> assertThat(fooWithBar).isLessThan(fooWithBaz)),
        dynamicTest(
            "fooWithBar is greater than foo", () -> assertThat(fooWithBar).isGreaterThan(foo)));
  }
}
