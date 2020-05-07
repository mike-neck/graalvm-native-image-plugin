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

import java.nio.file.Path;
import java.util.Optional;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.api.tasks.TaskAction;

public class InstallNativeImageTask extends DefaultTask {

    private final Property<NativeImageExtension> extension;

    @Inject
    public InstallNativeImageTask(Project project) {
        this.extension = project.getObjects().property(NativeImageExtension.class);
    }

    void setExtension(NativeImageExtension extension) {
        this.extension.set(extension);
    }

    @TaskAction
    public void installNativeImage() {
        GraalVmHome graalVmHome = graalVmHome();
        if (graalVmHome.notFound()) {
            getLogger().info("GRAALVM_HOME[{}] not found", graalVmHome);
            throw new InvalidUserDataException(String.format("graalVM not found at %s", graalVmHome));
        }
        Optional<Path> nativeImageCommand = nativeImageCommand();
        if (nativeImageCommand.isPresent()) {
            Path nativeImage = nativeImageCommand.get();
            getLogger().info("native-image command exists: {}", nativeImage);
            throw new StopExecutionException(
                    String.format("native-image command exists at %s", nativeImage));
        }
        Path graalVmUpdaterCommand = graalVmUpdaterCommand()
                .orElseThrow(() -> new InvalidUserDataException(
                        String.format("graalVM updater command does not exist in %s.", graalVmHome)));
        getLogger().info("found graalVM updater command: {}", graalVmUpdaterCommand);
        getProject().exec(execSpec -> {
            execSpec.setExecutable(graalVmUpdaterCommand);
            execSpec.args("install", "native-image");
        });
    }

    private GraalVmHome graalVmHome() {
        return extension.get().graalVmHome();
    }

    Optional<Path> graalVmUpdaterCommand() {
        return graalVmHome().graalVmUpdater();
    }

    Optional<Path> nativeImageCommand() {
        return graalVmHome().nativeImage();
    }
}
