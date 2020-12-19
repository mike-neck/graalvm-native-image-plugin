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
