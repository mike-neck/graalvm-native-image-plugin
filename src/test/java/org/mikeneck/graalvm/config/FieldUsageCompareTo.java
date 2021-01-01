package org.mikeneck.graalvm.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.jetbrains.annotations.NotNull;

enum FieldUsageCompareTo {
  ALL_NULL_TO_ALL_NULL {
    @Override
    @NotNull
    FieldUsage target() {
      return allNull;
    }

    @Override
    @NotNull
    FieldUsage other() {
      return allNull;
    }

    @Override
    void runTest() {
      assertThat(target()).isEqualByComparingTo(other());
    }
  },
  ALL_NULL_TO_UNSAFE {
    @Override
    @NotNull
    FieldUsage target() {
      return allNull;
    }

    @Override
    @NotNull
    FieldUsage other() {
      return unsafe;
    }

    @Override
    void runTest() {
      assertThat(target()).isLessThan(other());
    }
  },
  ALL_NULL_TO_WRITE {
    @Override
    @NotNull
    FieldUsage target() {
      return allNull;
    }

    @Override
    @NotNull
    FieldUsage other() {
      return write;
    }

    @Override
    void runTest() {
      assertThat(target()).isLessThan(other());
    }
  },
  ALL_NULL_TO_UNSAFE_WRITE {
    @Override
    @NotNull
    FieldUsage target() {
      return allNull;
    }

    @Override
    @NotNull
    FieldUsage other() {
      return unsafeWrite;
    }

    @Override
    void runTest() {
      assertThat(target()).isLessThan(other());
    }
  },
  UNSAFE_TO_ALL_NULL {
    @Override
    @NotNull
    FieldUsage target() {
      return unsafe;
    }

    @Override
    @NotNull
    FieldUsage other() {
      return allNull;
    }

    @Override
    void runTest() {
      assertThat(target()).isGreaterThan(other());
    }
  },
  SMALLER_NAME_TO_ALL_NULL {
    @Override
    @NotNull
    FieldUsage target() {
      return smallerName;
    }

    @Override
    @NotNull
    FieldUsage other() {
      return allNull;
    }

    @Override
    void runTest() {
      assertThat(target()).isLessThan(other());
    }
  },
  GREATER_NAME_TO_ALL_NULL {
    @Override
    @NotNull
    FieldUsage target() {
      return greaterName;
    }

    @Override
    @NotNull
    FieldUsage other() {
      return allNull;
    }

    @Override
    void runTest() {
      assertThat(target()).isGreaterThan(other());
    }
  },
  UNSAFE_TO_UNSAFE_FALSE {
    @Override
    @NotNull
    FieldUsage target() {
      return unsafe;
    }

    @Override
    @NotNull
    FieldUsage other() {
      return unsafeFalse;
    }

    @Override
    void runTest() {
      assertThat(target()).isGreaterThan(other());
    }
  },
  WRITE_TO_WRITE_FALSE {
    @Override
    @NotNull
    FieldUsage target() {
      return write;
    }

    @Override
    @NotNull
    FieldUsage other() {
      return writeFalse;
    }

    @Override
    void runTest() {
      assertThat(target()).isGreaterThan(other());
    }
  },
  ;

  @NotNull
  abstract FieldUsage target();

  @NotNull
  abstract FieldUsage other();

  abstract void runTest();

  static FieldUsage allNull = new FieldUsage("com.example.Foo");
  static FieldUsage unsafe = new FieldUsage("com.example.Foo").withAllowUnsafeAccess();
  static FieldUsage unsafeFalse = new FieldUsage("com.example.Foo").withoutAllowUnsafeAccess();
  static FieldUsage write = new FieldUsage("com.example.Foo").withAllowWrite();
  static FieldUsage writeFalse = new FieldUsage("com.example.Foo").withoutAllowWrite();
  static FieldUsage unsafeWrite =
      new FieldUsage("com.example.Foo").withAllowWrite().withAllowUnsafeAccess();
  static FieldUsage smallerName = new FieldUsage("com.example.Bar");
  static FieldUsage greaterName = new FieldUsage("com.example.Qux");
}
