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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public interface NativeImageArguments {

    default List<String> getArguments() {
        List<String> args = new ArrayList<>();
        args.add("-cp");
        args.add(classpath());
        args.add(outputPath());
        executableName().ifPresent(args::add);
        args.addAll(additionalArguments());
        args.add(mainClass());
        return Collections.unmodifiableList(args);
    }

    @NotNull
    String classpath();

    @NotNull
    String outputPath();

    @NotNull
    Optional<String> executableName();

    @NotNull
    List<String> additionalArguments();

    @NotNull
    String mainClass();
}