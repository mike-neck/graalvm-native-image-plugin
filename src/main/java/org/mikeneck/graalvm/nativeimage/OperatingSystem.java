package org.mikeneck.graalvm.nativeimage;

public enum OperatingSystem {
  WINDOWS,
  LINUX,
  MACOSX,
  ;

  public static OperatingSystem byName(String osName) {
    if (osName.contains("win")) {
      return WINDOWS;
    } else if (osName.contains("Mac")) {
      return MACOSX;
    } else {
      return LINUX;
    }
  }
}
