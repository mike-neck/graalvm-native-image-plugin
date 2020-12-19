package org.mikeneck.graalvm.config;


import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;

public class ProxyUsage extends TreeSet<String> implements Comparable<ProxyUsage> {

    public ProxyUsage() {
        super();
    }

    ProxyUsage(@NotNull String canonicalClassName) {
        super(Collections.singleton(canonicalClassName));
    }

    ProxyUsage(@NotNull Class<?> klass) {
        this(klass.getCanonicalName());
    }

    ProxyUsage(@NotNull String... canonicalClassNames) {
        super(Arrays.asList(canonicalClassNames));
    }

    ProxyUsage(@NotNull Class<?>... classes) {
        this(Arrays.stream(classes)
                .map(Class::getCanonicalName)
                .toArray(String[]::new));
    }

    @Override
    public int compareTo(@NotNull ProxyUsage o) {
        Iterator<String> thisIterator = this.iterator();
        Iterator<String> thatIterator = o.iterator();
        while (thisIterator.hasNext()) {
            if (!thatIterator.hasNext()) {
                return 1;
            }
            String thisClassName = thisIterator.next();
            String thatClassName = thatIterator.next();
            int classNameComparison = thisClassName.compareTo(thatClassName);
            if (classNameComparison != 0) {
                return classNameComparison;
            }
        }
        return thatIterator.hasNext()? -1: 0;
    }
}
