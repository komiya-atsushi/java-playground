package me.k11i.popcnt;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class Popcnt256Test extends PopcntTestBase {
    Popcnt256Test() {
        super(256);
    }

    @ParameterizedTest
    @EnumSource(Popcnt256.Implementation.class)
    void testSingle1(Popcnt256.Implementation impl) {
        super.testSingle1(impl);
    }

    @ParameterizedTest
    @EnumSource(Popcnt256.Implementation.class)
    void testFill1(Popcnt256.Implementation impl) {
        super.testFill1(impl);
    }
}
