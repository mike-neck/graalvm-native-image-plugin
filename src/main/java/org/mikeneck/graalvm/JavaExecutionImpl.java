package org.mikeneck.graalvm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.process.JavaExecSpec;
import org.jetbrains.annotations.NotNull;

public class JavaExecutionImpl implements JavaExecution, Action<JavaExecSpec> {

  final int index;
  private final ConfigurableFileCollection jarFile;
  private final Provider<Configuration> runtimeClasspath;
  private final Provider<String> mainClass;
  private final Supplier<GraalVmHome> graalVmHome;
  final File outputDirectory;
  private final Property<byte[]> inputStream;
  final List<String> arguments;
  final Map<String, String> env;

  JavaExecutionImpl(
      int index,
      @NotNull ConfigurableFileCollection jarFile,
      @NotNull Provider<Configuration> runtimeClasspath,
      @NotNull Provider<String> mainClass,
      @NotNull Supplier<GraalVmHome> graalVmHome,
      @NotNull File outputDirectory,
      @NotNull Property<byte[]> inputStream) {
    this.index = index;
    this.jarFile = jarFile;
    this.runtimeClasspath = runtimeClasspath;
    this.mainClass = mainClass;
    this.graalVmHome = graalVmHome;
    this.outputDirectory = outputDirectory;
    this.inputStream = inputStream;
    this.arguments = new ArrayList<>();
    this.env = new HashMap<>();
  }

  @Override
  public void arguments(Iterable<String> args) {
    for (String arg : args) {
      arguments.add(arg);
    }
  }

  @Override
  public void stdIn(byte[] input) {
    inputStream.set(input);
  }

  @Override
  public void environment(Map<String, String> env) {
    this.env.putAll(env);
  }

  @Override
  public void execute(@NotNull JavaExecSpec javaExecSpec) {
    javaExecSpec.setIgnoreExitValue(true);
    if (!arguments.isEmpty()) {
      javaExecSpec.args(arguments);
    }
    Path javaExecutable =
        graalVmHome
            .get()
            .javaExecutable()
            .orElseThrow(() -> new IllegalStateException("GraalVM Java not found"));
    javaExecSpec.setExecutable(javaExecutable);
    javaExecSpec.setMain(mainClass.get());
    javaExecSpec.classpath(jarFile, runtimeClasspath.get());
    javaExecSpec.environment(env);
    Path outputDir = outputDirectory.toPath();
    javaExecSpec.jvmArgs(
        String.format("-agentlib:native-image-agent=config-output-dir=%s", outputDir));
    javaExecSpec.setStandardInput(inputStream());
  }

  private InputStream inputStream() {
    ByteArrayInputStream defaultInputStream = new ByteArrayInputStream(new byte[0]);
    return inputStream.map(ByteArrayInputStream::new).getOrElse(defaultInputStream);
  }

  @Internal
  @Deprecated
  public int getIndex() {
    return index;
  }

  @InputFiles
  @Deprecated
  public ConfigurableFileCollection getJarFile() {
    return jarFile;
  }

  @InputFiles
  @Deprecated
  public Provider<Configuration> getRuntimeClasspath() {
    return runtimeClasspath;
  }

  @Input
  @Deprecated
  public Provider<String> getMainClass() {
    return mainClass;
  }

  @Internal
  @Deprecated
  public Supplier<GraalVmHome> getGraalVmHome() {
    return graalVmHome;
  }

  @OutputDirectory
  public File getOutputDirectory() {
    return outputDirectory;
  }

  @Input
  @Deprecated
  public Property<byte[]> getInputStream() {
    return inputStream;
  }

  @Input
  @Deprecated
  public List<String> getArguments() {
    return arguments;
  }

  @Input
  @Deprecated
  public Map<String, String> getEnv() {
    return env;
  }

  @SuppressWarnings("StringBufferReplaceableByString")
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("execution {");
    sb.append("index=").append(index);
    sb.append(", outputDir='").append(outputDirectory).append("'");
    sb.append(", arguments=").append(arguments);
    sb.append('}');
    return sb.toString();
  }
}
