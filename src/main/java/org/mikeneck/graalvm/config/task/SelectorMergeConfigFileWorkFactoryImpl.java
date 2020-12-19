package org.mikeneck.graalvm.config.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.SelectableMergeableConfig;

public class SelectorMergeConfigFileWorkFactoryImpl implements SelectorMergeConfigFileWorkFactory {

    @Override
    public FileInputsSelection resource(@NotNull String resourceName) {
        ObjectMapper objectMapper = new ObjectMapper();
        return files -> new SelectorMergeConfigFileWorkFactory.MappingCandidateSelection() {
            @Override
            public final <C extends SelectableMergeableConfig<C>> FileOutputSelection<C> resourceCandidateTypes(@NotNull TypeProvider... klasses) {
                return fileOutput -> {
                    @SuppressWarnings("unchecked")
                    List<MappingCandidate<FileInput, C>> candidates = Arrays.stream(klasses)
                            .map(TypeProvider::get)
                            .<MappingCandidate<FileInput, C>>map(klass -> new MappingCandidateByObjectMapper<>((Class<C>) klass, objectMapper))
                            .collect(Collectors.toList());
                    return new SelectorMergeConfigFileWork<C>(
                            resourceName,
                            ResourceCollection.create(resourceName, files),
                            candidates::iterator,
                            new WriteObjectOperationByObjectMapper<>(objectMapper),
                            fileOutput);
                };
            }
        };
    }
}
