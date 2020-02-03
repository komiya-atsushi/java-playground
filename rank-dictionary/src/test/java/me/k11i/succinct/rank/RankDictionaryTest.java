package me.k11i.succinct.rank;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.BitSet;
import java.util.SplittableRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RankDictionaryTest {
    static Stream<Arguments> parameters() {
        int[] sizes = {
                1,
                10,
                63,
                64,
                65,
                511,
                512,
                513,
                1023,
                1024,
                1025,
                2047,
                2048,
                2049,
                10_000_000
        };

        return Stream.of(RankDictionary.Implementation.values())
                .flatMap(impl -> IntStream.of(sizes)
                        .mapToObj(size -> Arguments.of(impl, size)));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void testRandom(RankDictionary.Implementation implementation, int size) {
        SplittableRandom r = new SplittableRandom(size);
        BitSet bits = new BitSet(size);
        for (int i = 0; i < size; i++) {
            bits.set(i, r.nextBoolean());
        }

        RankDictionary.Builder builder = implementation.builder(size);
        for (int i = 0; i < size; i++) {
            if (bits.get(i)) {
                builder.set(i);
            }
        }

        RankDictionary rankDictionary = builder.build();

        long expectedRank = 0;
        for (int i = 0; i < size; i++) {
            final int ii = i;
            assertEquals(
                    expectedRank,
                    rankDictionary.rank(i),
                    () -> String.format("i = %d", ii));

            if (bits.get(i)) {
                expectedRank++;
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void testAllZero(RankDictionary.Implementation implementation, int size) {
        RankDictionary.Builder builder = implementation.builder(size);
        RankDictionary rankDictionary = builder.build();

        for (int i = 0; i < size; i++) {
            final int ii = i;
            assertEquals(0,
                    rankDictionary.rank(i),
                    () -> String.format("i = %d", ii));
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void testAllOne(RankDictionary.Implementation implementation, int size) {
        RankDictionary.Builder builder = implementation.builder(size);
        for (int i = 0; i < size; i++) {
            builder.set(i);
        }

        RankDictionary rankDictionary = builder.build();

        for (int i = 0; i < size; i++) {
            final int ii = i;
            assertEquals(i,
                    rankDictionary.rank(i),
                    () -> String.format("i = %d", ii));
        }
    }
}
