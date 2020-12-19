package org.mikeneck.graalvm.config.task;

import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.SelectableMergeableConfig;

public interface SelectorMergeConfigFileWorkFactory {

  FileInputsSelection resource(@NotNull String resourceName);

  interface FileInputsSelection {
    MappingCandidateSelection files(Iterable<FileInput> files);
  }

  interface TypeProvider {
    Class<? extends SelectableMergeableConfig<?>> get();

    static <C extends SelectableMergeableConfig<C>> TypeProvider of(Class<C> klass) {
      return () -> klass;
    }
  }

  interface MappingCandidateSelection {
    <C extends SelectableMergeableConfig<C>> FileOutputSelection<C> resourceCandidateTypes(
        @NotNull TypeProvider... klasses);
  }

  interface FileOutputSelection<C extends SelectableMergeableConfig<C>> {
    MergeConfigFileWork<C> writeTo(@NotNull FileOutput fileOutput);
  }
}
