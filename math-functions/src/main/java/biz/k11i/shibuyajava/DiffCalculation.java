package biz.k11i.shibuyajava;

import java.util.DoubleSummaryStatistics;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

public class DiffCalculation {
    public static void main(String[] args) {
        measureAndShowDifference("log",
                StrictMath::log,
                org.apache.commons.math3.util.FastMath::log,
                net.jafama.FastMath::log,
                Double.MIN_NORMAL, Double.MAX_VALUE);

        measureAndShowDifference("log1p",
                StrictMath::log1p,
                org.apache.commons.math3.util.FastMath::log1p,
                net.jafama.FastMath::log1p,
                Double.MIN_NORMAL, Double.MAX_VALUE);

        measureAndShowDifference("exp",
                StrictMath::exp,
                org.apache.commons.math3.util.FastMath::exp,
                net.jafama.FastMath::exp,
                Double.MIN_NORMAL, 300.0);

        measureAndShowDifference("tanh",
                StrictMath::tanh,
                org.apache.commons.math3.util.FastMath::tanh,
                net.jafama.FastMath::tanh,
                Double.MIN_NORMAL, Double.MAX_VALUE);

        measureAndShowDifference("sqrt",
                StrictMath::sqrt,
                org.apache.commons.math3.util.FastMath::sqrt,
                net.jafama.FastMath::sqrt,
                Double.MIN_NORMAL, Double.MAX_VALUE);
    }

    static void measureAndShowDifference(
            String functionName,
            DoubleUnaryOperator jdk,
            DoubleUnaryOperator commonsMath3,
            DoubleUnaryOperator jafama,
            double left,
            double right) {
        DoubleSummaryStatistics commonsMath3Stat = measureDifference(jdk, commonsMath3, left, right);
        DoubleSummaryStatistics jafamaStat = measureDifference(jdk, jafama, left, right);

        System.out.printf("avg:%s\t%e\t%e%n", functionName, commonsMath3Stat.getAverage(), jafamaStat.getAverage());
        System.out.printf("max:%s\t%e\t%e%n", functionName, commonsMath3Stat.getMax(), jafamaStat.getMax());
    }

    static DoubleSummaryStatistics measureDifference(DoubleUnaryOperator f1, DoubleUnaryOperator f2, double left, double right) {
        return new Random(12345)
                .doubles(left, right)
                .limit(1_000_000)
                .map(d -> {
                    double r1 = f1.applyAsDouble(d);
                    double r2 = f2.applyAsDouble(d);

                    return Math.abs((r1 - r2) / r1);
                })
                .summaryStatistics();
    }
}
