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

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.jetbrains.annotations.NotNull;

public class WindowsNativeImageArgumentsFactory implements NativeImageArgumentsFactory {

    @Override
    public boolean supports(@NotNull OperatingSystem os) {
        return os == OperatingSystem.WINDOWS;
    }

    @Override
    public @NotNull NativeImageArguments create(
            @NotNull Property<Configuration> runtimeClasspath,
            @NotNull Property<String> mainClass,
            @NotNull RegularFileProperty jarFile,
            @NotNull DirectoryProperty outputDirectory,
            @NotNull Property<String> executableName,
            @NotNull ListProperty<String> additionalArguments) {
        UnixLikeOsArguments delegate = 
                new UnixLikeOsArguments(
                        runtimeClasspath,
                        mainClass,
                        jarFile,
                        outputDirectory,
                        executableName,
                        additionalArguments);
        return new WindowsNativeImageArguments(delegate);
    }
}
