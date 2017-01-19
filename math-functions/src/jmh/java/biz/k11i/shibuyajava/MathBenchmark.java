package biz.k11i.shibuyajava;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

public class MathBenchmark {
    @State(Scope.Benchmark)
    public static class Log {
        @Param({"0.001234", "1.234", "123.4", "12340.0", "123.4e+300"})
        public double val;

        @Benchmark
        public double commonsMath3() {
            return org.apache.commons.math3.util.FastMath.log(val);
        }

        @Benchmark
        public double jafama() {
            return net.jafama.FastMath.log(val);
        }

        @Benchmark
        public double jdk() {
            return Math.log(val);
        }
    }

    @State(Scope.Benchmark)
    public static class Log1p {
        @Param({"0.001234", "1.234", "123.4", "12340.0", "123.4e+300"})
        public double val;

        @Benchmark
        public double commonsMath3() {
            return org.apache.commons.math3.util.FastMath.log1p(val);
        }

        @Benchmark
        public double jafama() {
            return net.jafama.FastMath.log1p(val);
        }

        @Benchmark
        public double jdk() {
            return Math.log1p(val);
        }
    }

    @State(Scope.Benchmark)
    public static class Tanh {
        @Param({"0.001234", "1.234", "123.4", "12340.0", "123.4e+300"})
        public double val;

        @Benchmark
        public double commonsMath3() {
            return org.apache.commons.math3.util.FastMath.tanh(val);
        }

        @Benchmark
        public double jafama() {
            return net.jafama.FastMath.tanh(val);
        }

        @Benchmark
        public double jdk() {
            return Math.tanh(val);
        }
    }

    @State(Scope.Benchmark)
    public static class Exp {
        @Param({"0.001234", "1.234", "123.4", "12340.0", "123.4e+300"})
        public double val;

        @Benchmark
        public double commonsMath3() {
            return org.apache.commons.math3.util.FastMath.exp(val);
        }

        @Benchmark
        public double jafama() {
            return net.jafama.FastMath.exp(val);
        }

        @Benchmark
        public double jdk() {
            return Math.exp(val);
        }
    }

    @State(Scope.Benchmark)
    public static class ExpM1 {
        @Param({"-1.234", "0.001234", "1.234", "123.4", "12340.0", "123.4e+300"})
        public double val;

        @Benchmark
        public double commonsMath3() {
            return org.apache.commons.math3.util.FastMath.expm1(val);
        }

        @Benchmark
        public double jafama() {
            return net.jafama.FastMath.expm1(val);
        }

        @Benchmark
        public double jdk() {
            return Math.expm1(val);
        }
    }

    @State(Scope.Benchmark)
    public static class Sqrt {
        @Param({"0.001234", "1.234", "123.4", "12340.0", "123.4e+300"})
        public double val;

        @Benchmark
        public double commonsMath3() {
            return org.apache.commons.math3.util.FastMath.sqrt(val);
        }

        @Benchmark
        public double jafama() {
            return net.jafama.FastMath.sqrt(val);
        }

        @Benchmark
        public double jdk() {
            return Math.sqrt(val);
        }
    }

    @State(Scope.Benchmark)
    public static class Pow {
        @Param({"123e-20", "1.111123456", "12345.67"})
        public double x;

        @Param({"0.00123", "1.111", "1234567"})
        public double y;


        @Benchmark
        public double commonsMath3() {
            return org.apache.commons.math3.util.FastMath.pow(x, y);
        }

        @Benchmark
        public double jafama() {
            return net.jafama.FastMath.pow(x, y);
        }

        @Benchmark
        public double jdk() {
            return Math.pow(x, y);
        }
    }
}
