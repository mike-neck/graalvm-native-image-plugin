package org.mikeneck.graalvm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraalVmHomeTest {

    @Test
    void nativeImageOnNixLikeOs() throws IOException {
        Path graalVmHome = Files.createTempDirectory("test-on_nixLikeOs");
        Path binDirectory = Files.createDirectory(graalVmHome.resolve("bin"));
        Files.createFile(binDirectory.resolve("native-image"));

        GraalVmHome home = new GraalVmHome(graalVmHome);

        Optional<Path> nativeImage = home.nativeImage();

        assertTrue(nativeImage.isPresent());
    }

    @Test
    void nativeImageOnWindows() throws IOException {
        Path graalVmHome = Files.createTempDirectory("test-onWindows");
        Path binDirectory = Files.createDirectory(graalVmHome.resolve("bin"));
        Files.createFile(binDirectory.resolve("native-image.cmd"));

        GraalVmHome home = new GraalVmHome(graalVmHome);

        Optional<Path> nativeImage = home.nativeImage();

        assertTrue(nativeImage.isPresent());
    }

    @Test
    void javaExecutableOnNixLikeOs() throws IOException {
        Path graalVmHome = Files.createTempDirectory("test-on_nixLikeOs");
        Path binDirectory = Files.createDirectory(graalVmHome.resolve("bin"));
        Files.createFile(binDirectory.resolve("java"));

        GraalVmHome home = new GraalVmHome(graalVmHome);

        Optional<Path> javaExecutable = home.javaExecutable();

        assertTrue(javaExecutable.isPresent());
    }

    @Test
    void javaExecutableOnWindows() throws IOException {
        Path graalVmHome = Files.createTempDirectory("test-onWindows");
        Path binDirectory = Files.createDirectory(graalVmHome.resolve("bin"));
        Files.createFile(binDirectory.resolve("java.exe"));

        GraalVmHome home = new GraalVmHome(graalVmHome);

        Optional<Path> javaExecutable = home.javaExecutable();

        assertTrue(javaExecutable.isPresent());
    }

    @Test
    void invalidHome() throws IOException {
        Path graalVmHome = Files.createTempDirectory("test-onWindows");
        Files.createDirectory(graalVmHome.resolve("bin"));

        GraalVmHome home = new GraalVmHome(graalVmHome);

        Optional<Path> nativeImage = home.nativeImage();

        assertFalse(nativeImage.isPresent());
    }
}
