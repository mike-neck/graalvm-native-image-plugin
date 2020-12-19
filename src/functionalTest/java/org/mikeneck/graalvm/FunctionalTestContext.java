package org.mikeneck.graalvm;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FunctionalTestContext {

  private final String resourceRoot;
  final Path rootDir;
  private final List<String> subprojects;

  FunctionalTestContext(String resourceRoot) {
    this(resourceRoot, resourceRoot);
  }

  FunctionalTestContext(String resourceRoot, String path) {
    this(resourceRoot, Paths.get("build/functionalTest", path));
  }

  FunctionalTestContext(String resourceRoot, Path rootDir) {
    this.resourceRoot = resourceRoot;
    this.rootDir = rootDir;
    this.subprojects = Collections.emptyList();
  }

  FunctionalTestContext(String resourceRoot, Collection<SubProject> subProjects) {
    this(resourceRoot, resourceRoot, subProjects);
  }

  FunctionalTestContext(String resourceRoot, String path, Collection<SubProject> subProjects) {
    this(resourceRoot, Paths.get("build/functionalTest", path), subProjects);
  }

  FunctionalTestContext(String resourceRoot, Path rootDir, Collection<SubProject> subProjects) {
    this.resourceRoot = resourceRoot;
    this.rootDir = rootDir;
    this.subprojects = subProjects.stream().map(SubProject::path).collect(Collectors.toList());
  }

  private void createProjectRoot(Path projectDir) {
    try {
      Files.createDirectories(projectDir);
      List<String> lines = new ArrayList<>(1 + subprojects.size());
      lines.add(String.format("rootProject.name = '%s'", projectDir.getFileName()));
      lines.addAll(
          subprojects.stream()
              .map(path -> String.format("include '%s'", path))
              .collect(Collectors.toList()));
      writeString(projectDir.resolve("settings.gradle"), Collections.unmodifiableList(lines));
    } catch (IOException e) {
      rethrow(e);
    }
  }

  private static void createBuildDir(Path projectDir) {
    Path buildDir = projectDir.resolve("build");
    try {
      if (!Files.exists(buildDir)) {
        Files.createDirectories(buildDir);
      }
    } catch (IOException e) {
      rethrow(e);
    }
  }

  static void writeString(Path file, Collection<String> strings) throws IOException {
    Files.write(file, strings);
  }

  void setup() {
    try (ScanResult scanResult = new ClassGraph().whitelistPaths(resourceRoot).scan()) {
      createProjectRoot(rootDir);
      createBuildDir(rootDir);
      scanResult
          .getAllResources()
          .forEachInputStream(
              (resource, inputStream) -> {
                String path =
                    resource
                        .getPath()
                        .replace(String.format("%s/", resourceRoot), "")
                        .replaceAll("_", "/")
                        .replaceAll("\\.txt", "")
                        .replaceAll("-", ".")
                        .replaceAll("\\.\\.", "-");
                System.out.println(path);
                Path file = rootDir.resolve(path);
                Path parent = file.getParent();
                io(
                    () -> {
                      if (!Files.exists(parent)) {
                        Files.createDirectories(parent);
                      }
                      Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
                    });
              },
              false);
    }
  }

  Path file(String relativePathFromProjectRoot) {
    return rootDir.resolve(relativePathFromProjectRoot);
  }

  String fileTextContentsOf(String relativePathFromProjectRoot) {
    Path file = file(relativePathFromProjectRoot);
    try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
      return lines.collect(Collectors.joining("\n"));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  interface IoOperation {
    void run() throws IOException;
  }

  static void io(IoOperation operation) {
    try {
      operation.run();
    } catch (IOException e) {
      rethrow(e);
    }
  }

  static void rethrow(Throwable throwable) {
    rethrow0(throwable);
  }

  @SuppressWarnings("unchecked")
  static <T extends Throwable> void rethrow0(Throwable throwable) throws T {
    throw (T) throwable;
  }
}
