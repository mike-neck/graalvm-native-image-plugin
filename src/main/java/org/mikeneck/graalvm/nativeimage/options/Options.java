package org.mikeneck.graalvm.nativeimage.options;

import java.util.function.Function;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.jetbrains.annotations.NotNull;

public interface Options {

  @NotNull
  @Internal
  Provider<TraceClassInitialization> traceClassInitialization();

  @NotNull
  @Internal
  default Provider<String> traceClassInitialization(@NotNull String option) {
    return this.traceClassInitialization(
        traceClassInitialization -> traceClassInitialization.option(option));
  }

  @NotNull
  @Internal
  Provider<String> traceClassInitialization(
      @NotNull
          Function<? super TraceClassInitialization, ? extends String>
              traceClassInitializationOption);
}
