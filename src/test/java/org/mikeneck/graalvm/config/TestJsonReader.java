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
