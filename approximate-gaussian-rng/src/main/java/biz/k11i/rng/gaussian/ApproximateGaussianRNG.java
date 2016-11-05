package biz.k11i.rng.gaussian;

import biz.k11i.rng.GaussianRNG;

import java.util.Random;

public class ApproximateGaussianRNG implements GaussianRNG {
    private final int N;
    private final double N_HALF;
    private final double M;

    public ApproximateGaussianRNG(int n) {
        N = n;
        N_HALF = 0.5 * N;
        M = Math.sqrt(N / 12.0);
    }

    @Override
    public double generate(Random random) {
        double t = 0.0;

        for (int i = 0; i < N; i++) {
            t += random.nextDouble();
        }

        return (t - N_HALF) / M;
    }
}
