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
package org.mikeneck.graalvm

class GenerateNativeImageConfigTaskWrapper(private val task: GenerateNativeImageConfigTask) {

  @Suppress("UsePropertyAccessSyntax")
  var enabled: Boolean
    get() = task.enabled
    set(value) = task.setEnabled(value)

  var graalVmHome: String
    get() = throw UnsupportedOperationException("getGraalVmHome is not supported.")
    set(value) = task.setGraalVmHome(value)

  @Suppress("UsePropertyAccessSyntax")
  var exitOnApplicationError: Boolean
    get() = throw UnsupportedOperationException("getExitOnApplicationError is not supported.")
    set(value) = task.setExitOnApplicationError(value)

  fun resumeOnApplicationError() = task.resumeOnApplicationError()

  fun byRunningApplicationWithoutArguments() = task.byRunningApplicationWithoutArguments()

  fun byRunningApplication(javaExecutionConfig : JavaExecution.() -> Unit) =
      task.byRunningApplication { javaExecution -> javaExecutionConfig.invoke(javaExecution) }
}
