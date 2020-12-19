package org.mikeneck.graalvm.config.task;

import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import org.mikeneck.graalvm.config.MergeableConfig;

public interface MergeConfigFileWork<C extends MergeableConfig<C>> {
  void run() throws IOException;

  static SelectorMergeConfigFileWorkFactory ofSelector() {
    ServiceLoader<SelectorMergeConfigFileWorkFactory> loader =
        ServiceLoader.load(SelectorMergeConfigFileWorkFactory.class);
    Iterator<SelectorMergeConfigFileWorkFactory> iterator = loader.iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    } else {
      throw new IllegalStateException(
          "Loader of " + SelectorMergeConfigFileWorkFactory.class + " not found");
    }
  }
}
