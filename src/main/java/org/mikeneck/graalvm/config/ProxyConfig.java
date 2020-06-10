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
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ProxyConfig extends TreeSet<ProxyUsage> implements MergeableConfig<ProxyConfig> {

    public ProxyConfig() {
        super();
    }

    ProxyConfig(String... items) {
        this(Arrays.stream(items)
                .map(ProxyUsage::new)
                .collect(Collectors.toSet()));
    }

    private ProxyConfig(Collection<ProxyUsage> proxy) {
        super(proxy);
    }

    public ProxyConfig(SortedSet<ProxyUsage> s) {
        super(s);
    }

    @Override
    public ProxyConfig mergeWith(ProxyConfig other) {
        ProxyConfig newProxyConfig = new ProxyConfig(this);
        newProxyConfig.addAll(other);
        return newProxyConfig;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
