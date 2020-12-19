package org.mikeneck.graalvm.config;

import org.jetbrains.annotations.NotNull;

public interface SelectableMergeableConfig<S extends SelectableMergeableConfig<S>> extends MergeableConfig<S> {

    <T extends SelectableMergeableConfig<T>> boolean canBeMergeWith(@NotNull T other);
}
