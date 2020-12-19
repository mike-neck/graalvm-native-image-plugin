package org.mikeneck.graalvm.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class ReflectConfig extends TreeSet<ClassUsage> implements MergeableConfig<ReflectConfig> {

  public ReflectConfig() {}

  ReflectConfig(@NotNull Collection<? extends ClassUsage> c) {
    super(c);
  }

  ReflectConfig(@NotNull ClassUsage... usages) {
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
  public ReflectConfig mergeWith(ReflectConfig other) {
    Map<String, ClassUsage> map =
        stream().collect(Collectors.toMap((ClassUsage usage) -> usage.name, usage -> usage));
    for (ClassUsage usage : other) {
      map.computeIfPresent(usage.name, (k, current) -> current.mergeWith(usage));
      map.putIfAbsent(usage.name, usage);
    }
    return new ReflectConfig(map.values());
  }
}
