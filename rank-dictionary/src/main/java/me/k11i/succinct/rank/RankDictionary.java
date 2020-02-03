package me.k11i.succinct.rank;

/**
 * Provides functionality to answer rank queries for bit vectors.
 */
public interface RankDictionary {
    /**
     * Returns the rank of the {@code i}-th bit.
     *
     * @param i bit index.
     * @return the rank of the {@code i}-th bit.
     */
    long rank(long i);

    enum Implementation {
        RANK9_32 {
            @Override
            Builder builder(long size) {
                if (size > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("RANK9_32 does not support the size greater than Integer.MAX_VALUE.");
                }
                return new Rank9.Builder(size, Rank9.Variant._32);
            }
        },

        RANK9_32_INTERLEAVED {
            @Override
            Builder builder(long size) {
                if (size > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("RANK9_32 does not support the size greater than Integer.MAX_VALUE.");
                }
                return new Rank9.Builder(size, Rank9.Variant._32_INTERLEAVED);
            }
        },

        RANK9_64 {
            @Override
            Builder builder(long size) {
                return new Rank9.Builder(size, Rank9.Variant._64);
            }
        },

        RANK9_64_INTERLEAVED {
            @Override
            Builder builder(long size) {
                return new Rank9.Builder(size, Rank9.Variant._64_INTERLEAVED);
            }
        };

        abstract Builder builder(long size);
    }


    interface Builder {
        void set(long i);

        RankDictionary build();
    }

    abstract class DefaultBuilder implements Builder {
        protected final long size;
        protected final long[] blocks;
        protected int bitCount;

        DefaultBuilder(long size) {
            this.size = size;
            this.blocks = new long[(int) ((size + 64 - 1) >>> 6)];
        }

        @Override
        public void set(long i) {
            blocks[(int) (i >>> 6)] |= 1L << (i & 0x3f);
            bitCount++;
        }
    }
}
