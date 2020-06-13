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
package org.mikeneck.graalvm.config.task;

import java.io.File;
import org.gradle.api.provider.Provider;
import org.jetbrains.annotations.NotNull;

public interface ConfigFileConfiguration {

    @NotNull
    Provider<File> provider(File file);

    default void addJniConfig(File jniConfig) {
        addJniConfig(provider(jniConfig));
    }

    default void addProxyConfig(File proxyConfig) {
        addProxyConfig(provider(proxyConfig));
    }

    default void addReflectConfig(File reflectConfig) {
        addReflectConfig(provider(reflectConfig));
    }

    default void addResourceConfig(File resourceConfig) {
        addResourceConfig(provider(resourceConfig));
    }

    void addJniConfig(Provider<File> jniConfig);

    void addProxyConfig(Provider<File> proxyConfig);

    void addReflectConfig(Provider<File> reflectConfig);

    void addResourceConfig(Provider<File> resourceConfig);
}
