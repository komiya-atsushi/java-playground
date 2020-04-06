package me.k11i.popcnt;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PopcntTestBase {
    private final int testDataBits;
    private final int blockBits;
    private final int blockBytes;

    PopcntTestBase(int blockBits) {
        this.testDataBits = blockBits * 2;
        this.blockBits = blockBits;
        this.blockBytes = blockBits / 64;
    }

    void testSingle1(Popcnt popcnt) {
        long[] values = new long[testDataBits / 64];

        for (int i = 0; i < testDataBits; i++) {
            Arrays.fill(values, 0);
            values[i / 64] = 1L << i;

            for (int offset = 0; offset < testDataBits / 64; offset += blockBits / 64) {
                for (int bitIndex = 0; bitIndex < blockBits; bitIndex++) {
                    int expected = 0;
                    if ((i / blockBits) * blockBytes == offset && bitIndex > i % blockBits) {
                        expected = 1;
                    }

                    assertEquals(
                            expected,
                            popcnt.count(values, offset, bitIndex),
                            String.format("i = %d, offset = %d, bitIndex = %d", i, offset, bitIndex));
                }
            }
        }
    }

    void testFill1(Popcnt popcnt) {
        long[] values = new long[testDataBits / 64];
        Arrays.fill(values, ~0);

        for (int i = 0; i < testDataBits; i++) {
            int offset = (i / blockBits) * blockBytes;
            int expected = i % blockBits;

            assertEquals(
                    expected,
                    popcnt.count(values, offset, i % blockBits),
                    String.format("i = %d", i));
        }
    }
}
