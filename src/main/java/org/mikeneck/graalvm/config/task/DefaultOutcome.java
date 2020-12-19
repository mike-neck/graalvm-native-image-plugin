package org.mikeneck.graalvm.config.task;

import org.jetbrains.annotations.NotNull;

public class DefaultOutcome<@NotNull I, @NotNull P> implements Outcome<I, P> {

    @NotNull
    private final P product;
    @NotNull
    private final Mapper<I, P> mapper;

    DefaultOutcome(@NotNull P product, @NotNull Mapper<I, P> mapper) {
        this.product = product;
        this.mapper = mapper;
    }

    @Override
    public @NotNull Mapper<I, P> detectedMapper() {
        return mapper;
    }

    @Override
    public @NotNull P detectedProduct() {
        return product;
    }
}
