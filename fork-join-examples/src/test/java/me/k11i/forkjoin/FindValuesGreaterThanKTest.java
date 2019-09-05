package me.k11i.forkjoin;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindValuesGreaterThanKTest {
    private int cutoff = 5000;
    private double k = 0.5;
    private double[] values = new SplittableRandom(1).doubles().limit(100_000).toArray();
    private double[] expected = Arrays.stream(values).filter(x -> x > 0.5).sorted().toArray();
    private FindValuesGreaterThanK sut = new FindValuesGreaterThanK(values, k, cutoff);
    private static ForkJoinPool pool;

    @BeforeAll
    static void setUpAll() {
        pool = new ForkJoinPool();
    }

    @AfterAll
    static void tearDownAll() {
        pool.shutdown();
    }

    @Test
    void testInappropriateResourceSharing() {
        verify(sut.inappropriateResourceSharing(pool));
    }

    @Test
    void testHeavyweightMerging() {
        verify(sut.heavyweightMerging(pool));
    }

    @Test
    void testLightweight() {
        verify(sut.lightweight(pool));
    }

    void verify(List<Double> result) {
        assertEquals(expected.length, result.size());

        Collections.sort(result);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result.get(i));
        }
    }
}