package org.mikeneck.graalvm;

import java.io.File;
import java.nio.file.Path;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.jetbrains.annotations.NotNull;

/**
 * Represents native-image command options. You can configure command options via this interface.
 */
public interface NativeImageArgumentsConfig {
  /**
   * Pass parameters by file to native-image, not by command line arguments. The file will be
   * created by {@link NativeImageTask}. The path of the file is {@code
   * buildDir/tmp/native-image-args/arguments.txt}.
   */
  void preferByFile();
  /**
   * Pass parameters by file to native-image, not by command line arguments. The given file will be
   * created by {@link NativeImageTask}.
   *
   * @param file - the file to pass command line parameters.
   */
  void preferByFile(@NotNull File file);
  /**
   * Pass parameters by file to native-image, not by command line arguments. The given file will be
   * created by {@link NativeImageTask}.
   *
   * @param file - the file to pass command line parameters.
   */
  void preferByFile(@NotNull Path file);
  /**
   * Pass parameters by file to native-image, not by command line arguments. The given file will be
   * created by {@link NativeImageTask}.
   *
   * @param file - the file to pass command line parameters.
   */
  void preferByFile(@NotNull RegularFile file);
  /**
   * Pass parameters by file to native-image, not by command line arguments. The given file will be
   * created by {@link NativeImageTask}.
   *
   * @param file - the file to pass command line parameters.
   */
  void preferByFile(@NotNull Provider<? extends RegularFile> file);

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
