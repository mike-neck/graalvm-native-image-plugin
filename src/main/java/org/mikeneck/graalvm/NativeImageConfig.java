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

import java.io.File;
import java.nio.file.Path;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.bundling.Jar;

public interface NativeImageConfig {

    void setGraalVmHome(String graalVmHome);

    void setJarTask(Jar jarTask);

    void setMainClass(String mainClass);

    void setExecutableName(String name);

    void setRuntimeClasspath(Configuration configuration);

    void setOutputDirectory(File directory);

    void setOutputDirectory(Path directory);

    void setOutputDirectory(String directory);

    void setOutputDirectory(Provider<Directory> directory);

    void arguments(String... arguments);
}
