package me.k11i.forkjoin;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
public class FindValuesGreaterThanKBenchmark {
    private double[] values;
    private double k = 0.5;
    private int n = 500_000;
    private int cutoff = 8000;
    private ForkJoinPool pool;

    @Setup
    public void setUp() {
        values = new SplittableRandom(1).doubles().limit(n).toArray();
        pool = new ForkJoinPool();
    }

    @TearDown
    public void tearDown() {
        pool.shutdown();
    }

    @Benchmark
    public List<Double> inappropriateResourceSharing() {
        return new FindValuesGreaterThanK(values, k, cutoff)
                .inappropriateResourceSharing(pool);
    }

    @Benchmark
    public List<Double> heavyweightMerging() {
        return new FindValuesGreaterThanK(values, k, cutoff)
                .heavyweightMerging(pool);
    }

    @Benchmark
    public List<Double> lightweight() {
        return new FindValuesGreaterThanK(values, k, cutoff)
                .lightweight(pool);
    }

    @Benchmark
    public List<Double> sequentialStream() {
        return Arrays.stream(values)
                .filter(x -> x > k)
                .boxed()
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<Double> parallelStream() {
        return Arrays.stream(values)
                .parallel()
                .filter(x -> x > k)
                .boxed()
                .collect(Collectors.toList());
    }
}
