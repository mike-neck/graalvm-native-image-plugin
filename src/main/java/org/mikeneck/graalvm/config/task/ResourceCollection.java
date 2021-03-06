package org.mikeneck.graalvm.config.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public interface ResourceCollection<T> extends Iterable<T> {

  @NotNull
  String resourceName();

  @NotNull
  static <T> ResourceCollection<T> create(
      @NotNull String resourceName, @NotNull Iterable<T> resources) {
    return new ResourceCollection<T>() {
      @Override
      public @NotNull String resourceName() {
        return resourceName;
      }

      @NotNull
      @Override
      public Iterator<T> iterator() {
        return resources.iterator();
      }
    };
  }

  default @NotNull <P> Iterable<P> applyAll(@NotNull MappingCandidates<T, P> candidates)
      throws IOException {
    @NotNull String resourceName = resourceName();
    Iterator<T> iterator = this.iterator();
    if (!iterator.hasNext()) {
      return Collections.emptyList();
    }
    T first = iterator.next();
    Optional<@NotNull Outcome<T, P>> optional = candidates.findOut(first);
    if (!optional.isPresent()) {
      throw new IOException(
          String.format(
              "%s cannot be restored by candidate processors[%s]", resourceName, candidates));
    }
    Outcome<T, P> outcome = optional.get();
    List<P> list = new ArrayList<>(Collections.singleton(outcome.detectedProduct()));
    Mapper<T, P> mapper = outcome.detectedMapper();
    while (iterator.hasNext()) {
      T input = iterator.next();
      try {
        P product = mapper.work(input);
        list.add(product);
      } catch (IOException e) {
        throw new IOException(
            String.format(
                "%s has invalid resource[%s] that cannot be restored by matching mapping[%s]",
                resourceName, input, mapper),
            e);
      }
    }
    return list;
  }
}
