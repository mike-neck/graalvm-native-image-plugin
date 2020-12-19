package org.mikeneck.graalvm.config.task;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
interface Transformer<F, P> {
  @NotNull
  P apply(@NotNull F from);

  @SuppressWarnings("unchecked")
  @NotNull
  default List<@NotNull P> makeList(@NotNull F... froms) {
    @NotNull List<@NotNull P> list = new ArrayList<>();
    for (@NotNull F from : froms) {
      list.add(this.apply(from));
    }
    return list;
  }
}
