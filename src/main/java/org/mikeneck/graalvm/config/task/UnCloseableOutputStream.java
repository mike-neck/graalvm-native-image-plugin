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
            public void close() { }
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
                return "UnCloseableOutputStream[delegateTo=" +
                        outputStream.toString() +
                        "]";
            }
        };
    }
}
