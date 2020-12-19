package org.mikeneck.graalvm.config.task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public interface Outcome<@NotNull I, @NotNull P> {

  @NotNull
  Mapper<I, P> detectedMapper();

  @NotNull
  P detectedProduct();

  @TestOnly
  @NotNull
  static <@NotNull I, @NotNull P> Outcome<I, P> forTest(
      @NotNull P product, @NotNull Mapper<I, P> mapper) {
    return new Outcome<I, P>() {
      @Override
      public @NotNull Mapper<I, P> detectedMapper() {
        return mapper;
      }

      @Override
      public @NotNull P detectedProduct() {
        return product;
      }
    };
  }
}
