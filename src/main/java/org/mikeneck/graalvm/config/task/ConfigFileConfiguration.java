package org.mikeneck.graalvm.config.task;

import java.io.File;

public interface ConfigFileConfiguration {

  void addJniConfig(File jniConfig);

  void addProxyConfig(File proxyConfig);

  void addReflectConfig(File reflectConfig);

  void addResourceConfig(File resourceConfig);
}
