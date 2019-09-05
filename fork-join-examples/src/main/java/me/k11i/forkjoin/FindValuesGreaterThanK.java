package me.k11i.forkjoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class FindValuesGreaterThanK {
    private final int cutoff;
    private final double[] values;
    private final double k;

    public FindValuesGreaterThanK(double[] values, double k, int cutoff) {
        this.values = values;
        this.cutoff = cutoff;
        this.k = k;
    }

    public List<Double> inappropriateResourceSharing(ForkJoinPool pool) {
        List<Double> result = Collections.synchronizedList(new ArrayList<>());
        pool.invoke(new InappropriateResourceSharing(0, values.length, result));
        return result;
    }

    public List<Double> heavyweightMerging(ForkJoinPool pool) {
        return pool.invoke(new HeavyweightMerging(0, values.length));
    }

    public List<Double> lightweight(ForkJoinPool pool) {
        List<Double> result = new ArrayList<>();
        pool.invoke(new Lightweight(0, values.length)).collectResult(result);
        return result;
    }

    class InappropriateResourceSharing extends RecursiveAction {
        private final int left;
        private final int right;

        private final List<Double> result;

        InappropriateResourceSharing(int left, int right, List<Double> result) {
            this.left = left;
            this.right = right;
            this.result = result;
        }

        @Override
        protected void compute() {
            int n = right - left;
            if (n > cutoff) {
                int mid = left + (n >>> 1);

                ForkJoinTask<Void> l = new InappropriateResourceSharing(left, mid, result).fork();
                new InappropriateResourceSharing(mid, right, result).compute();
                l.join();

            } else {
                for (int i = left; i < right; i++) {
                    if (values[i] > k) {
                        result.add(values[i]);
                    }
                }
            }
        }
    }

    class HeavyweightMerging extends RecursiveTask<List<Double>> {
        private final int left;
        private final int right;

        HeavyweightMerging(int left, int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        protected List<Double> compute() {
            int n = right - left;
            if (n > cutoff) {
                int mid = left + (n >>> 1);

                ForkJoinTask<List<Double>> l = new HeavyweightMerging(left, mid).fork();
                List<Double> result = new HeavyweightMerging(mid, right).compute();
                result.addAll(l.join());

                return result;

            } else {
                List<Double> result = new ArrayList<>();

                for (int i = left; i < right; i++) {
                    if (values[i] > k) {
                        result.add(values[i]);
                    }
                }

                return result;
            }
        }
    }

    class Lightweight extends RecursiveTask<LightweightResult> {
        private final int left;
        private final int right;

        Lightweight(int left, int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        protected LightweightResult compute() {
            int n = right - left;
            if (n > cutoff) {
                int mid = left + (n >>> 1);

                ForkJoinTask<LightweightResult> l = new Lightweight(left, mid).fork();
                LightweightResult r = new Lightweight(mid, right).compute();
                return new LightweightResult.Internal(l.join(), r);

            } else {
                List<Double> result = new ArrayList<>();

                for (int i = left; i < right; i++) {
                    if (values[i] > k) {
                        result.add(values[i]);
                    }
                }

                return new LightweightResult.Leaf(result);
            }
        }
    }

    interface LightweightResult {
        void collectResult(List<Double> result);

        class Internal implements LightweightResult {
            private final LightweightResult left;
            private final LightweightResult right;

            Internal(LightweightResult left, LightweightResult right) {
                this.left = left;
                this.right = right;
            }

            @Override
            public void collectResult(List<Double> result) {
                left.collectResult(result);
                right.collectResult(result);
            }
        }

        class Leaf implements LightweightResult {
            private final List<Double> result;

            Leaf(List<Double> result) {
                this.result = result;
            }

            @Override
            public void collectResult(List<Double> result) {
                result.addAll(this.result);
            }
        }
    }
}
