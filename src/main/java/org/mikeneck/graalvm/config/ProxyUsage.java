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
