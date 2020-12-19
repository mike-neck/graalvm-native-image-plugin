package org.mikeneck.graalvm.nativeimage.options;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.GraalVmVersion;

public interface TraceClassInitialization {

    /**
     * Returns {@code TraceClassInitialization} option.
     * This method should be aware of a using GraalVM's version.
     * <p>
     * With GraalVM version previous than 20.3, calling this method with some parameters results a boolean option String.
     * Calling this method without any parameters results a boolean option String too.
     * 
     * <p>
     * With GraalVM version later than or equals to 20.3, calling this method with some parameters results a comma separated String of given FQCNs. 
     * Calling this method without any parameters results an empty String, thus the option will be ignored.
     * 
     * @param classNames - FQCNs of classes initialization is traced for.
     * @return - {@code TraceClassInitialization} option.
     */
    @NotNull
    String option(@NotNull String... classNames);

    static TraceClassInitialization of(GraalVmVersion version) {
        if (version.lessThan(GraalVmVersion.GRAAL_20_3_0_JAVA_8) || version.lessThan(GraalVmVersion.GRAAL_20_3_0_JAVA_11)) {
            return classNames -> "-H:+TraceClassInitialization";
        } else {
            return classNames -> {
                if (classNames.length == 0) {
                    return "";
                }
                return Arrays.stream(classNames).collect(Collectors.joining(",", "-H:TraceClassInitialization=", ""));
            };
        }
    }
}
