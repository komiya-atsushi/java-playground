package me.k11i.guava;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.nio.charset.StandardCharsets;
import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@SuppressWarnings("UnstableApiUsage")
@State(Scope.Benchmark)
public class BloomFilterFunnelBenchmark {
    private static final int EXPECTED_INSERTIONS = 1 << 14;
    private static final int INDEX_MOD_MASK = EXPECTED_INSERTIONS - 1;
    private static final double FPP = 0.001;

    public enum FunnelProvider {
        STRING_FUNNEL_ISO_8859_1(Funnels.stringFunnel(StandardCharsets.ISO_8859_1)),
        STRING_FUNNEL_UTF_8(Funnels.stringFunnel(StandardCharsets.UTF_8)),
        STRING_FUNNEL_UTF_16(Funnels.stringFunnel(StandardCharsets.UTF_16)),
        UNENCODED_CHARS_FUNNEL(Funnels.unencodedCharsFunnel());

        private final Funnel<CharSequence> funnel;

        FunnelProvider(Funnel<CharSequence> funnel) {
            this.funnel = funnel;
        }

        Funnel<CharSequence> funnel() {
            return funnel;
        }
    }

    public enum StringsProvider {
        NUMERIC_ONLY {
            @Override
            String generate(long l) {
                return String.format("%020d", l);
            }
        },

        HIRAGANA_ALPHA_MIX {
            private final String[] chars = IntStream.concat(
                    "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん".chars(),
                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".chars())
                    .mapToObj(c -> Character.valueOf((char) c).toString())
                    .toArray(String[]::new);

            @Override
            String generate(long l) {
                return new SplittableRandom(l).ints()
                        .limit(20)
                        .map(i -> i >>> 1)
                        .mapToObj(i -> chars[(i % chars.length)])
                        .collect(Collectors.joining());
            }
        };

        String[] strings() {
            return LongStream.range(0, EXPECTED_INSERTIONS)
                    .map(l -> l * 0x9e3779b97f4a7c15L)
                    .mapToObj(this::generate)
                    .toArray(String[]::new);
        }

        abstract String generate(long l);
    }

    @Param(value = {
            "STRING_FUNNEL_ISO_8859_1",
            "STRING_FUNNEL_UTF_8",
            "STRING_FUNNEL_UTF_16",
            "UNENCODED_CHARS_FUNNEL"
    })
    public FunnelProvider funnelProvider;

    @Param(value = {
            "NUMERIC_ONLY",
            "HIRAGANA_ALPHA_MIX"
    })
    public StringsProvider stringsProvider;

    private BloomFilter<String> filter;
    private String[] strings;
    private int index;

    @Setup
    public void setUp() {
        filter = BloomFilter.create(funnelProvider.funnel(), EXPECTED_INSERTIONS, FPP);
        strings = stringsProvider.strings();

        IntStream.range(0, strings.length / 2)
                .map(i -> i * 2)
                .mapToObj(i -> strings[i])
                .forEach(s -> filter.put(s));
    }

    @Benchmark
    public boolean benchmarkMightContain() {
        String query = strings[index];
        index = (index + 1) & INDEX_MOD_MASK;
        return filter.mightContain(query);
    }
}
