package org.mikeneck.graalvm;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class TestProjectSetup implements ParameterResolver, BeforeEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    Method method = context.getRequiredTestMethod();
    if (method == null) {
      throw new IllegalStateException("Test method not found");
    }
    TestProject testProject = method.getDeclaredAnnotation(TestProject.class);
    if (testProject == null) {
      throw new IllegalArgumentException("@TestProject annotation is required.");
    }
    String resourceName = testProject.value();
    String[] subprojects = testProject.subprojects();
    List<SubProject> subProjects =
        Arrays.stream(subprojects).<SubProject>map(path -> () -> path).collect(Collectors.toList());
    FunctionalTestContext ctx =
        subProjects.isEmpty()
            ? new FunctionalTestContext(resourceName)
            : new FunctionalTestContext(resourceName, subProjects);
    ctx.setup();

    ExtensionContext.Store store =
        context.getStore(ExtensionContext.Namespace.create(TestProject.class));
    store.put(testProject, ctx);
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Method method = extensionContext.getRequiredTestMethod();
    if (method == null) {
      return false;
    }
    TestProject testProject = method.getDeclaredAnnotation(TestProject.class);
    if (testProject == null) {
      return false;
    }
    Class<?> parameterType = parameterContext.getParameter().getType();
    if (!TestParameterResolverHandler.hasSupportingMember(parameterType)) {
      return false;
    }
    ExtensionContext.Store store =
        extensionContext.getStore(ExtensionContext.Namespace.create(TestProject.class));
    FunctionalTestContext ctx = store.get(testProject, FunctionalTestContext.class);
    return ctx != null;
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Method method = extensionContext.getRequiredTestMethod();
    if (method == null) {
      throw new IllegalStateException("Test method not found");
    }
    TestProject testProject = method.getDeclaredAnnotation(TestProject.class);
    if (testProject == null) {
      throw new IllegalStateException("@TestProject annotation is required.");
    }
    Class<?> type = parameterContext.getParameter().getType();
    TestParameterResolverHandler handler = TestParameterResolverHandler.from(type);
    if (handler == null) {
      throw new IllegalArgumentException(type.getSimpleName() + " is not supported.");
    }
    ExtensionContext.Store store =
        extensionContext.getStore(ExtensionContext.Namespace.create(TestProject.class));
    FunctionalTestContext ctx = store.get(testProject, FunctionalTestContext.class);
    if (ctx == null) {
      throw new IllegalStateException("@TestProject not configured properly.");
    }
    return handler.toParameter(ctx);
  }
}
