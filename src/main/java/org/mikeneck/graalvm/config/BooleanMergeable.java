package org.mikeneck.graalvm.config;

import org.jetbrains.annotations.Nullable;

public interface BooleanMergeable {

  @Nullable
  static Boolean mergeBoolean(@Nullable Boolean fromThis, @Nullable Boolean fromOther) {
    if (fromThis == null) {
      return fromOther;
    }
    if (fromOther == null) {
      return fromThis;
    }
    return fromThis || fromOther;
  }
}
