package org.mikeneck.graalvm.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MethodUsageTest {

    static List<MethodUsage> sorted(MethodUsage... methods) {
        List<MethodUsage> list = new ArrayList<>(Arrays.asList(methods));
        list.sort(Comparator.comparing((MethodUsage m) -> m));
        return list;
    }

    @Test
    void compareToSmallName() {
        MethodUsage foo = MethodUsage.of("foo");
        MethodUsage bar = MethodUsage.of("bar");

        List<MethodUsage> sorted = sorted(foo, bar);

        assertThat(sorted).isEqualTo(Arrays.asList(bar, foo));
    }

    @Test
    void compareToLargeName() {
        MethodUsage bar = MethodUsage.of("bar");
        MethodUsage baz = MethodUsage.of("baz");

        List<MethodUsage> sorted = sorted(bar, baz);

        assertThat(sorted).isEqualTo(Arrays.asList(bar, baz));
    }

    @Test
    void compareToSameNameWithSingleLargeParameterType() {
        MethodUsage fooInteger = MethodUsage.of("foo", Integer.class);
        MethodUsage fooLong = MethodUsage.of("foo", Long.class);

        List<MethodUsage> sorted = sorted(fooInteger, fooLong);

        assertThat(sorted).isEqualTo(Arrays.asList(fooInteger, fooLong));
    }

    @Test
    void compareToSameNameWithSingleSmallParameterType() {
        MethodUsage fooLong = MethodUsage.of("foo", Long.class);
        MethodUsage fooInteger = MethodUsage.of("foo", Integer.class);

        List<MethodUsage> sorted = sorted(fooLong, fooInteger);

        assertThat(sorted).isEqualTo(Arrays.asList(fooInteger, fooLong));
    }

    @Test
    void compareToSameNameWithManyParameterType() {
        MethodUsage fooWith3Params = MethodUsage.of("foo", String.class, String.class, int.class);
        MethodUsage fooWith4Params = MethodUsage.of("foo", String.class, String.class, int.class, Function.class);

        List<MethodUsage> sorted = sorted(fooWith3Params, fooWith4Params);

        assertThat(sorted).isEqualTo(Arrays.asList(fooWith3Params, fooWith4Params));
    }

    @Test
    void compareToSameNameWithLessParameterType() {
        MethodUsage fooWith4Params = MethodUsage.of("foo", String.class, String.class, int.class, Function.class);
        MethodUsage fooWith3Params = MethodUsage.of("foo", String.class, String.class, int.class);

        List<MethodUsage> sorted = sorted(fooWith4Params, fooWith3Params);

        assertThat(sorted).isEqualTo(Arrays.asList(fooWith3Params, fooWith4Params));
    }

    @Test
    void compareToSelf() {
        MethodUsage methodUsage = MethodUsage.of("foo", String.class, String.class, int.class, Function.class);
        MethodUsage self = MethodUsage.of("foo", String.class, String.class, int.class, Function.class);

        assertThat(methodUsage.compareTo(self)).isEqualTo(0);
    }
}
