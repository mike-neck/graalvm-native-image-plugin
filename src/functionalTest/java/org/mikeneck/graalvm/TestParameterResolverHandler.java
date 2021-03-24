package org.mikeneck.graalvm;

import java.util.Arrays;
import org.gradle.testkit.runner.GradleRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TestParameterResolverHandler {
  GRADLE_WRAPPER {
    @Override
    boolean supports(@NotNull Class<?> parameterType) {
      return parameterType.equals(Gradlew.class);
    }

    @Override
    Object toParameter(@NotNull FunctionalTestContext ctx) {
      @SuppressWarnings("UnnecessaryLocalVariable")
      Gradlew gradlew =
          params ->
              GradleRunner.create()
                  .withProjectDir(ctx.rootDir.toFile())
                  .withArguments(params)
                  .withPluginClasspath()
                  .forwardOutput()
                  .build();
      return gradlew;
    }
  },
  TEST_CONTEXT {
    @Override
    boolean supports(@NotNull Class<?> parameterType) {
      return parameterType.equals(FunctionalTestContext.class);
    }

    @Override
    Object toParameter(@NotNull FunctionalTestContext ctx) {
      return ctx;
    }
  },
  ;

  abstract boolean supports(@NotNull final Class<?> parameterType);

  abstract Object toParameter(@NotNull final FunctionalTestContext ctx);

  static boolean hasSupportingMember(@NotNull final Class<?> parameterType) {
    return Arrays.stream(values()).anyMatch(member -> member.supports(parameterType));
  }

  @Nullable
  static TestParameterResolverHandler from(@NotNull final Class<?> parameterType) {
    return Arrays.stream(values())
        .filter(handler -> handler.supports(parameterType))
        .findAny()
        .orElse(null);
  }
}
