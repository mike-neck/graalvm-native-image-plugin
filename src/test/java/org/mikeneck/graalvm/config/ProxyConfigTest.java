package org.mikeneck.graalvm.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class ProxyConfigTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TestJsonReader reader = new TestJsonReader();

  @Test
  void jsonWithContents() throws IOException {
    try (InputStream inputStream = reader.configJsonResource("config/proxy-config-1.json")) {
      ProxyConfig proxyConfig = objectMapper.readValue(inputStream, ProxyConfig.class);
      assertThat(proxyConfig)
          .contains(new ProxyUsage("com.example.App"), new ProxyUsage("com.example.Printer"));
    }
  }

  @Test
  void jsonWithoutContents() throws IOException {
    try (InputStream inputStream = reader.configJsonResource("config/proxy-config-2.json")) {
      ProxyConfig proxyConfig = objectMapper.readValue(inputStream, ProxyConfig.class);
      assertThat(proxyConfig).isEqualTo(Collections.emptySortedSet());
    }
  }

  @Test
  void case60() throws IOException {
    try (InputStream inputStream = reader.configJsonResource("config/proxy-config-3.json")) {
      ProxyConfig proxyConfig = objectMapper.readValue(inputStream, ProxyConfig.class);
      assertThat(proxyConfig)
          .contains(
              new ProxyUsage(
                  "org.apache.http.conn.ConnectionRequest", "com.amazonaws.http.conn.Wrapped"),
              new ProxyUsage(
                  "org.apache.http.conn.HttpClientConnectionManager",
                  "org.apache.http.pool.ConnPoolControl",
                  "com.amazonaws.http.conn.Wrapped"));
    }
  }

  @Test
  void mergeWithOthers() {
    ProxyConfig left = new ProxyConfig("com.example.Foo", "com.example.Bar");
    ProxyConfig right = new ProxyConfig("com.example.Baz", "com.example.Qux");

    ProxyConfig proxyUsages = left.mergeWith(right);

    assertThat(proxyUsages)
        .isEqualTo(
            sortedSetOf(
                new ProxyUsage("com.example.Bar"),
                new ProxyUsage("com.example.Baz"),
                new ProxyUsage("com.example.Foo"),
                new ProxyUsage("com.example.Qux")));
  }

  @SafeVarargs
  static <T extends Comparable<T>> SortedSet<T> sortedSetOf(T... items) {
    return new TreeSet<>(Arrays.asList(items));
  }

  @Test
  void mergeWithOthersSharding() {
    ProxyConfig left = new ProxyConfig("com.abb.Foo", "com.example.Bar");
    ProxyConfig right = new ProxyConfig("com.example.Baz", "com.abb.Foo");

    ProxyConfig proxyUsages = left.mergeWith(right);

    assertThat(proxyUsages)
        .isEqualTo(
            sortedSetOf(
                new ProxyUsage("com.abb.Foo"),
                new ProxyUsage("com.example.Bar"),
                new ProxyUsage("com.example.Baz")));
  }

  @Test
  void mergeWithSelfBecomesSelf() {
    ProxyConfig proxyConfig =
        new ProxyConfig("com.example.Foo", "com.example.Bar", "com.example.Baz");

    @SuppressWarnings("CollectionAddedToSelf")
    ProxyConfig merged = proxyConfig.mergeWith(proxyConfig);

    assertThat(merged).isEqualTo(proxyConfig);
  }

  @Test
  void mergeWithEmptyBecomesSelf() {
    ProxyConfig proxyConfig =
        new ProxyConfig("com.example.Foo", "com.example.Bar", "com.example.Baz");

    ProxyConfig merged = proxyConfig.mergeWith(new ProxyConfig());

    assertThat(merged).isEqualTo(proxyConfig);
  }

  @Test
  void emptyMergedWithEmptyBecomesEmpty() {
    ProxyConfig proxyConfig = new ProxyConfig().mergeWith(new ProxyConfig());

    assertThat(proxyConfig).isEqualTo(Collections.emptySortedSet());
  }
}
