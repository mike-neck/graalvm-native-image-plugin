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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.api.tasks.TaskAction;

public class DefaultInstallNativeImageTask extends DefaultTask implements InstallNativeImageTask {

    private final Provider<GraalVmHome> graalVmHome;

    @Inject
    public DefaultInstallNativeImageTask(Provider<GraalVmHome> graalVmHome) {
        this.graalVmHome = graalVmHome;
        getOutputs().upToDateWhen(task -> 
                nativeImageCommand()
                        .filter(Files::exists)
                        .isPresent());
    }

    @TaskAction
    public void installNativeImage() {
        Logger logger = getLogger();
        logger.info("installing native-image at GraalVm:{}", this.graalVmHome.getOrNull());
        GraalVmHome graalVmHome = this.graalVmHome.get();
        if (graalVmHome.notFound()) {
            logger.info("GRAALVM_HOME[{}] not found", graalVmHome);
            throw new InvalidUserDataException(String.format("graalVM not found at %s", graalVmHome));
        }
        Optional<Path> nativeImageCommand = nativeImageCommand();
        if (nativeImageCommand.isPresent()) {
            Path nativeImage = nativeImageCommand.get();
            logger.info("native-image command exists: {}", nativeImage);
            throw new StopExecutionException(
                    String.format("native-image command exists at %s", nativeImage));
        }
        Path graalVmUpdaterCommand = graalVmUpdaterCommand()
                .orElseThrow(() -> new InvalidUserDataException(
                        String.format("graalVM updater command does not exist in %s.", graalVmHome)));
        logger.info("found graalVM updater command: {}", graalVmUpdaterCommand);
        getProject().exec(execSpec -> {
            execSpec.setExecutable(graalVmUpdaterCommand);
            execSpec.args("install", "native-image");
        });
    }

    @Override
    public Provider<GraalVmHome> getGraalVmHome() {
        return graalVmHome;
    }

    Optional<Path> graalVmUpdaterCommand() {
        return graalVmHome.get().graalVmUpdater();
    }

    Optional<Path> nativeImageCommand() {
        return graalVmHome.get().nativeImage();
    }
}
