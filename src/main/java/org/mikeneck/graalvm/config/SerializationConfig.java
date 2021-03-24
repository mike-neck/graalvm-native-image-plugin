package org.mikeneck.graalvm.config;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

public class SerializationConfig extends TreeSet<ConstructorUsage>
    implements MergeableConfig<SerializationConfig> {

  public SerializationConfig() {
    super();
  }

  public SerializationConfig(ConstructorUsage... constructorUsages) {
    super(Arrays.asList(constructorUsages));
  }

  private SerializationConfig(SortedSet<ConstructorUsage> constructorUsages) {
    super(constructorUsages);
  }

  @Override
  public SerializationConfig mergeWith(SerializationConfig other) {
    SortedSet<ConstructorUsage> set = new TreeSet<>();
    set.addAll(this);
    set.addAll(other);
    return new SerializationConfig(set);
  }
}
