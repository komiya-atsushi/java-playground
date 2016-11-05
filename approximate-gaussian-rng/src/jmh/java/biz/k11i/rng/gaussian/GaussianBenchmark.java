package biz.k11i.rng.gaussian;

import biz.k11i.rng.GaussianRNG;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Thread)
public class GaussianBenchmark {
    private ApproximateGaussianRNG approximateGaussianRNG = new ApproximateGaussianRNG(5);

    @Benchmark
    public double threadLocalRandom() {
        return ThreadLocalRandom.current().nextGaussian();
    }

    @Benchmark
    public double approximate() {
        return approximateGaussianRNG.generate(ThreadLocalRandom.current());
    }

    @Benchmark
    public double ziggurat() {
        return GaussianRNG.FAST_RNG.generate(ThreadLocalRandom.current());
    }
}
