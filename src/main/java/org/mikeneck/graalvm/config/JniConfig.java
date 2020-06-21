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
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class JniConfig extends TreeSet<ClassUsage> implements MergeableConfig<JniConfig> {

    public JniConfig() {
        super();
    }

    JniConfig(Collection<ClassUsage> usages) {
        super(usages);
    }

    JniConfig(ClassUsage... usages) {
        this(Arrays.asList(usages));
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public JniConfig mergeWith(JniConfig other) {
        Map<String, ClassUsage> map = stream()
                .collect(Collectors.toMap((ClassUsage classUsage) -> classUsage.name, classUsage -> classUsage));
        for (ClassUsage usage : other) {
            if (map.containsKey(usage.name)) {
                map.put(usage.name, map.get(usage.name).mergeWith(usage));
            } else {
                map.put(usage.name, usage);
            }
        }
        return new JniConfig(map.values());
    }
}
