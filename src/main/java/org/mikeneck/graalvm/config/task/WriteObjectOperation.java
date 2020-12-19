package org.mikeneck.graalvm.config.task;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public interface WriteObjectOperation<T> {

    void write(@NotNull UnCloseableOutputStream out, @NotNull T object) throws IOException;
}
