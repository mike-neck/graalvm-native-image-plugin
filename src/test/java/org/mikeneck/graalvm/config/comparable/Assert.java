package org.mikeneck.graalvm.config.comparable;

import org.assertj.core.api.GenericComparableAssert;

public interface Assert {

  static <T extends Comparable<T>> GenericComparableAssert<T> comparable(T left) {
    return new GenericComparableAssert<>(left);
  }
}
