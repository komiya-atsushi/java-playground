package me.k11i.forkjoin;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParallelMergeSortTest {
    private static ForkJoinPool pool;

    @BeforeAll
    static void setUpAll() {
        pool = new ForkJoinPool();
    }

    @AfterAll
    static void tearDownAll() {
        pool.shutdown();
    }

    static Stream<Arguments> testParameter() {
        List<Arguments> result = new ArrayList<>();

        int[] nValues = {1_000, 20_000, 300_000};
        int[] cutoffValues = {1, 30, 500, 7000};

        for (int n : nValues) {
            for (int cutoff : cutoffValues) {
                if (n <= cutoff) {
                    continue;
                }

                result.add(Arguments.of(n, cutoff));
            }
        }

        return result.stream();
    }

    @ParameterizedTest
    @MethodSource("testParameter")
    void test(int n, int cutoff) {
        long t0 = System.currentTimeMillis();
        double[] values = new SplittableRandom(1).doubles().limit(n).toArray();

        long t1 = System.currentTimeMillis();
        double[] expected = Arrays.copyOf(values, values.length);
        Arrays.parallelSort(expected);

        long t2 = System.currentTimeMillis();
        ParallelMergeSort.sort(values, pool, cutoff);

        long t3 = System.currentTimeMillis();
        for (int i = 0; i < values.length; i++) {
            assertEquals(expected[i], values[i]);
        }

        long t4 = System.currentTimeMillis();

        System.out.printf("generate test = %d, generate expected = %d, exercise = %d, verify = %d%n",
                t1 - t0, t2 - t1, t3 - t2, t4 - t3);
    }
}
