package org.mikeneck.graalvm.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class JniConfig extends TreeSet<ClassUsage> implements MergeableConfig<JniConfig> {

  public JniConfig() {
    super();
  }

  JniConfig(Collection<ClassUsage> usages) {
    super(usages);
  }

  JniConfig(ClassUsage... usages) {
    this(Arrays.asList(usages));
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public JniConfig mergeWith(JniConfig other) {
    Map<String, ClassUsage> map =
        stream()
            .collect(
                Collectors.toMap(
                    (ClassUsage classUsage) -> classUsage.name, classUsage -> classUsage));
    for (ClassUsage usage : other) {
      if (map.containsKey(usage.name)) {
        map.put(usage.name, map.get(usage.name).mergeWith(usage));
      } else {
        map.put(usage.name, usage);
      }
    }
    return new JniConfig(map.values());
  }
}
