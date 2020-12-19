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
package org.mikeneck.graalvm.config.task;

import org.jetbrains.annotations.NotNull;

public class DefaultOutcome<@NotNull I, @NotNull P> implements Outcome<I, P> {

    @NotNull
    private final P product;
    @NotNull
    private final Mapper<I, P> mapper;

    DefaultOutcome(@NotNull P product, @NotNull Mapper<I, P> mapper) {
        this.product = product;
        this.mapper = mapper;
    }

    @Override
    public @NotNull Mapper<I, P> detectedMapper() {
        return mapper;
    }

    @Override
    public @NotNull P detectedProduct() {
        return product;
    }
}
