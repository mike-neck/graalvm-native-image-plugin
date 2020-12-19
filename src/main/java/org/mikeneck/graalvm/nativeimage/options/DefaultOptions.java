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

import java.util.function.Function;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.GraalVmVersion;

public class DefaultOptions implements Options {

    private final Provider<GraalVmVersion> graalVmVersion;

    public DefaultOptions(Provider<GraalVmVersion> graalVmVersion) {
        this.graalVmVersion = graalVmVersion;
    }

    public Provider<GraalVmVersion> getGraalVmVersion() {
        return graalVmVersion;
    }

    @Override
    @NotNull
    @Internal
    public Provider<TraceClassInitialization> traceClassInitialization() {
        return getGraalVmVersion().map(TraceClassInitialization::of);
    }

    @Override
    @NotNull
    @Internal
    public Provider<String> traceClassInitialization(@NotNull Function<@NotNull ? super TraceClassInitialization, @NotNull ? extends String> traceClassInitializationOption) {
        return this.traceClassInitialization()
                .map(traceClassInitialization -> {
                    @NotNull
                    String option = traceClassInitializationOption.apply(traceClassInitialization);
                    return option;
                });
    }
}
