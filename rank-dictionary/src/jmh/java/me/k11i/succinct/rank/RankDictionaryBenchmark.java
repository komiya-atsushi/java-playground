package me.k11i.succinct.rank;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.SplittableRandom;

@State(Scope.Benchmark)

public class RankDictionaryBenchmark {
    @Param({
            "RANK9_64",
            "RANK9_64_INTERLEAVED",
            "RANK9_32",
            "RANK9_32_INTERLEAVED",
    })
    private RankDictionary.Implementation implementation;

    private RankDictionary rankDictionary;
    private SplittableRandom random;
    private int mask;

    @Setup
    public void setUp() {
        int size = 1 << 30;
        mask = size - 1;

        random = new SplittableRandom(123);
        RankDictionary.Builder builder = implementation.builder(size);

        for (int i = 0; i < size; i++) {
            if (random.nextBoolean()) {
                builder.set(i);
            }
        }

        rankDictionary = builder.build();
    }

    @Benchmark
    public long benchmark() {
        return rankDictionary.rank(random.nextInt() & mask);
    }
}
