package org.mikeneck.graalvm;

import java.io.File;
import org.gradle.api.tasks.OutputDirectory;

interface JavaExecutionOutput {

  @OutputDirectory
  File getOutputDirectory();
}
