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
package org.mikeneck.graalvm.config;

import java.io.InputStream;

public class TestJsonReader {

    private final ClassLoader classLoader = getClass().getClassLoader();

    InputStream configJsonResource(String name) {
        InputStream inputStream = classLoader.getResourceAsStream(name);
        if (inputStream == null) {
            throw new IllegalStateException(String.format("%s not found", name));
        }
        return inputStream;
    }
}
