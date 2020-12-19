package org.mikeneck.graalvm.config.task;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import org.jetbrains.annotations.NotNull;

public interface UnCloseableOutputStream extends Flushable {

  void write(int b) throws IOException;

  void write(byte[] bytes) throws IOException;

  void write(byte[] bytes, int offset, int length) throws IOException;

  default OutputStream asOutputStream() {
    UnCloseableOutputStream self = this;
    return new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        self.write(b);
      }

      @Override
      public void write(byte[] b) throws IOException {
        self.write(b);
      }

      @Override
      public void write(byte[] b, int off, int len) throws IOException {
        self.write(b, off, len);
      }

      @Override
      public void flush() throws IOException {
        self.flush();
      }

      @Override
      public void close() {}
    };
  }

  static UnCloseableOutputStream delegateTo(@NotNull OutputStream outputStream) {
    return new UnCloseableOutputStream() {
      @Override
      public void write(int b) throws IOException {
        outputStream.write(b);
      }

      @Override
      public void write(byte[] bytes) throws IOException {
        outputStream.write(bytes);
      }

      @Override
      public void write(byte[] bytes, int offset, int length) throws IOException {
        outputStream.write(bytes, offset, length);
      }

      @Override
      public void flush() throws IOException {
        outputStream.flush();
      }

      @Override
      public String toString() {
        return "UnCloseableOutputStream[delegateTo=" + outputStream.toString() + "]";
      }
    };
  }
}
