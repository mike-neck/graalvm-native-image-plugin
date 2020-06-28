import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.bundling.Jar
import org.mikeneck.graalvm.NativeImageTask
import java.io.File

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

fun Project.nativeImage(configuration: NativeImageTask.() -> Unit): Unit =
    when(val nativeImageTask = this.tasks.withType(NativeImageTask::class.java).findByName("nativeImage")) {
      null -> Unit
      else -> configuration(nativeImageTask)
    }

var NativeImageTask.graalVmHome: String
  get() = throw UnsupportedOperationException("getGraalVmHome is not supported.")
  set(value) = this.setGraalVmHome(value)

var NativeImageTask.jarTask: Jar
  get() = throw UnsupportedOperationException("getJarTask is not supported.")
  set(value) = this.setJarTask(value)

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
