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

import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class ResourceConfig {

    @NotNull
    public List<ResourceUsage> resources = Collections.emptyList();

    @NotNull
    public List<BundleUsage> bundles = Collections.emptyList();

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResourceUsage{");
        sb.append("resources=").append(resources);
        sb.append(", bundles=").append(bundles);
        sb.append('}');
        return sb.toString();
    }
}
