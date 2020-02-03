package me.k11i.succinct.rank;

/**
 * Implementation of the data structure called Rank9.
 *
 * @see <a href="http://vigna.di.unimi.it/ftp/papers/Broadword.pdf">Broadword Implementation of Rank/Select Queries</a>
 */
@SuppressWarnings("DuplicatedCode")
public class Rank9 {
    private static class Rank9_32 implements RankDictionary {
        private final int[] first;
        private final long[] second;
        private final long[] blocks;

        private Rank9_32(long[] blocks) {
            int tableSize = (blocks.length + 8 - 1) >>> 3;
            this.first = new int[tableSize];
            this.second = new long[tableSize];
            this.blocks = blocks;

            long count = 0;
            for (int i = 0; i < blocks.length; i += 8) {
                int index = i >>> 3;

                first[index] = (int) count;
                count += Long.bitCount(blocks[i]);

                for (int j = i + 1, limit = Math.min(i + 8, blocks.length); j < limit; j++) {
                    second[index] |= (count - first[index]) << (63 - (j - i) * 9);
                    count += Long.bitCount(blocks[j]);
                }
            }
        }

        @Override
        public long rank(long i) {
            int blockIndex = (int) (i >>> 6);
            int tableIndex = (int) (i >>> 9);
            int shiftBits = 63 - (blockIndex & 0x7) * 9;
            long mask = (1L << (i & 0x3f)) - 1;

            return first[tableIndex]
                    + ((second[tableIndex] >>> shiftBits) & 0x1ff)
                    + Long.bitCount(blocks[blockIndex] & mask);
        }
    }

    private static class Rank9_32Interleaved implements RankDictionary {
        private final long[] table;
        private final long[] blocks;

        private Rank9_32Interleaved(long[] blocks) {
            int tableSize = (blocks.length + 4 - 1) >>> 2;
            this.table = new long[tableSize];
            this.blocks = blocks;

            long count = 0;
            for (int i = 0; i < blocks.length; i += 4) {
                int index = i / 4;

                long baseCount = count;
                table[index] = count;
                count += Long.bitCount(blocks[i]);

                for (int j = i + 1, limit = Math.min(i + 4, blocks.length); j < limit; j++) {
                    table[index] |= (count - baseCount) << (59 - (j - i) * 9);
                    count += Long.bitCount(blocks[j]);
                }
            }
        }

        @Override
        public long rank(long i) {
            int blockIndex = (int) (i >>> 6);
            int index = (int) (i >>> 8);
            int shiftBits = 59 - (blockIndex & 0x3) * 9;
            long mask = (1L << (i & 0x3f)) - 1;

            return (int) table[index]
                    + ((table[index] >>> shiftBits) & 0x1ff)
                    + Long.bitCount(blocks[blockIndex] & mask);
        }
    }

    private static class Rank9_64 implements RankDictionary {
        private final long[] first;
        private final long[] second;
        private final long[] blocks;

        private Rank9_64(long[] blocks) {
            int tableSize = (blocks.length + 8 - 1) >>> 3;
            this.first = new long[tableSize];
            this.second = new long[tableSize];
            this.blocks = blocks;

            long count = 0;
            for (int i = 0; i < blocks.length; i += 8) {
                int index = i >>> 3;

                first[index] = count;
                count += Long.bitCount(blocks[i]);

                for (int j = i + 1, limit = Math.min(i + 8, blocks.length); j < limit; j++) {
                    second[index] |= (count - first[index]) << (63 - (j - i) * 9);
                    count += Long.bitCount(blocks[j]);
                }
            }
        }

        @Override
        public long rank(long i) {
            int blockIndex = (int) (i >>> 6);
            int tableIndex = (int) (i >>> 9);
            int shiftBits = 63 - (blockIndex & 0x7) * 9;
            long mask = (1L << (i & 0x3f)) - 1;

            return first[tableIndex]
                    + ((second[tableIndex] >>> shiftBits) & 0x1ff)
                    + Long.bitCount(blocks[blockIndex] & mask);
        }
    }

    private static class Rank9_64Interleaved implements RankDictionary {
        private final long[] table;
        private final long[] blocks;

        private Rank9_64Interleaved(long[] blocks) {
            int tableSize = ((blocks.length + 8 - 1) >>> 2) & ~1;
            this.table = new long[tableSize];
            this.blocks = blocks;

            long count = 0;
            for (int i = 0; i < blocks.length; i += 8) {
                int index = (i >>> 2) & ~1;

                table[index] = count;
                count += Long.bitCount(blocks[i]);

                for (int j = i + 1, limit = Math.min(i + 8, blocks.length); j < limit; j++) {
                    table[index + 1] |= (count - table[index]) << (63 - (j - i) * 9);
                    count += Long.bitCount(blocks[j]);
                }
            }
        }

        @Override
        public long rank(long i) {
            int blockIndex = (int) (i >>> 6);
            int index = (int) ((i >>> 8) & ~1);
            int shiftBits = 63 - (blockIndex & 0x7) * 9;
            long mask = (1L << (i & 0x3f)) - 1;

            return table[index]
                    + ((table[index + 1] >>> shiftBits) & 0x1ff)
                    + Long.bitCount(blocks[blockIndex] & mask);
        }
    }

    enum Variant {
        _32,
        _32_INTERLEAVED,
        _64,
        _64_INTERLEAVED
    }

    static class Builder extends RankDictionary.DefaultBuilder {
        private final Variant variant;

        Builder(long size, Variant variant) {
            super(size);
            this.variant = variant;
        }

        @Override
        public RankDictionary build() {
            switch (variant) {
                case _32:
                    return new Rank9_32(this.blocks);
                case _32_INTERLEAVED:
                    return new Rank9_32Interleaved(this.blocks);
                case _64:
                    return new Rank9_64(this.blocks);
                case _64_INTERLEAVED:
                    return new Rank9_64Interleaved(this.blocks);
                default:
                    throw new RuntimeException();
            }
        }
    }
}
