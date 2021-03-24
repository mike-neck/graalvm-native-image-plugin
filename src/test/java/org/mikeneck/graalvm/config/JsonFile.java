package org.mikeneck.graalvm.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JsonFile {
  /**
   * the name of json file.
   *
   * @return the name of json file.
   */
  String value();

  /**
   * the name of directory which contains file.
   *
   * @return the name of directory.
   */
  String directory() default "config";
}
