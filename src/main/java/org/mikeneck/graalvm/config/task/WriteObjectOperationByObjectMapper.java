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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.jetbrains.annotations.NotNull;

public class WriteObjectOperationByObjectMapper<T> implements WriteObjectOperation<T> {

    @NotNull
    private final ObjectMapper objectMapper;

    public WriteObjectOperationByObjectMapper(@NotNull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void write(@NotNull UnCloseableOutputStream out, @NotNull T object) throws IOException {
        try (Writer writer = new OutputStreamWriter(out.asOutputStream())) {
            objectMapper.writer().writeValue(writer, object);
        }
    }
}
