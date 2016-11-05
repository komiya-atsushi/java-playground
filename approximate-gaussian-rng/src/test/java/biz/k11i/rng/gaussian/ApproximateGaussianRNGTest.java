package biz.k11i.rng.gaussian;

import biz.k11i.rng.GaussianRNG;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

public class ApproximateGaussianRNGTest {

    @Test
    public void test_5() {
        testGoodnessOfFit(5);
    }

    @Test
    public void test_10() {
        testGoodnessOfFit(10);
    }

    @Test
    public void test_20() {
        testGoodnessOfFit(20);
    }

    @Test
    public void test_30() {
        testGoodnessOfFit(30);
    }

    @Test
    public void test_40() {
        testGoodnessOfFit(40);
    }

    @Test
    public void test_50() {
        testGoodnessOfFit(50);
    }

    @Test
    public void test_60() {
        testGoodnessOfFit(60);
    }

    private void testGoodnessOfFit(int n) {
        final int numBins = 200;

        new RNGTester(numBins) {
            private GaussianRNG rng = new ApproximateGaussianRNG(n);

            @Override
            double generateRandomValue(Random random) {
                return rng.generate(random);
            }

            @Override
            public String toString() {
                return String.format("ApproximateGaussianRNG(n=%d)", n);
            }
        }.testGoodnessOfFit();
    }
}


abstract class RNGTester {
    private final int numBins;

    RNGTester(int numBins) {
        this.numBins = numBins;
    }

    abstract double generateRandomValue(Random random);

    private double[] calculateBoundaries() {
        RealDistribution distribution = new NormalDistribution();

        double[] result = new double[numBins];
        for (int i = 0; i < numBins; i++) {
            double p = 1.0 * (i + 1) / numBins;
            result[i] = distribution.inverseCumulativeProbability(p);
        }

        return result;
    }

    private double[] calculateExpectations() {
        double[] result = new double[numBins];
        Arrays.fill(result, 1.0 / numBins);
        return result;
    }

    void testGoodnessOfFit() {
        final double SIGNIFICANCE_LEVEL = 0.001;
        final int NUM_TRIALS = 20;
        final int NUM_ITERATIONS = 2_000_000;
        final int ACCEPTABLE_FAILURE_COUNT = 2;

        double[] boundaries = calculateBoundaries();
        double[] expected = calculateExpectations();

        long[] observed = new long[numBins];

        int failureCount = 0;
        for (int i = 0; i < NUM_TRIALS; i++) {
            Arrays.fill(observed, 0);

            long beginMillis = System.currentTimeMillis();

            for (int j = 0; j < NUM_ITERATIONS; j++) {
                double r = generateRandomValue(ThreadLocalRandom.current());

                int k = Arrays.binarySearch(boundaries, r);
                observed[k < 0 ? ~k : k]++;
            }

            long endMillis = System.currentTimeMillis();

            ChiSquareTest chiSquareTest = new ChiSquareTest();
            double chiSquare = chiSquareTest.chiSquare(expected, observed);
            double pValue = chiSquareTest.chiSquareTest(expected, observed);

            String message = String.format("[%s] chi^2 = %.3f, p-value = %.5f, elapsedMillis = %d",
                    this.toString(), chiSquare, pValue, endMillis - beginMillis);
            System.out.println(message);

            if (pValue < SIGNIFICANCE_LEVEL) {
                failureCount++;
            }
        }

        assertThat(failureCount, lessThan(ACCEPTABLE_FAILURE_COUNT));
    }
}
