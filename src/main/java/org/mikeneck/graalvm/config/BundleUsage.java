package org.mikeneck.graalvm.config;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class BundleUsage implements Comparable<BundleUsage> {

    @NotNull
    public String name = "";

    public BundleUsage() {
    }

    BundleUsage(@NotNull String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BundleUsage)) return false;
        BundleUsage that = (BundleUsage) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResourceBundleUsage{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(@NotNull BundleUsage o) {
        return this.name.compareTo(o.name);
    }
}
