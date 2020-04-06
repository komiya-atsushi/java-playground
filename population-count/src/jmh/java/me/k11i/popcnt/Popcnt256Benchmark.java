package me.k11i.popcnt;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.SplittableRandom;

@State(Scope.Benchmark)
public class Popcnt256Benchmark {
    private SplittableRandom random;
    private long[] values = new long[4];

    @Setup
    public void setUp() {
        random = new SplittableRandom(1);
    }

    private int fillRandomValues() {
        values[0] = random.nextLong();
        values[1] = random.nextLong();
        values[2] = random.nextLong();
        values[3] = random.nextLong();
        return random.nextInt() & 0xff;
    }

    @Benchmark
    public int loop() {
        int bitIndex = fillRandomValues();
        return Popcnt256.loop(values, 0, bitIndex);
    }

    @Benchmark
    public int branchless() {
        int bitIndex = fillRandomValues();
        return Popcnt256.branchless(values, 0, bitIndex);
    }
}
