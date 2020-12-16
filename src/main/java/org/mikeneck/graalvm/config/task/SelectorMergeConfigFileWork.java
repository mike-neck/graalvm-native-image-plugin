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

import java.io.IOException;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.SelectableMergeableConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectorMergeConfigFileWork<C extends SelectableMergeableConfig<C>> implements MergeConfigFileWork<C> {

    private static final Logger logger = LoggerFactory.getLogger(SelectorMergeConfigFileWork.class);

    @NotNull private final String resourceName;

    @NotNull private final ResourceCollection<FileInput> inputsFiles;

    @NotNull private final MappingCandidates<FileInput, C> mappingCandidates;

    @NotNull private final WriteObjectOperation<C> outputStreamOperation;

    @NotNull private final FileOutput outputFile;

    SelectorMergeConfigFileWork(
            @NotNull String resourceName,
            @NotNull ResourceCollection<FileInput> inputsFiles,
            @NotNull MappingCandidates<FileInput, C> mappingCandidates,
            @NotNull WriteObjectOperation<C> outputStreamOperation,
            @NotNull FileOutput outputFile) {
        this.resourceName = resourceName;
        this.inputsFiles = inputsFiles;
        this.mappingCandidates = mappingCandidates;
        this.outputStreamOperation = outputStreamOperation;
        this.outputFile = outputFile;
    }

    @Override
    public void run() throws IOException {
        Iterable<C> objects = inputsFiles.applyAll(mappingCandidates);
        if (objects.spliterator().estimateSize() == 0) {
            return;
        }
        try {
            C object = StreamSupport.stream(objects.spliterator(), false)
                    .reduce((left, right) -> {
                        if (left.canBeMergeWith(right)) {
                            return left.mergeWith(right);
                        } else {
                            throw new IllegalStateException(String.format("illegal state of %s: %s cannot be merge with %s", resourceName, left, right));
                        }
                    }).orElseThrow(() -> new IllegalStateException("no resources"));
            logger.info("writing merged config file {} to {}", object.getClass().getSimpleName(), outputFile);
            outputFile.withOutputStream(outputStream -> outputStreamOperation.write(outputStream, object));
        } catch (IllegalStateException e) {
            throw new IOException(String.format("failed to write %s resource", resourceName), e);
        }
    }
}
