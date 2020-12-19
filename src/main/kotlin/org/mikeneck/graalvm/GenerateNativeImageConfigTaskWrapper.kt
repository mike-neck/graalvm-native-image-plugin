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
