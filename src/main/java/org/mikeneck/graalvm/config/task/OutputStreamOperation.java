package org.mikeneck.graalvm.config.task;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface OutputStreamOperation {

    void consume(@NotNull UnCloseableOutputStream outputStream) throws IOException;
}
