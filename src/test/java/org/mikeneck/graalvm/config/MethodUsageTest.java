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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MethodUsageTest {

    static List<MethodUsage> sorted(MethodUsage... methods) {
        List<MethodUsage> list = new ArrayList<>(Arrays.asList(methods));
        list.sort(Comparator.comparing((MethodUsage m) -> m));
        return list;
    }

    @Test
    public void compareToSmallName() {
        MethodUsage foo = MethodUsage.of("foo");
        MethodUsage bar = MethodUsage.of("bar");

        List<MethodUsage> sorted = sorted(foo, bar);

        assertThat(sorted, is(Arrays.asList(bar, foo)));
    }

    @Test
    public void compareToLargeName() {
        MethodUsage bar = MethodUsage.of("bar");
        MethodUsage baz = MethodUsage.of("baz");

        List<MethodUsage> sorted = sorted(bar, baz);

        assertThat(sorted, is(Arrays.asList(bar, baz)));
    }

    @Test
    public void compareToSameNameWithSingleLargeParameterType() {
        MethodUsage fooInteger = MethodUsage.of("foo", Integer.class);
        MethodUsage fooLong = MethodUsage.of("foo", Long.class);

        List<MethodUsage> sorted = sorted(fooInteger, fooLong);

        assertThat(sorted, is(Arrays.asList(fooInteger, fooLong)));
    }

    @Test
    public void compareToSameNameWithSingleSmallParameterType() {
        MethodUsage fooLong = MethodUsage.of("foo", Long.class);
        MethodUsage fooInteger = MethodUsage.of("foo", Integer.class);

        List<MethodUsage> sorted = sorted(fooLong, fooInteger);

        assertThat(sorted, is(Arrays.asList(fooInteger, fooLong)));
    }

    @Test
    public void compareToSameNameWithManyParameterType() {
        MethodUsage fooWith3Params = MethodUsage.of("foo", String.class, String.class, int.class);
        MethodUsage fooWith4Params = MethodUsage.of("foo", String.class, String.class, int.class, Function.class);

        List<MethodUsage> sorted = sorted(fooWith3Params, fooWith4Params);

        assertThat(sorted, is(Arrays.asList(fooWith3Params, fooWith4Params)));
    }

    @Test
    public void compareToSameNameWithLessParameterType() {
        MethodUsage fooWith4Params = MethodUsage.of("foo", String.class, String.class, int.class, Function.class);
        MethodUsage fooWith3Params = MethodUsage.of("foo", String.class, String.class, int.class);

        List<MethodUsage> sorted = sorted(fooWith4Params, fooWith3Params);

        assertThat(sorted, is(Arrays.asList(fooWith3Params, fooWith4Params)));
    }

    @Test
    public void compareToSelf() {
        MethodUsage methodUsage = MethodUsage.of("foo", String.class, String.class, int.class, Function.class);
        MethodUsage self = MethodUsage.of("foo", String.class, String.class, int.class, Function.class);

        assertThat(methodUsage.compareTo(self), is(0));
    }
}
