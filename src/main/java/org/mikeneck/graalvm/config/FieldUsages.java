package org.mikeneck.graalvm.config;

import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;

public class FieldUsages implements MergeableConfig<FieldUsages>, Iterable<FieldUsage> {

  @NotNull private final SortedSet<FieldUsage> fieldUsages;

  public FieldUsages(@NotNull SortedSet<FieldUsage> fieldUsages) {
    this.fieldUsages = fieldUsages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FieldUsages)) return false;
    FieldUsages that = (FieldUsages) o;
    return fieldUsages.equals(that.fieldUsages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldUsages);
  }

  @SuppressWarnings("StringBufferReplaceableByString")
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("FieldUsages{");
    sb.append("fieldUsages=").append(fieldUsages);
    sb.append('}');
    return sb.toString();
  }

  @NotNull
  @Override
  public Iterator<FieldUsage> iterator() {
    return fieldUsages.iterator();
  }

  @Override
  public FieldUsages mergeWith(FieldUsages other) {
    SortedSet<FieldUsage> newFieldUsages = new TreeSet<>();
    if (this.fieldUsages.isEmpty()) {
      newFieldUsages.addAll(other.fieldUsages);
      return new FieldUsages(newFieldUsages);
    } else if (other.fieldUsages.isEmpty()) {
      newFieldUsages.addAll(this.fieldUsages);
      return new FieldUsages(newFieldUsages);
    }
    Iterator<FieldUsage> self = iterator();
    Iterator<FieldUsage> that = other.iterator();
    FieldUsage s = self.next();
    FieldUsage o = that.next();

    while (true) {
      int c = s.name.compareTo(o.name);
      if (c < 0) {
        newFieldUsages.add(s);
        if (!self.hasNext()) {
          newFieldUsages.add(o);
          break;
        }
        s = self.next();
        continue;
      } else if (c > 0) {
        newFieldUsages.add(o);
        if (!that.hasNext()) {
          newFieldUsages.add(s);
          break;
        }
        o = that.next();
        continue;
      }
      s = s.mergeWith(o);
      if (!that.hasNext()) {
        newFieldUsages.add(s);
        break;
      }
      o = that.next();
    }
    while (self.hasNext()) {
      newFieldUsages.add(self.next());
    }
    while (that.hasNext()) {
      newFieldUsages.add(that.next());
    }
    return new FieldUsages(newFieldUsages);
  }
}
