package org.mikeneck.graalvm;

import org.gradle.api.Task;

public interface ShareEnabledState extends Task {

    @Override
    void setEnabled(boolean b);

    void shareEnabledStateWith(Task... tasks);
}
