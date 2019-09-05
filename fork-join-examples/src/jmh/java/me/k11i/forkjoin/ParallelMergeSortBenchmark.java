package me.k11i.forkjoin;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.util.Arrays;
import java.util.SplittableRandom;
import java.util.concurrent.ForkJoinPool;

@State(Scope.Benchmark)
public class ParallelMergeSortBenchmark {
    private double[] original;
    private double[] values;
    private double[] work;
    private int n = 300_000;
    private ForkJoinPool pool;

    @State(Scope.Benchmark)
    public static class Parameter {
        @Param(value = {"10", "100", "1000", "10000", "100000"})
        public int cutoff;
    }

    @Setup
    public void setUp() {
        original = new SplittableRandom(1).doubles().limit(n).toArray();
        values = new double[n];
        work = new double[n];
        pool = new ForkJoinPool();
    }

    @TearDown
    public void tearDown() {
        pool.shutdown();
    }

    @Setup(Level.Invocation)
    public void setUpInvocation() {
        System.arraycopy(original, 0, values, 0, n);
        System.arraycopy(original, 0, work, 0, n);
    }

    @Benchmark
    public double[] parallelMerge(Parameter p) {
        pool.invoke(new ParallelMergeSort(p.cutoff).newForkJoinTask(values, work));
        return values;
    }

    @Benchmark
    public double[] sequentialSort() {
        Arrays.sort(values);
        return values;
    }

    @Benchmark
    public double[] parallelSort() {
        Arrays.parallelSort(values);
        return values;
    }
}
