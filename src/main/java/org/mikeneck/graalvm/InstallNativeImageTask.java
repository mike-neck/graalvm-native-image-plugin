package org.mikeneck.graalvm;

import org.gradle.api.Task;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;

public interface InstallNativeImageTask extends Task {

    @Internal
    Provider<GraalVmHome> getGraalVmHome();
}
