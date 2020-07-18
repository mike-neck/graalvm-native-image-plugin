/*
 * Copyright 2020 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.graalvm;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

class FunctionalTestContext {

    private final String resourceRoot;
    final Path rootDir;

    FunctionalTestContext(String resourceRoot) {
        this(resourceRoot, resourceRoot);
    }

    FunctionalTestContext(String resourceRoot, String path) {
        this(resourceRoot, Paths.get("build/functionalTest", path));
    }

    FunctionalTestContext(String resourceRoot, Path rootDir) {
        this.resourceRoot = resourceRoot;
        this.rootDir = rootDir;
    }

    private void createProjectRoot(Path projectDir) {
        try {
            Files.createDirectories(projectDir);
            writeString(
                    projectDir.resolve("settings.gradle"),
                    String.format("rootProject.name = '%s'", projectDir.getFileName()));
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

    static void writeString(Path file, String string) throws IOException {
        Files.write(file, Collections.singleton(string));
    }

    void setup() {
        try (ScanResult scanResult = new ClassGraph()
                .whitelistPaths(resourceRoot)
                .scan()) {
            createProjectRoot(rootDir);
            createBuildDir(rootDir);
            scanResult
                    .getAllResources()
                    .forEachInputStream((resource, inputStream) -> {
                        String path = resource.getPath()
                                .replace(String.format("%s/", resourceRoot), "")
                                .replaceAll("_", "/")
                                .replaceAll("\\.txt", "")
                                .replaceAll("-", ".")
                                .replaceAll("\\.\\.", "-");
                        System.out.println(path);
                        Path file = rootDir.resolve(path);
                        Path parent = file.getParent();
                        io(() -> {
                            if (!Files.exists(parent)) {
                                Files.createDirectories(parent);
                            }
                            Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
                        });
                    }, false);
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
    static  <T extends Throwable> void rethrow0(Throwable throwable) throws T {
        throw (T) throwable;
    }
}
