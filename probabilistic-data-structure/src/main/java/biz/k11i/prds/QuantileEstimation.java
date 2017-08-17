package biz.k11i.prds;

import com.clearspring.analytics.stream.quantile.TDigest;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class QuantileEstimation {
    static final int N = 100_000;
    static final SortedMap<Integer, Double> POINTS;
    static {
        NormalDistribution dist = new NormalDistribution();
        POINTS = new TreeMap<>();

        for (int i = -2; i <= 2; i++) {
            POINTS.put(i, dist.cumulativeProbability(i));
        }
    }

    public static void main(String[] args) {
        POINTS.forEach((sigma, nth) -> {
            naive(newGaussianRandomStream(), nth);
            tDigest(newGaussianRandomStream(), nth);
        });
    }

    static DoubleStream newGaussianRandomStream() {
        MersenneTwister mt = new MersenneTwister(98765);
        return IntStream.range(0, N)
                .mapToDouble(ignore -> mt.nextGaussian());
    }

    static void naive(DoubleStream gaussianRandom, double nth) {
        double[] values = gaussianRandom.sorted().toArray();

        int indexOfNth = (int) Math.round(values.length * nth);

        System.out.println(values[indexOfNth]);
        Helper.showMemoryFootprint(values);
    }

    static void tDigest(DoubleStream gaussianRandom, double nth) {
        TDigest digest = new TDigest(5.0);

        gaussianRandom.forEach(value -> {
            digest.add(value);
        });

        System.out.println(digest.quantile(nth));
        Helper.showMemoryFootprint(digest);
    }
}
