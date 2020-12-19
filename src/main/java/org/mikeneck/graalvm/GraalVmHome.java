package org.mikeneck.graalvm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.gradle.api.InvalidUserDataException;
import org.jetbrains.annotations.NotNull;

class GraalVmHome {

    private final Path graalVmHome;

    GraalVmHome(Path home) {
        graalVmHome = home;
    }

    boolean notFound() {
        return !exists();
    }

    boolean exists() {
        return Files.exists(graalVmHome);
    }

    @NotNull
    Optional<Path> graalVmUpdater() {
        return graalVmUpdaterCandidates()
                .stream()
                .filter(Files::exists)
                .findFirst();
    }

    @NotNull
    GraalVmVersion graalVmVersion() {
        try {
            return GraalVmVersion.findFromPath(graalVmHome);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserDataException("Invalid graalVmHome, release file not found", e);
        }
    }

    private List<Path> graalVmUpdaterCandidates() {
        return Arrays.asList(
                graalVmHome.resolve("bin/gu"),
                graalVmHome.resolve("bin/gu.cmd")
        );
    }

    @NotNull
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

    @NotNull
    Optional<Path> javaExecutable() {
        return javaExecutableCandidates()
                .stream()
                .filter(Files::exists)
                .findFirst();
    }

    private List<Path> javaExecutableCandidates() {
        return Arrays.asList(
                graalVmHome.resolve("bin/java"),
                graalVmHome.resolve("bin/java.exe")
        );
    }

    @Override
    public String toString() {
        return graalVmHome.toString();
    }
}
