package org.mikeneck.graalvm;

import java.util.Arrays;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.jetbrains.annotations.NotNull;

public class GenerateNativeImageConfigSemaphoreTask extends DefaultTask {

  @NotNull private final GenerateNativeImageConfigTask generateNativeImageConfigTask;

  @NotNull private final MergeNativeImageConfigTask mergeNativeImageConfigTask;

  @Inject
  public GenerateNativeImageConfigSemaphoreTask(
      @NotNull GenerateNativeImageConfigTask generateNativeImageConfigTask,
      @NotNull MergeNativeImageConfigTask mergeNativeImageConfigTask) {
    this.generateNativeImageConfigTask = generateNativeImageConfigTask;
    this.mergeNativeImageConfigTask = mergeNativeImageConfigTask;
    setDependsOn(Arrays.asList(generateNativeImageConfigTask, mergeNativeImageConfigTask));
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    generateNativeImageConfigTask.setEnabled(enabled);
    mergeNativeImageConfigTask.setEnabled(enabled);
  }
}
