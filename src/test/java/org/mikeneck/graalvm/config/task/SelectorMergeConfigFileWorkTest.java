package org.mikeneck.graalvm.config.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mikeneck.graalvm.config.SelectableMergeableConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mikeneck.graalvm.config.task.FileInputToResourceConfig$20$3$MappingForTest.$20$3ResourceConfig;
import static org.mikeneck.graalvm.config.task.FileInputToResourceConfigMappingForTest.oldResourceConfig;

class SelectorMergeConfigFileWorkTest {

    private static <T> void writeObject(@NotNull UnCloseableOutputStream out, @NotNull T object) {
        PrintWriter printWriter = new PrintWriter(
                new OutputStreamWriter(
                        out.asOutputStream(),
                        StandardCharsets.UTF_8));
        printWriter.println(object);
        printWriter.flush();
    }

    private static <T> WriteObjectOperation<T> writeObjectOperation() {
        return SelectorMergeConfigFileWorkTest::writeObject;
    }

    @DisplayName("parsing order:[ old resource-config -> 20-3 resource-config ], resources: [old resource-config, old resource-config], expects: merged old resource config")
    @Test
    void expectedSucceedCase1() throws IOException {
        MappingCandidates<FileInput, SelectableMergeableConfig<?>> mappingCandidates = MappingCandidates.forTest(
                new FileInputToResourceConfigMappingForTest(),
                new FileInputToResourceConfig$20$3$MappingForTest()
        );
        List<FileInput> resources = Arrays.asList(
                oldResourceConfig("resource,resource-foo", "bundle-foo"),
                oldResourceConfig("resource,resource-bar", ""));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileOutput fileOutput = () -> out;

        @SuppressWarnings({"unchecked", "rawtypes"})
        SelectorMergeConfigFileWork<?> mergeConfigFileWork = new SelectorMergeConfigFileWork(
                "test",
                ResourceCollection.create("test", resources),
                mappingCandidates,
                writeObjectOperation(),
                fileOutput);

        mergeConfigFileWork.run();

        String output = out.toString("UTF-8");

        assertThat(output)
                .hasLineCount(1)
                .contains("ResourceConfig", "resource", "resource-foo", "resource-bar", "bundle-foo");
    }

    @DisplayName("parsing order:[ old resource-config -> 20-3 resource-config ],  resources: [20-3 resource-config, 20-3 resource-config], expects: merged 20-3 resource config")
    @Test
    void expectedSucceedCase2() throws IOException {
        MappingCandidates<FileInput, SelectableMergeableConfig<?>> mappingCandidates = MappingCandidates.forTest(
                new FileInputToResourceConfigMappingForTest(),
                new FileInputToResourceConfig$20$3$MappingForTest()
        );
        List<FileInput> resources = Arrays.asList(
                $20$3ResourceConfig("in-foo,in-bar", "ex-foo", ""),
                $20$3ResourceConfig("in-baz,in-foo", "ex-bar,ex-baz", "bundle-foo"),
                $20$3ResourceConfig("in-foo", "ex-bar", "bundle-bar,bundle-foo"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileOutput fileOutput = () -> out;

        @SuppressWarnings({"unchecked", "rawtypes"})
        SelectorMergeConfigFileWork<?> mergeConfigFileWork = new SelectorMergeConfigFileWork(
                "test",
                ResourceCollection.create("test", resources),
                mappingCandidates,
                writeObjectOperation(),
                fileOutput);

        mergeConfigFileWork.run();

        String output = out.toString("UTF-8");

        assertThat(output)
                .hasLineCount(1)
                .contains("$20$3", "in-foo", "in-bar", "in-baz", "ex-bar", "ex-baz", "ex-foo", "bundle-foo");
    }
}