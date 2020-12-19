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
