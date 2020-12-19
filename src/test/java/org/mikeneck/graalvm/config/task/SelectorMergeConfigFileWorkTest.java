package org.mikeneck.graalvm.config.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mikeneck.graalvm.config.task.FileInputToResourceConfig$20$3$MappingForTest.$20$3ResourceConfig;
import static org.mikeneck.graalvm.config.task.FileInputToResourceConfigMappingForTest.oldResourceConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mikeneck.graalvm.config.SelectableMergeableConfig;

class SelectorMergeConfigFileWorkTest {

  private static final MappingCandidate<FileInput, SelectableMergeableConfig<?>>
      olfResourceConfigParser = new FileInputToResourceConfigMappingForTest();
  private static final MappingCandidate<FileInput, SelectableMergeableConfig<?>>
      $20$3ResourceConfigParser = new FileInputToResourceConfig$20$3$MappingForTest();

  private static <T> void writeObject(@NotNull UnCloseableOutputStream out, @NotNull T object) {
    PrintWriter printWriter =
        new PrintWriter(new OutputStreamWriter(out.asOutputStream(), StandardCharsets.UTF_8));
    printWriter.println(object);
    printWriter.flush();
  }

  private static <T> WriteObjectOperation<T> writeObjectOperation() {
    return SelectorMergeConfigFileWorkTest::writeObject;
  }

  @SafeVarargs
  @NotNull
  private static MappingCandidates<FileInput, SelectableMergeableConfig<?>> parserUsing(
      @NotNull MappingCandidate<FileInput, SelectableMergeableConfig<?>>... parsers) {
    return MappingCandidates.forTest(parsers);
  }

  @DisplayName(
      "parsing order:[ old resource-config -> 20-3 resource-config ], resources: [old resource-config, old resource-config], expects: merged old resource config")
  @Test
  void expectedSucceedCase1() throws IOException {
    MappingCandidates<FileInput, SelectableMergeableConfig<?>> mappingCandidates =
        parserUsing(olfResourceConfigParser, $20$3ResourceConfigParser);
    List<FileInput> resources =
        Arrays.asList(
            oldResourceConfig("resource,resource-foo", "bundle-foo"),
            oldResourceConfig("resource,resource-bar", ""));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    FileOutput fileOutput = () -> out;

    @SuppressWarnings({"unchecked", "rawtypes"})
    SelectorMergeConfigFileWork<?> mergeConfigFileWork =
        new SelectorMergeConfigFileWork(
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

  @DisplayName(
      "parsing order:[ old resource-config -> 20-3 resource-config ],  resources: [20-3 resource-config, 20-3 resource-config], expects: merged 20-3 resource config")
  @Test
  void expectedSucceedCase2() throws IOException {
    MappingCandidates<FileInput, SelectableMergeableConfig<?>> mappingCandidates =
        parserUsing(olfResourceConfigParser, $20$3ResourceConfigParser);
    List<FileInput> resources =
        Arrays.asList(
            $20$3ResourceConfig("in-foo,in-bar", "ex-foo", ""),
            $20$3ResourceConfig("in-baz,in-foo", "ex-bar,ex-baz", "bundle-foo"),
            $20$3ResourceConfig("in-foo", "ex-bar", "bundle-bar,bundle-foo"));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    FileOutput fileOutput = () -> out;

    @SuppressWarnings({"unchecked", "rawtypes"})
    SelectorMergeConfigFileWork<?> mergeConfigFileWork =
        new SelectorMergeConfigFileWork(
            "test",
            ResourceCollection.create("test", resources),
            mappingCandidates,
            writeObjectOperation(),
            fileOutput);

    mergeConfigFileWork.run();

    String output = out.toString("UTF-8");

    assertThat(output)
        .hasLineCount(1)
        .contains(
            "$20$3", "in-foo", "in-bar", "in-baz", "ex-bar", "ex-baz", "ex-foo", "bundle-foo");
  }

  @DisplayName(
      "parsing order:[ old resource-config -> 20-3 resource-config ], resources: [old resource-config, , 20-3 resource-config], , expects: exception thrown")
  @TestFactory
  Iterable<DynamicTest> expectedFailureCase1() throws IOException {
    MappingCandidates<FileInput, SelectableMergeableConfig<?>> mappingCandidates =
        parserUsing(olfResourceConfigParser, $20$3ResourceConfigParser);
    List<FileInput> resources =
        Arrays.asList(
            oldResourceConfig("resource,resource-foo", "bundle-foo"),
            $20$3ResourceConfig("in-foo", "ex-bar", "bundle-bar,bundle-foo"));
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    FileOutput fileOutput = () -> out;

    @SuppressWarnings({"unchecked", "rawtypes"})
    SelectorMergeConfigFileWork<?> mergeConfigFileWork =
        new SelectorMergeConfigFileWork(
            "test",
            ResourceCollection.create("test", resources),
            mappingCandidates,
            writeObjectOperation(),
            fileOutput);

    return assertThrowsException(
            "expected IOException will be thrown", IOException.class, mergeConfigFileWork::run)
        .then(
            "it won't have any message",
            exception ->
                assertThat(exception)
                    .hasMessageContainingAll("has invalid resource", "$20$3Resource"));
  }

  @DisplayName(
      "parsing order:[ 20-3 resource-config ->  old resource-config ], resources: [old resource-config, , 20-3 resource-config], , expects: exception thrown")
  @TestFactory
  Iterable<DynamicTest> expectedFailureCase2() throws IOException {
    MappingCandidates<FileInput, SelectableMergeableConfig<?>> mappingCandidates =
        parserUsing($20$3ResourceConfigParser, olfResourceConfigParser);
    List<FileInput> resources =
        Arrays.asList(
            oldResourceConfig("resource,resource-foo", "bundle-foo"),
            $20$3ResourceConfig("in-foo", "ex-bar", "bundle-bar,bundle-foo"));
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    FileOutput fileOutput = () -> out;

    @SuppressWarnings({"unchecked", "rawtypes"})
    SelectorMergeConfigFileWork<?> mergeConfigFileWork =
        new SelectorMergeConfigFileWork(
            "test",
            ResourceCollection.create("test", resources),
            mappingCandidates,
            writeObjectOperation(),
            fileOutput);

    return assertThrowsException(
            "expected IOException will be thrown", IOException.class, mergeConfigFileWork::run)
        .then(
            "it won't have any message",
            exception ->
                assertThat(exception)
                    .hasMessageContainingAll("has invalid resource", "$20$3Resource"));
  }

  interface Testing<T> {
    void test(@NotNull T object) throws Throwable;
  }

  interface IfExceptionThrownThen<E extends Throwable> {
    Iterable<DynamicTest> then(@NotNull String description, @NotNull Testing<? super E> testing)
        throws E;
  }

  private static <E extends Throwable> IfExceptionThrownThen<E> assertThrowsException(
      @NotNull String description,
      @NotNull Class<E> klass,
      @NotNull Executable throwingExecutable) {
    return (nextDesc, testing) -> {
      Throwable[] exceptions = new Throwable[1];
      DynamicTest first =
          DynamicTest.dynamicTest(
              description,
              () -> {
                E exception = assertThrows(klass, throwingExecutable);
                exceptions[0] = exception;
              });
      DynamicTest second =
          DynamicTest.dynamicTest(
              nextDesc,
              () -> {
                @SuppressWarnings("unchecked")
                E throwable = (E) exceptions[0];
                if (throwable == null) {
                  return;
                }
                testing.test(throwable);
              });
      return Arrays.asList(first, second);
    };
  }
}
