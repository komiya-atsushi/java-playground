package me.k11i.suffixarray;

import me.k11i.suffixarray.sais.SAIS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SuffixArrayTest {
    static final int SEED = 20210430;

    enum ConstructionAlgorithm {
        NAIVE(Naive::build),
        SAIS_STRAIGHTFORWARD(SAIS.Straightforward::build),
        SAIS_MEMORY_EFFICIENT(SAIS.MemoryEfficient::build);

        private final Function<String, SuffixArray> constructor;

        ConstructionAlgorithm(Function<String, SuffixArray> constructor) {
            this.constructor = constructor;
        }

        SuffixArray build(String text) {
            return constructor.apply(text);
        }
    }

    void executeAndVerify(ConstructionAlgorithm alg, String text) {
        List<SuffixArray> result = new ArrayList<>();
        assertThatCode(() -> result.add(alg.build(text)))
                .describedAs("CA = %s, text = %s", alg, text)
                .doesNotThrowAnyException();

        SuffixArray sa = result.get(0);
        assertThat(sa.length()).isEqualTo(text.length());

        for (int i = 0; i < sa.length(); i++) {
            assertThat(sa.suffixIndex(i))
                    .describedAs("CA = %s, text = %s, SA[%d] = %d", alg, text, i, sa.suffixIndex(i))
                    .isGreaterThanOrEqualTo(0)
                    .isLessThan(text.length());
        }

        String prevSuffix = text.substring(sa.suffixIndex(0));
        for (int i = 1; i < sa.length(); i++) {
            String suffix = text.substring(sa.suffixIndex(i));
            assertThat(suffix)
                    .describedAs("CA = %s, text = %s, suffix(SA[%d]) = '%s', suffix(SA[%d]) = '%s'", alg, text, i - 1, prevSuffix, i, suffix)
                    .isGreaterThan(prevSuffix);
            prevSuffix = suffix;
        }
    }

    @ParameterizedTest
    @EnumSource(ConstructionAlgorithm.class)
    void emptyString(ConstructionAlgorithm alg) {
        assertThatCode(() -> alg.build("")).doesNotThrowAnyException();
        assertThat(alg.build("").length()).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(ConstructionAlgorithm.class)
    void shortBinaryText(ConstructionAlgorithm alg) {
        for (int len = 1; len < 5; len++) {
            String format = "%" + len + "s";

            for (int v = 0; v < (len << 1); v++) {
                String text = String.format(format, Integer.toBinaryString(v)).replace(' ', '0');
                executeAndVerify(alg, text);
            }
        }
    }

    @ParameterizedTest
    @EnumSource(ConstructionAlgorithm.class)
    void randomBinaryText(ConstructionAlgorithm alg) {
        RandomTextGenerator r = new RandomTextGenerator(SEED, RandomTextGenerator.CHAR_TABLE_BINARY);

        for (int i = 0; i < 1000; i++) {
            String text = r.generate(100);
            executeAndVerify(alg, text);
        }
    }

    @ParameterizedTest
    @EnumSource(ConstructionAlgorithm.class)
    void randomNumericText(ConstructionAlgorithm alg) {
        RandomTextGenerator r = new RandomTextGenerator(SEED, RandomTextGenerator.CHAR_TABLE_NUMERIC);

        for (int i = 0; i < 1000; i++) {
            String text = r.generate(200);
            executeAndVerify(alg, text);
        }
    }

    @ParameterizedTest
    @EnumSource(ConstructionAlgorithm.class)
    void randomAlphanumericText(ConstructionAlgorithm alg) {
        RandomTextGenerator r = new RandomTextGenerator(SEED);

        for (int i = 0; i < 1000; i++) {
            String text = r.generate(200);
            executeAndVerify(alg, text);
        }
    }

    @ParameterizedTest
    @EnumSource(ConstructionAlgorithm.class)
    void randomTextContainsNullCharacters(ConstructionAlgorithm alg) {
        RandomTextGenerator r = new RandomTextGenerator(SEED, "\u0000abcd".toCharArray());

        for (int i = 0; i < 1000; i++) {
            String text = r.generate(100);
            executeAndVerify(alg, text);
        }
    }
}

class RandomTextGenerator {
    static final char[] CHAR_TABLE_BINARY = "01".toCharArray();
    static final char[] CHAR_TABLE_NUMERIC = "0123456789".toCharArray();
    static final char[] CHAR_TABLE_ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

    private final SplittableRandom rng;
    private final char[] charTable;

    RandomTextGenerator(int seed) {
        this(seed, CHAR_TABLE_ALPHANUMERIC);
    }

    RandomTextGenerator(int seed, char[] charTable) {
        this.rng = new SplittableRandom(seed);
        this.charTable = charTable;
    }

    String generate(int length) {
        return rng.ints(length, 0, charTable.length)
                .map(v -> charTable[v])
                .collect(StringBuilder::new,
                        (b, value) -> b.append((char) value),
                        StringBuilder::append)
                .toString();
    }
}

// For testing
class Naive {
    static class Suffix implements Comparable<Suffix> {
        final String text;
        final int offset;

        Suffix(String text, int offset) {
            this.text = text;
            this.offset = offset;
        }

        @Override
        public int compareTo(Suffix o) {
            return this.text.substring(offset).compareTo(o.text.substring(o.offset));
        }
    }

    static SuffixArray build(String text) {
        Suffix[] suffixes = new Suffix[text.length()];

        for (int i = 0; i < text.length(); i++) {
            suffixes[i] = new Suffix(text, i);
        }

        Arrays.sort(suffixes);
        int[] array = Stream.of(suffixes).mapToInt((s) -> s.offset).toArray();

        return new SuffixArray(array, 0, array.length);
    }
}

