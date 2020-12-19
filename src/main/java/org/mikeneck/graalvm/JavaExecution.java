package org.mikeneck.graalvm;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public interface JavaExecution {

  default void arguments(String... args) {
    arguments(Arrays.asList(args));
  }

  void arguments(Iterable<String> args);

  void stdIn(byte[] input);

  default void stdIn(String utf8StringInput) {
    stdIn(StandardCharsets.UTF_8, utf8StringInput);
  }

  default void stdIn(Charset charset, String input) {
    stdIn(input.getBytes(charset));
  }

  void environment(Map<String, String> env);
}
