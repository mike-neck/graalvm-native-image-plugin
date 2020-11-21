/*
 * Copyright 2020 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.graalvm;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.testkit.runner.GradleRunner;
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
        List<SubProject> subProjects = Arrays.stream(subprojects).<SubProject>map(path -> () -> path).collect(Collectors.toList());
        FunctionalTestContext ctx = new FunctionalTestContext(resourceName, subProjects);
        ctx.setup();

        ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.create(TestProject.class));
        store.put(testProject, ctx);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Method method = extensionContext.getRequiredTestMethod();
        if (method == null) {
            return false;
        }
        TestProject testProject = method.getDeclaredAnnotation(TestProject.class);
        if (testProject == null) {
            return false;
        }
        if (!parameterContext.getParameter().getType().equals(Gradlew.class)) {
            return false;
        }
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.create(TestProject.class));
        FunctionalTestContext ctx = store.get(testProject, FunctionalTestContext.class);
        return ctx != null;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Method method = extensionContext.getRequiredTestMethod();
        if (method == null) {
            throw new IllegalStateException("Test method not found");
        }
        TestProject testProject = method.getDeclaredAnnotation(TestProject.class);
        if (testProject == null) {
            throw new IllegalStateException("@TestProject annotation is required.");
        }
        Class<?> type = parameterContext.getParameter().getType();
        if (!type.equals(Gradlew.class)) {
            throw new IllegalArgumentException(type.getSimpleName() + " is not supported.");
        }
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.create(TestProject.class));
        FunctionalTestContext ctx = store.get(testProject, FunctionalTestContext.class);
        if (ctx == null) {
            throw new IllegalStateException("@TestProject not configured properly.");
        }
        return (Gradlew) params -> GradleRunner.create()
                .withProjectDir(ctx.rootDir.toFile())
                .withArguments(params)
                .withPluginClasspath()
                .forwardOutput()
                .build();
    }
}
