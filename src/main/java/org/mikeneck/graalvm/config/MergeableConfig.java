package org.mikeneck.graalvm.config;

public interface MergeableConfig<M extends MergeableConfig<M>> {

  M mergeWith(M other);
}
