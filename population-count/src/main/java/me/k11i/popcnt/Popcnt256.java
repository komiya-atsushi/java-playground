package me.k11i.popcnt;

@SuppressWarnings({"unused"})
public interface Popcnt256 {
    enum Implementation implements Popcnt {
        LOOP {
            @Override
            public int count(long[] values, int offset, int bitIndex) {
                return Popcnt256.loop(values, offset, bitIndex);
            }
        },

        BRANCHLESS {
            @Override
            public int count(long[] values, int offset, int bitIndex) {
                return Popcnt256.branchless(values, offset, bitIndex);
            }
        },
    }

    static int loop(long[] values, int offset, int bitIndex) {
        int longIndex = bitIndex >>> 6;
        long mask = (1L << bitIndex) - 1;

        int result = Long.bitCount(values[offset + longIndex] & mask);
        for (int i = 0; i < longIndex; i++) {
            result += Long.bitCount(values[offset + i]);
        }

        return result;
    }

    static int branchless(long[] values, int offset, int bitIndex) {
        long b = 0b1110_1101_1011_0111__0000_0111_0011_0001L >>> (bitIndex >> 6);
        long rightShiftBits = 255L - bitIndex;

        long mask0 = (0x7fff_ffff_ffff_ffffL | (b << 35)) >> rightShiftBits;
        long mask1 = ((0x7fff_ffff_ffff_ffffL | (b << 39)) + (b & 1)) >> rightShiftBits;
        long mask2 = ((0x7fff_ffff_ffff_ffffL | (b << 43)) + ((b >>> 4) & 1)) >> rightShiftBits;
        long mask3 = ((0x7fff_ffff_ffff_ffffL | (b << 47)) + ((b >>> 8) & 1)) >> rightShiftBits;

        return Long.bitCount(values[offset] & mask0)
                + Long.bitCount(values[offset + 1] & mask1)
                + Long.bitCount(values[offset + 2] & mask2)
                + Long.bitCount(values[offset + 3] & mask3);
    }
}
