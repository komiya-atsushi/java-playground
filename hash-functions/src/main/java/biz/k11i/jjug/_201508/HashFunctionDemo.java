package biz.k11i.jjug._201508;

import com.google.common.hash.Hasher;
import net.jpountz.xxhash.StreamingXXHash64;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;

public class HashFunctionDemo {
	private static final int SEED = 98765;

	public static void main(String[] args) {
		// byte
		System.out.println("# byte[]");

		demoBytes(
				"Guava / Murmur3",
				bytes -> com.google.common.hash.Hashing.murmur3_128(SEED).hashBytes(bytes).asLong());
		demoBytes(
				"OpenHFT / Murmur3",
				bytes -> net.openhft.hashing.LongHashFunction.murmur_3(SEED).hashBytes(bytes));
		demoBytes(
				"OpenHFT / xxHash",
				bytes -> net.openhft.hashing.LongHashFunction.xx_r39(SEED).hashBytes(bytes));
		demoBytes(
				"lz4-java / xxHash",
				bytes -> net.jpountz.xxhash.XXHashFactory.safeInstance().hash64().hash(bytes, 0, bytes.length, SEED));

		// long
		System.out.println("# long");

		demoLong(
				"Guava / Murmur3",
				longVal -> com.google.common.hash.Hashing.murmur3_128(SEED).hashLong(longVal).asLong());
		demoLong(
				"OpenHFT / Murmur3",
				longVal -> net.openhft.hashing.LongHashFunction.murmur_3(SEED).hashLong(longVal));

		// String
		System.out.println("# String");

		demoString("Guava / Murmur3",
				text -> com.google.common.hash.Hashing.murmur3_128(SEED).hashUnencodedChars(text).asLong());

		demoString("OpenHFT / Murmur3",
				text -> net.openhft.hashing.LongHashFunction.murmur_3(SEED).hashChars(text));

		// Streaming
		System.out.println("# Streaming");

		demoStream("Guava / Murmur3",
				() -> com.google.common.hash.Hashing.murmur3_128(SEED).newHasher(),
				Hasher::putByte,
				hasher -> hasher.hash().asLong());

		demoStream("lz4-java / xxHash",
				() -> net.jpountz.xxhash.XXHashFactory.safeInstance().newStreamingHash64(SEED),
				(hasher, b) -> hasher.update(new byte[]{b}, 0, 1),
				StreamingXXHash64::getValue);

	}

	static void demoBytes(String name, Function<byte[], Long> hasher) {
		byte[] bytes = {1, 2, 3, 4, 5};

		long result = hasher.apply(bytes);

		System.out.printf("- %s : %16x", name, result);
		System.out.println();
	}

	static void demoLong(String name, LongFunction<Long> hasher) {
		long longVal = 12345L;

		long result = hasher.apply(longVal);

		System.out.printf("- %s : %16x", name, result);
		System.out.println();
	}

	static void demoString(String name, Function<String, Long> hasher) {
		String text = "あなたとJAVA, 今すぐダウンロード";

		long result = hasher.apply(text);

		System.out.printf("- %s : %16x", name, result);
		System.out.println();
	}

	static <H> void demoStream(String name, Supplier<H> hasherGenerator, BiConsumer<H, Byte> hashing, Function<H, Long> finisher) {
		byte[] bytes = {1, 2, 3, 4, 5};

		H hasher = hasherGenerator.get();

		for (byte b : bytes) {
			hashing.accept(hasher, b);
		}

		long result = finisher.apply(hasher);

		System.out.printf("- %s : %16x", name, result);
		System.out.println();
	}
}
