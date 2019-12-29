/*
 * Copyright 2019 Shinya Mochida
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class GraalVmHome {

    private final Path graalVmHome;

    GraalVmHome(Path home) {
        graalVmHome = home;
    }

    Optional<Path> nativeImage() {
        return nativeImageCandidates()
                .stream()
                .filter(Files::exists)
                .findFirst();
    }

    private List<Path> nativeImageCandidates() {
        return Arrays.asList(
                graalVmHome.resolve("bin/native-image"),
                graalVmHome.resolve("bin/native-image.cmd")
        );
    }

    @Override
    public String toString() {
        return graalVmHome.toString();
    }
}
