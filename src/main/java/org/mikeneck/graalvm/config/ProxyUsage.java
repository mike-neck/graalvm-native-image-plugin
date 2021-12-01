package org.mikeneck.graalvm.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;

@JsonDeserialize(using = ProxyUsage.ProxyUsageDeserializer.class)
public class ProxyUsage extends TreeSet<String> implements Comparable<ProxyUsage> {

  public ProxyUsage() {
    super();
  }

  ProxyUsage(@NotNull String canonicalClassName) {
    super(Collections.singleton(canonicalClassName));
  }

  ProxyUsage(@NotNull Class<?> klass) {
    this(klass.getCanonicalName());
  }

  ProxyUsage(@NotNull String... canonicalClassNames) {
    super(Arrays.asList(canonicalClassNames));
  }

  ProxyUsage(@NotNull Class<?>... classes) {
    this(Arrays.stream(classes).map(Class::getCanonicalName).toArray(String[]::new));
  }

  @Override
  public int compareTo(@NotNull ProxyUsage o) {
    Iterator<String> thisIterator = this.iterator();
    Iterator<String> thatIterator = o.iterator();
    while (thisIterator.hasNext()) {
      if (!thatIterator.hasNext()) {
        return 1;
      }
      String thisClassName = thisIterator.next();
      String thatClassName = thatIterator.next();
      int classNameComparison = thisClassName.compareTo(thatClassName);
      if (classNameComparison != 0) {
        return classNameComparison;
      }
    }
    return thatIterator.hasNext() ? -1 : 0;
  }

  static class ProxyUsageDeserializer extends JsonDeserializer {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      String[] interfaces =
          mapper
              .readerForListOf(String.class)
              .readValue(findArray(p.getCodec().readTree(p)), String[].class);
      return new ProxyUsage(interfaces);
    }

    private ArrayNode findArray(TreeNode tree) {
      if (tree.isArray()) {
        return (ArrayNode) tree;
      }
      if (tree.isObject()) {
        // new format from GraalVM 21.3.0
        TreeNode interfaces = tree.get("interfaces");
        if (interfaces != null && interfaces.isArray()) {
          return (ArrayNode) interfaces;
        }
      }
      throw new IllegalArgumentException();
    }
  }
}
