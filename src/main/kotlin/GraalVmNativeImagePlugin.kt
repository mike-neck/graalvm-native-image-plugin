import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.bundling.Jar
import org.mikeneck.graalvm.GenerateNativeImageConfigTask
import org.mikeneck.graalvm.GenerateNativeImageConfigTaskWrapper
import org.mikeneck.graalvm.NativeImageTask
import java.io.File

fun Project.nativeImage(configuration: NativeImageTask.() -> Unit): Unit =
    when(val nativeImageTask = this.tasks.withType(NativeImageTask::class.java).findByName("nativeImage")) {
      null -> Unit
      else -> configuration(nativeImageTask)
    }

var NativeImageTask.graalVmHome: String
  get() = throw UnsupportedOperationException("getGraalVmHome is not supported.")
  set(value) = this.setGraalVmHome(value)

@Deprecated(message = "use `NativeImageTask.classpath = jarTask` instead.", level = DeprecationLevel.WARNING)
var NativeImageTask.jarTask: Jar
  get() = throw UnsupportedOperationException("getJarTask is not supported.")
  set(value) = this.setClasspath(value)

var NativeImageTask.classpath: FileCollection
  get() = throw UnsupportedOperationException("getClasspath is not supported.")
  set(value) = this.setClasspath(value)

var NativeImageTask.mainClass: String
  get() = throw UnsupportedOperationException("getMainClass is not supported.")
  set(value) = this.setMainClass(value)

var NativeImageTask.executableName: String
  get() = throw UnsupportedOperationException("getExecutableName is not supported.")
  set(value) = this.setExecutableName(value)

var NativeImageTask.runtimeClasspath: Configuration
  get() = throw UnsupportedOperationException("getRuntimeClasspath is not supported.")
  set(value) = this.setRuntimeClasspath(value)

var NativeImageTask.outputDirectory: File
  get() = throw UnsupportedOperationException("getOutputDirectory is not supported.")
  set(value) = this.setOutputDirectory(value)

fun Project.generateNativeImageConfig(generateNativeImageConfigConfiguration: GenerateNativeImageConfigTaskWrapper.() -> Unit): Unit =
    this.tasks.named("generateNativeImageConfig", GenerateNativeImageConfigTask::class.java).configure { task ->
      GenerateNativeImageConfigTaskWrapper(task).apply(generateNativeImageConfigConfiguration)
    }
