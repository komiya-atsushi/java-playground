package me.k11i.forkjoin;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class ParallelMergeSort {
    private final int cutoff;

    ParallelMergeSort(int cutoff) {
        this.cutoff = cutoff;
    }

    public ForkJoinTask<Void> newForkJoinTask(double[] values, double[] work) {
        return new MergeSortAction(values, work, 0, values.length);
    }

    public static void sort(double[] values, ForkJoinPool pool, int cutoff) {
        double[] work = Arrays.copyOf(values, values.length);
        ParallelMergeSort mergeSort = new ParallelMergeSort(cutoff);
        pool.invoke(mergeSort.newForkJoinTask(values, work));
    }

    class MergeSortAction extends RecursiveAction {
        private final double[] values;
        private final double[] work;
        private final int left;
        private final int right;

        MergeSortAction(double[] values, double[] work, int left, int right) {
            this.values = values;
            this.work = work;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            int n = right - left;
            if (n > cutoff) {
                int mid = left + (n >>> 1);

                invokeAll(new MergeSortAction(work, values, left, mid),
                        new MergeSortAction(work, values, mid, right));

                merge(mid);

            } else {
                Arrays.sort(values, left, right);
            }
        }

        private void merge(int mid) {
            int l = left;
            int r = mid;
            int i = left;
            for (; l < mid && r < right; i++) {
                if (work[l] <= work[r]) {
                    values[i] = work[l++];
                } else {
                    values[i] = work[r++];
                }
            }

            if (l < mid) {
                System.arraycopy(work, l, values, i, mid - l);
            } else {
                System.arraycopy(work, r, values, i, right - r);
            }
        }
    }
}
