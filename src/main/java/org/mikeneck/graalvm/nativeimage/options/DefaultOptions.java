package org.mikeneck.graalvm.nativeimage.options;

import java.util.function.Function;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.GraalVmVersion;

public class DefaultOptions implements Options {

  private final Provider<GraalVmVersion> graalVmVersion;

  public DefaultOptions(Provider<GraalVmVersion> graalVmVersion) {
    this.graalVmVersion = graalVmVersion;
  }

  public Provider<GraalVmVersion> getGraalVmVersion() {
    return graalVmVersion;
  }

  @Override
  @NotNull
  @Internal
  public Provider<TraceClassInitialization> traceClassInitialization() {
    return getGraalVmVersion().map(TraceClassInitialization::of);
  }

  @Override
  @NotNull
  @Internal
  public Provider<String> traceClassInitialization(
      @NotNull
          Function<@NotNull ? super TraceClassInitialization, @NotNull ? extends String>
              traceClassInitializationOption) {
    return this.traceClassInitialization()
        .map(
            traceClassInitialization -> {
              @NotNull
              String option = traceClassInitializationOption.apply(traceClassInitialization);
              return option;
            });
  }
}
