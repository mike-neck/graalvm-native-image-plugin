package org.mikeneck.graalvm.config.task;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CollectionProcessorTest {

    @Test
    void expectedCaseWithSingleCandidate() throws IOException {
        List<String> inputs = Arrays.asList("expected-foo", "expected-bar", "expected-baz", "expected-qux-quux");
        CollectionProcessor<String> processor = inputs::iterator;
        MappingCandidates<String, String> mappingCandidates =
                MappingCandidates.forTest(
                        input -> {
                            Mapper<String, String> mapper = string -> {
                                String newValue = Arrays.stream(string.split("-"))
                                        .skip(1)
                                        .collect(Collectors.joining("-"));
                                if (newValue.isEmpty()) {
                                    throw new IOException(String.format("%s does not have character[-]", string));
                                }
                                return newValue;
                            };
                            return mapper.workOptional(input)
                                    .map(first -> Outcome.forTest(first, mapper));
                        }
                );

        Iterable<String> result = processor.run("expected strings", mappingCandidates);

        assertThat(result)
                .containsExactly("foo", "bar", "baz", "qux-quux");
    }

    @Test
    void hasFailureInputForSingleCandidate() {
        List<String> inputs = Arrays.asList("expected-foo", "expected-bar", "error_baz", "expected-qux-quux");
        CollectionProcessor<String> processor = inputs::iterator;
        MappingCandidates<String, String> mappingCandidates =
                MappingCandidates.forTest(
                        input -> {
                            Mapper<String, String> mapper = string -> {
                                String newValue = Arrays.stream(string.split("-"))
                                        .skip(1)
                                        .collect(Collectors.joining("-"));
                                if (newValue.isEmpty()) {
                                    throw new IOException(String.format("%s does not have character[-]", string));
                                }
                                return newValue;
                            };
                            return mapper.workOptional(input)
                                    .map(first -> Outcome.forTest(first, mapper));
                        }
                );

        IOException ioException = assertThrows(
                IOException.class,
                () -> processor.run("has failure @3", mappingCandidates)
        );
        assertThat(ioException)
                .hasMessageContaining("error_baz")
                .hasMessageContaining("has failure @3");
    }

    @Test
    void expectedCaseWithSecondCandidate() throws IOException {
        List<String> inputs = Arrays.asList("expected-foo", "expected-bar", "expected-baz", "expected-qux-quux");
        CollectionProcessor<String> processor = inputs::iterator;
        MappingCandidates<String, String> mappingCandidates =
                MappingCandidates.forTest(
                        input -> {
                            Mapper<String, String> mapper = string -> {
                                String newValue = Arrays.stream(string.split("/"))
                                        .skip(1)
                                        .collect(Collectors.joining("/"));
                                if (newValue.isEmpty()) {
                                    throw new IOException(String.format("%s does not have character[/]", string));
                                }
                                return newValue;
                            };
                            return mapper.workOptional(input)
                                    .map(first -> Outcome.forTest(first, mapper));
                        },
                        input -> {
                            Mapper<String, String> mapper = string -> {
                                String newValue = Arrays.stream(string.split("-"))
                                        .skip(1)
                                        .collect(Collectors.joining("-"));
                                if (newValue.isEmpty()) {
                                    throw new IOException(String.format("%s does not have character[-]", string));
                                }
                                return newValue;
                            };
                            return mapper.workOptional(input)
                                    .map(first -> Outcome.forTest(first, mapper));
                        }
                );

        Iterable<String> result = processor.run("expected strings", mappingCandidates);

        assertThat(result)
                .containsExactly("foo", "bar", "baz", "qux-quux");
    }

    @Test
    void expectedCaseEmptyInput() throws IOException {
        List<String> inputs = Collections.emptyList();
        CollectionProcessor<String> processor = inputs::iterator;
        MappingCandidates<String, String> mappingCandidates =
                MappingCandidates.forTest(
                        input -> {
                            Mapper<String, String> mapper = string -> {
                                String newValue = Arrays.stream(string.split("-"))
                                        .skip(1)
                                        .collect(Collectors.joining("-"));
                                if (newValue.isEmpty()) {
                                    throw new IOException(String.format("%s does not have character[-]", string));
                                }
                                return newValue;
                            };
                            return mapper.workOptional(input)
                                    .map(first -> Outcome.forTest(first, mapper));
                        }
                );

        Iterable<String> result = processor.run("expected strings", mappingCandidates);

        assertThat(result).isEmpty();
    }
}
