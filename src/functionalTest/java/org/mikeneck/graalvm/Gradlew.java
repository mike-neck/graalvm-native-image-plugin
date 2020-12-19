package org.mikeneck.graalvm;

import org.gradle.testkit.runner.BuildResult;

@FunctionalInterface
public interface Gradlew {

    BuildResult invoke(String... params);
}
