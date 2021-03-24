package org.mikeneck.graalvm.config;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class TestJsonReader implements AfterEachCallback, ParameterResolver {

  private final ClassLoader classLoader = getClass().getClassLoader();

  InputStream configJsonResource(String name) {
    InputStream inputStream = classLoader.getResourceAsStream(name);
    if (inputStream == null) {
      throw new IllegalStateException(String.format("%s not found", name));
    }
    return inputStream;
  }

  private static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create(TestJsonReader.class);

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Method method = extensionContext.getRequiredTestMethod();
    if (!method.isAnnotationPresent(JsonFile.class)) {
      return false;
    }
    Parameter parameter = parameterContext.getParameter();
    return parameter.getType().equals(InputStream.class);
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Method method = extensionContext.getRequiredTestMethod();
    JsonFile jsonFile = method.getAnnotation(JsonFile.class);
    String resource = String.format("%s/%s", jsonFile.directory(), jsonFile.value());
    InputStream inputStream = configJsonResource(resource);

    ExtensionContext.Store store = extensionContext.getStore(NAMESPACE);
    store.put(resource, inputStream);

    return inputStream;
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    Method method = context.getRequiredTestMethod();
    JsonFile jsonFile = method.getAnnotation(JsonFile.class);
    String resource = String.format("%s/%s", jsonFile.directory(), jsonFile.value());

    ExtensionContext.Store store = context.getStore(NAMESPACE);
    InputStream stream = store.get(resource, InputStream.class);
    if (stream != null) {
      stream.close();
    }
  }
}
