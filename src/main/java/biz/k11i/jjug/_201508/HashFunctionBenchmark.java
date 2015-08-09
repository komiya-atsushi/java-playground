package biz.k11i.jjug._201508;

import com.google.common.hash.Hashing;
import net.jpountz.xxhash.XXHashFactory;
import net.openhft.hashing.LongHashFunction;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public abstract class HashFunctionBenchmark {
	static final int SEED = 98765;

	@State(Scope.Benchmark)
	public static abstract class ByteArrayBenchmark {
		public enum ByteArrayData {
			_8(8),
			_1024(1024),
			_64K(65536);

			final byte[] data;

			ByteArrayData(int length) {
				data = new byte[length];
				new Random(12345).nextBytes(data);
			}
		}

		@Param({"_8", "_1024", "_64K"})
		public ByteArrayData dataSet;

		public abstract void hashByteArray(Blackhole bh);

		// Implementations

		// CityHash

		public static class OpenHFT_CityHash extends ByteArrayBenchmark {
			@Benchmark
			public void hashByteArray(Blackhole bh) {
				bh.consume(LongHashFunction.city_1_1(SEED).hashBytes(dataSet.data));
			}
		}

		// MurmurHash3

		public static class OpenHFT_Murmur3 extends ByteArrayBenchmark {
			@Benchmark
			public void hashByteArray(Blackhole bh) {
				bh.consume(LongHashFunction.murmur_3(SEED).hashBytes(dataSet.data));
			}
		}

		public static class Guava_Murmur3 extends ByteArrayBenchmark {
			@Benchmark
			public void hashByteArray(Blackhole bh) {
				bh.consume(Hashing.murmur3_128(SEED).hashBytes(dataSet.data).asLong());
			}
		}

		// xxHash

		public static class OpenHFT_xxHash extends ByteArrayBenchmark {
			@Benchmark
			public void hashByteArray(Blackhole bh) {
				bh.consume(LongHashFunction.xx_r39(SEED).hashBytes(dataSet.data));
			}
		}

		public static class LZ4_xxHash_PureJava extends ByteArrayBenchmark {
			@Benchmark
			public void hashByteArray(Blackhole bh) {
				bh.consume(XXHashFactory.safeInstance().hash64().hash(dataSet.data, 0, dataSet.data.length, SEED));
			}
		}

		public static class LZ4_xxHash_Unsafe extends ByteArrayBenchmark {
			@Benchmark
			public void hashByteArray(Blackhole bh) {
				bh.consume(XXHashFactory.unsafeInstance().hash64().hash(dataSet.data, 0, dataSet.data.length, SEED));
			}
		}

		public static class LZ4_xxHash_Native extends ByteArrayBenchmark {
			@Benchmark
			public void hashByteArray(Blackhole bh) {
				bh.consume(XXHashFactory.nativeInstance().hash64().hash(dataSet.data, 0, dataSet.data.length, SEED));
			}
		}
	}

	@State(Scope.Benchmark)
	public static abstract class LongBenchmark {
		private static final long VALUE = 123456789012345L;

		public abstract void hashLong(Blackhole bh);

		// google-guava
		public static class Guava_Murmur3 extends LongBenchmark {
			@Benchmark
			public void hashLong(Blackhole bh) {
				bh.consume(Hashing.murmur3_128(SEED).hashLong(VALUE).asLong());
			}
		}

		// OpenHFT

		public static class OpenHFT_CityHash extends LongBenchmark {
			@Benchmark
			public void hashLong(Blackhole bh) {
				bh.consume(LongHashFunction.city_1_1(SEED).hashLong(VALUE));
			}
		}

		public static class OpenHFT_Murmur3 extends LongBenchmark {
			@Benchmark
			public void hashLong(Blackhole bh) {
				bh.consume(LongHashFunction.murmur_3(SEED).hashLong(VALUE));
			}
		}

		public static class OpenHFT_xxHash extends LongBenchmark {
			@Benchmark
			public void hashLong(Blackhole bh) {
				bh.consume(LongHashFunction.xx_r39(SEED).hashLong(VALUE));
			}
		}
	}

	@State(Scope.Benchmark)
	public static abstract class StringBenchmark {
		private static final String VALUE = IntStream.range(0, 65536)
				.mapToObj(v -> String.format("%d", v % 10))
				.collect(Collectors.joining());

		public abstract void hashString(Blackhole bh);

		// google-guava

		public static class Guava_Murmur3 extends StringBenchmark {
			@Benchmark
			public void hashString(Blackhole bh) {
				bh.consume(Hashing.murmur3_128(SEED).hashUnencodedChars(VALUE).asLong());
			}
		}

		// OpenHFT

		public static class OpenHFT_CityHash extends StringBenchmark {
			@Benchmark
			public void hashString(Blackhole bh) {
				bh.consume(LongHashFunction.city_1_1(SEED).hashChars(VALUE));
			}
		}

		public static class OpenHFT_Murmur3 extends StringBenchmark {
			@Benchmark
			public void hashString(Blackhole bh) {
				bh.consume(LongHashFunction.murmur_3(SEED).hashChars(VALUE));
			}
		}

		public static class OpenHFT_xxHash extends StringBenchmark {
			@Benchmark
			public void hashString(Blackhole bh) {
				bh.consume(LongHashFunction.xx_r39(SEED).hashChars(VALUE));
			}
		}
	}
}
