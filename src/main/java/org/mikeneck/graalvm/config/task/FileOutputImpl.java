package org.mikeneck.graalvm.config.task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class FileOutputImpl implements FileOutput {

    private final Path file;

    FileOutputImpl(Path file) {
        this.file = file;
    }

    @Override
    public OutputStream newOutputStream() throws IOException {
        return Files.newOutputStream(
                file,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public String toString() {
        @SuppressWarnings("StringBufferReplaceableByString") 
        final StringBuilder sb = new StringBuilder("FileOutputImpl{");
        sb.append(file);
        sb.append('}');
        return sb.toString();
    }
}
