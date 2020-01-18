# guava-bloom-filter

## Benchmark result

### Java 8

```
# JMH version: 1.21
# VM version: JDK 1.8.0_111, Java HotSpot(TM) 64-Bit Server VM, 25.111-b14
# VM options: <none>
# Warmup: 5 iterations, 10 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time

Benchmark                                                 (funnelProvider)   (stringsProvider)   Mode  Cnt     Score     Error   Units
BloomFilterFunnelBenchmark.benchmarkMightContain  STRING_FUNNEL_ISO_8859_1        NUMERIC_ONLY  thrpt   25  4468.988 ± 503.390  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain  STRING_FUNNEL_ISO_8859_1  HIRAGANA_ALPHA_MIX  thrpt   25  2985.201 ±  24.995  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain       STRING_FUNNEL_UTF_8        NUMERIC_ONLY  thrpt   25  4515.737 ±  75.485  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain       STRING_FUNNEL_UTF_8  HIRAGANA_ALPHA_MIX  thrpt   25  2668.791 ±  42.054  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain      STRING_FUNNEL_UTF_16        NUMERIC_ONLY  thrpt   25  2560.604 ±  17.492  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain      STRING_FUNNEL_UTF_16  HIRAGANA_ALPHA_MIX  thrpt   25  2006.310 ± 266.193  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain    UNENCODED_CHARS_FUNNEL        NUMERIC_ONLY  thrpt   25  3305.086 ± 380.414  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain    UNENCODED_CHARS_FUNNEL  HIRAGANA_ALPHA_MIX  thrpt   25  3449.155 ± 389.339  ops/ms
```


### Java 11

```
# JMH version: 1.21
# VM version: JDK 11.0.2, OpenJDK 64-Bit Server VM, 11.0.2+9
# VM options: <none>
# Warmup: 5 iterations, 10 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time

Benchmark                                                 (funnelProvider)   (stringsProvider)   Mode  Cnt     Score     Error   Units
BloomFilterFunnelBenchmark.benchmarkMightContain  STRING_FUNNEL_ISO_8859_1        NUMERIC_ONLY  thrpt   25  5524.452 ±  93.976  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain  STRING_FUNNEL_ISO_8859_1  HIRAGANA_ALPHA_MIX  thrpt   25  3090.977 ±  61.322  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain       STRING_FUNNEL_UTF_8        NUMERIC_ONLY  thrpt   25  5343.421 ±  84.840  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain       STRING_FUNNEL_UTF_8  HIRAGANA_ALPHA_MIX  thrpt   25  2988.135 ±  11.156  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain      STRING_FUNNEL_UTF_16        NUMERIC_ONLY  thrpt   25  2459.293 ±  25.800  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain      STRING_FUNNEL_UTF_16  HIRAGANA_ALPHA_MIX  thrpt   25  1666.821 ±   5.439  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain    UNENCODED_CHARS_FUNNEL        NUMERIC_ONLY  thrpt   25  3533.423 ± 115.303  ops/ms
BloomFilterFunnelBenchmark.benchmarkMightContain    UNENCODED_CHARS_FUNNEL  HIRAGANA_ALPHA_MIX  thrpt   25  3572.144 ± 619.394  ops/ms
```
