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

import java.io.Serializable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class ProxyUsageTest {

    @Test
    void compareEmptyToEmpty() {
        ProxyUsage left = new ProxyUsage();
        ProxyUsage right = new ProxyUsage();

        assertThat(left.compareTo(right)).isEqualTo(0);
    }

    @Test
    void comparingEmptyToHavingElements() {
        ProxyUsage left = new ProxyUsage(Serializable.class);
        ProxyUsage right = new ProxyUsage();

        assertThat(left.compareTo(right)).isEqualTo(1);
    }
}
