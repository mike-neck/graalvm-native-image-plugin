package org.mikeneck.graalvm;

import org.gradle.api.provider.Provider;

/**
 * Represents native-image command options. You can configure command options via this interface.
 */
public interface NativeImageArgumentsConfig {
  /**
   * Passes an option to native-image one by one.
   *
   * @param argument - native-image option argument.
   */
  void add(String argument);

  /**
   * Passes an option to native-image one by one.
   *
   * @param argument - native-image option argument.
   */
  void add(Provider<String> argument);
}
