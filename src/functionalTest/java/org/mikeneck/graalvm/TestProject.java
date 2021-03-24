package org.mikeneck.graalvm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestProject {

  /** @return The path from test resource route. */
  String value();

  /** @return The directory name where resource files are copied. */
  String directoryName() default "";

  /** @return The subprojects of the test project. */
  String[] subprojects() default {};
}
