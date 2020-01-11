package me.k11i.guava;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;

import java.nio.charset.StandardCharsets;
import java.util.stream.LongStream;

@SuppressWarnings("UnstableApiUsage")
public class BloomFilterDemo {
    private static final int EXPECTED_INSERTIONS = 100000;
    private static final int HOLDOUT = 10000;
    private static final double FPP = 0.01;

    public static void main(String[] args) {
        run(Funnels.stringFunnel(StandardCharsets.ISO_8859_1));
        run(Funnels.stringFunnel(StandardCharsets.UTF_8));
        run(Funnels.stringFunnel(StandardCharsets.UTF_16));
        run(Funnels.unencodedCharsFunnel());
    }

    static void run(Funnel<CharSequence> funnel) {
        BloomFilter<String> bf = BloomFilter.create(funnel, EXPECTED_INSERTIONS, FPP);
        LongStream.range(0, EXPECTED_INSERTIONS - HOLDOUT)
                .map(i -> i * 0x9e3779b97f4a7c15L)
                .mapToObj(l -> String.format("%020d", l))
                .forEach(bf::put);

        long falsePositiveCount = LongStream.range(EXPECTED_INSERTIONS - HOLDOUT, EXPECTED_INSERTIONS)
                .map(i -> i * 0x9e3779b97f4a7c15L)
                .mapToObj(l -> String.format("%020d", l))
                .filter(bf::mightContain)
                .count();

        double falsePositiveRate = 100.0 * falsePositiveCount / HOLDOUT;

        System.out.printf("[%s] # of FPs = %d, FPR = %f%%%n",
                funnel.toString(),
                falsePositiveCount,
                falsePositiveRate);
    }
}
