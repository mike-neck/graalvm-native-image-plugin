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
package org.mikeneck.graalvm.nativeimage.options;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.GraalVmVersion;

public interface TraceClassInitialization {

    /**
     * Returns {@code TraceClassInitialization} option.
     * This method should be aware of a using GraalVM's version.
     * <p>
     * With GraalVM version previous than 20.3, calling this method with some parameters results a boolean option String.
     * Calling this method without any parameters results a boolean option String too.
     * 
     * <p>
     * With GraalVM version later than or equals to 20.3, calling this method with some parameters results a comma separated String of given FQCNs. 
     * Calling this method without any parameters results an empty String, thus the option will be ignored.
     * 
     * @param classNames - FQCNs of classes initialization is traced for.
     * @return - {@code TraceClassInitialization} option.
     */
    @NotNull
    String option(@NotNull String... classNames);

    static TraceClassInitialization of(GraalVmVersion version) {
        if (version.lessThan(GraalVmVersion.GRAAL_20_3_0_JAVA_8) || version.lessThan(GraalVmVersion.GRAAL_20_3_0_JAVA_11)) {
            return classNames -> "-H:+TraceClassInitialization";
        } else {
            return classNames -> {
                if (classNames.length == 0) {
                    return "";
                }
                return Arrays.stream(classNames).collect(Collectors.joining(",", "-H:TraceClassInitialization=", ""));
            };
        }
    }
}
