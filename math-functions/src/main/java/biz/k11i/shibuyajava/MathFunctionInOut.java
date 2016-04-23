package biz.k11i.shibuyajava;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

public class MathFunctionInOut {
	private static final Map<String, Double> VALUES;

	static {
		VALUES = new LinkedHashMap<>();
		VALUES.put("Double.NEGATIVE_INFINITY", Double.NEGATIVE_INFINITY);
		VALUES.put("-Double.MAX_VALUE", -Double.MAX_VALUE);
		VALUES.put("-1.1", -1.1);
		VALUES.put("0.0", 0.0);
		VALUES.put("Double.MAX_VALUE", Double.MAX_VALUE);
		VALUES.put("Double.POSITIVE_INFINITY", Double.POSITIVE_INFINITY);
		VALUES.put("Double.NaN", Double.NaN);
	}

	public static void main(String[] args) {
		printHeader();

		calculateAndPrint("log(%s)", Math::log);
		calculateAndPrint("exp(%s)", Math::exp);
		calculateAndPrint("sqrt(%s)", Math::sqrt);
		calculateAndPrint("cos(%s)", Math::cos);

		calculateAndPrint("pow(-1, %s)", v -> Math.pow(-1, v));
		calculateAndPrint("pow(0, %s)", v -> Math.pow(0, v));
		calculateAndPrint("pow(1, %s)", v -> Math.pow(1, v));
		calculateAndPrint("pow(2, %s)", v -> Math.pow(2, v));

		calculateAndPrint("%s + ∞", v -> v + Double.POSITIVE_INFINITY);
	}

	static void printHeader() {
		System.out.println("\t" + VALUES.keySet().stream().collect(Collectors.joining("\t")));

	}

	static void calculateAndPrint(String name, DoubleUnaryOperator operator) {
		System.out.printf(name, "x");

		for (Map.Entry<String, Double> entry : VALUES.entrySet()) {
			double value = entry.getValue();
			double result = operator.applyAsDouble(value);
			System.out.printf("\t%e", result);
		}

		System.out.println();
	}
}
