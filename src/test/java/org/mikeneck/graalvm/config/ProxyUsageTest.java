package org.mikeneck.graalvm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import org.junit.jupiter.api.Test;
import org.mikeneck.graalvm.config.comparable.Assert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class ProxyUsageTest {

    @Test
    void compareEmptyToEmpty() {
        ProxyUsage left = new ProxyUsage();
        ProxyUsage right = new ProxyUsage();

        assertAll(
                () -> assertThat(new ProxyUsage[]{left}).isEqualTo(new ProxyUsage[]{ right }),
                () -> Assert.comparable(left).isEqualByComparingTo(right)
        );
    }

    @Test
    void comparingEmptyToHavingElements() {
        ProxyUsage left = new ProxyUsage(Serializable.class);
        ProxyUsage right = new ProxyUsage();

        Assert.comparable(left).isGreaterThan(right);
    }

    @Test
    void comparingSingleToSingle() {
        ProxyUsage left = new ProxyUsage(Iterable.class);
        ProxyUsage right = new ProxyUsage(AutoCloseable.class);

        Assert.comparable(left).isGreaterThan(right);
    }

    @Test
    void comparingMultipleToMultiple() {
        ProxyUsage left = new ProxyUsage(Iterator.class, AutoCloseable.class);
        ProxyUsage right = new ProxyUsage(AutoCloseable.class, List.class);

        Assert.comparable(left).isLessThan(right);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void unmarshalEmpty() throws IOException {
        SortedSet<String> proxyUsage = objectMapper.readValue("[]", ProxyUsage.class);
        assertThat(proxyUsage).isEmpty();
    }

    @Test
    void unmarshalNonEmpty() throws IOException {
        Object proxyUsage = objectMapper.readValue(
                "[\"com.example.Foo\",\"com.example.Bar\"]", 
                ProxyUsage.class);

        assertThat(proxyUsage)
                .isEqualTo(new ProxyUsage("com.example.Bar", "com.example.Foo"));
    }

    @Test
    void marshalEmpty() throws IOException {
        String json = objectMapper.writeValueAsString(new ProxyUsage());
        assertThat(json).isEqualTo("[]");
    }

    @Test
    void marshalNonEmpty() throws IOException {
        String json = objectMapper.writeValueAsString(new ProxyUsage(List.class, AutoCloseable.class));
        assertThat(json)
                .isEqualTo("[" +
                        "\"java.lang.AutoCloseable\"" +
                        "," +
                        "\"java.util.List\"" +
                        "]"
                );
    }
}
