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
package org.mikeneck.graalvm.nativeimage;

import java.util.Arrays;
import java.util.Collection;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.jetbrains.annotations.NotNull;

public class UnixLikeOsArgumentsFactory implements NativeImageArgumentsFactory {

    private static final Collection<OperatingSystem> SUPPORTING_OS =
            Arrays.asList(OperatingSystem.LINUX, OperatingSystem.MACOSX);

    @Override
    public boolean supports(@NotNull OperatingSystem os) {
        return SUPPORTING_OS.contains(os);
    }

    @Override
    public @NotNull NativeImageArguments create(
            @NotNull Property<Configuration> runtimeClasspath,
            @NotNull Property<String> mainClass,
            @NotNull ConfigurableFileCollection jarFile,
            @NotNull DirectoryProperty outputDirectory,
            @NotNull Property<String> executableName,
            @NotNull ListProperty<String> additionalArguments,
            @NotNull ConfigurationFiles configurationFiles) {
        return new UnixLikeOsArguments(
                runtimeClasspath, 
                mainClass, 
                jarFile, 
                outputDirectory, 
                executableName, 
                additionalArguments,
                configurationFiles);
    }
}
