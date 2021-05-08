package me.k11i.suffixarray.sais;

import me.k11i.suffixarray.SuffixArray;

import java.util.NoSuchElementException;

public class SAIS {
    /**
     * Straightforward implementation of the SAIS.
     */
    public static class Straightforward {
        private static final Straightforward INSTANCE = new Straightforward();

        public static SuffixArray build(String text) {
            return INSTANCE._build(text);
        }

        SuffixArray _build(String _text) {
            int len = _text.length();
            switch (len) {
                case 0:
                case 1:
                    return new SuffixArray(new int[len], 0, len);
            }

            Text text = new StringText(_text);
            int[] sa = new int[text.length()];

            solveRecursive(text, new IntArray(sa, 0, sa.length));

            // first element of the SA always points a sentinel suffix
            return new SuffixArray(sa, 1, sa.length - 1);
        }

        void solveRecursive(Text text, IntArray sa) {
            ScannedText scanned = ScannedText.scan(text);
            Buckets buckets = scanned.newBuckets(sa);

            sa.fill(-1);

            // stage 1: reduce the problem by at least 1/2
            putLMSIndices(text, buckets, scanned.iterateLMSIndices());
            induceSort(scanned, sa, buckets);

            // find the lexicographic names of LMS substrings
            LMSText lmsText = constructLMSText(scanned, sa);

            // stage 2: solve the reduced problem
            IntArray lmsSA = solveReducedProblem(lmsText, new IntArray(lmsText.length()));

            // stage 3: induce the result for the original problem
            putSortedLMSIndices(scanned, buckets, sa, lmsSA);
            induceSort(scanned, sa, buckets);
        }

        void putLMSIndices(Text text, Buckets buckets, LMSIndexIterator itr) {
            buckets.reset(false);

            while (itr.hasNext()) {
                int lmsIndex = itr.next();
                buckets.backwardAndPut(text.charAt(lmsIndex), lmsIndex);
            }
        }

        void induceSort(ScannedText text, IntArray array, Buckets buckets) {
            // Scan array from left to right and put L-type indices
            buckets.reset(true);
            for (int i = 0; i < array.length(); i++) {
                int index = array.get(i) - 1;
                if (index >= 0) {
                    if (!text.isSType(index)) {
                        buckets.putAndForward(text.charAt(index), index);
                    }
                }
            }

            // Scan array from right to left and put S-type indices
            buckets.reset(false);
            for (int i = array.length() - 1; i >= 0; i--) {
                int index = array.get(i) - 1;
                if (index >= 0) {
                    if (text.isSType(index)) {
                        buckets.backwardAndPut(text.charAt(index), index);
                    }
                }
            }
        }

        LMSText constructLMSText(ScannedText text, IntArray sa) {
            IntArray newTextChars = new IntArray(text.length() / 2 + 1);
            newTextChars.fill(-1);

            int prevIndex = -1;
            int cardinality = 0;
            for (int i = 0; i < text.length(); i++) {
                int index = sa.get(i);
                if (!text.isLMS(index)) {
                    continue;
                }

                if (prevIndex >= 0) {
                    for (int d = 0; d < text.length(); d++) {
                        if (text.charAt(index + d) != text.charAt(prevIndex + d)
                                || text.isSType(index + d) != text.isSType(prevIndex + d)) {
                            cardinality++;
                            break;
                        }
                        if (d > 0 && (text.isLMS(index + d) || text.isLMS(prevIndex + d))) {
                            break;
                        }
                    }
                }

                newTextChars.put(index >>> 1, cardinality);
                prevIndex = index;
            }

            int length = 0;
            for (int i = 0; i < newTextChars.length(); i++) {
                int c = newTextChars.get(i);
                if (c >= 0) {
                    newTextChars.put(length++, c);
                }
            }

            return new LMSText(newTextChars.subArray(0, length), cardinality + 1);
        }

        IntArray solveReducedProblem(Text text, IntArray sa) {
            if (text.length() > text.alphabetSize()) {
                solveRecursive(text, sa);
            } else {
                // generate the suffix array of reduced problem directly
                for (int i = 0; i < text.length(); i++) {
                    sa.put(text.charAt(i), i);
                }
            }
            return sa;
        }

        void putSortedLMSIndices(ScannedText text, Buckets buckets, IntArray sa, IntArray lmsSA) {
            sa.fill(-1);

            IntArray lmsIndices = text.listLMSIndices();
            putLMSIndices(text, buckets, new SortedReverseIterator(lmsIndices, lmsSA));
        }

        static class SortedReverseIterator implements LMSIndexIterator {
            private final IntArray lmsIndices;
            private final IntArray lmsSA;
            private int i;

            /**
             * @param lmsIndices LMS suffix indices in order of appearance.
             * @param lmsSA suffix array of the reduced problem
             */
            SortedReverseIterator(IntArray lmsIndices, IntArray lmsSA) {
                this.lmsIndices = lmsIndices;
                this.lmsSA = lmsSA;
                this.i = lmsIndices.length() - 1;
            }

            @Override
            public boolean hasNext() {
                return i >= 0;
            }

            @Override
            public int next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return lmsIndices.get(lmsSA.get(i--));
            }
        }
    }

    public static class MemoryEfficient extends Straightforward {
        private static final MemoryEfficient INSTANCE = new MemoryEfficient();

        public static SuffixArray build(String text) {
            return INSTANCE._build(text);
        }

        void solveRecursive(Text text, IntArray sa) {
            ScannedText scanned = ScannedText.scan(text);
            Buckets buckets = scanned.newBuckets(sa);

            sa.fill(-1);

            // stage 1: reduce the problem by at least 1/2
            putLMSIndices(text, buckets, scanned.iterateLMSIndices());
            induceSort(scanned, sa, buckets);

            // find the lexicographic names of LMS substrings
            LMSText lmsText = constructLMSText(scanned, sa);

            // stage 2: solve the reduced problem
            IntArray lmsSA = solveReducedProblem(lmsText, sa.subArray(0, lmsText.length()));

            // stage 3: induce the result for the original problem
            putSortedLMSIndices(scanned, buckets, sa, lmsSA);
            induceSort(scanned, sa, buckets);
        }

        LMSText constructLMSText(ScannedText text, IntArray sa) {
            // compact all the sorted LMS substrings into the left side of the sa
            for (int i = 0, j = 0; j < text.numLMS(); i++) {
                int index = sa.get(i);
                if (text.isLMS(index)) {
                    sa.put(j++, index);
                }
            }

            IntArray newTextChars = sa.subArray(text.numLMS());
            newTextChars.fill(-1);

            int prevIndex = -1;
            int cardinality = 0;

            for (int i = 0; i < text.numLMS(); i++) {
                int index = sa.get(i);

                if (prevIndex >= 0) {
                    for (int d = 0; d < text.length(); d++) {
                        if (text.charAt(index + d) != text.charAt(prevIndex + d)
                                || text.isSType(index + d) != text.isSType(prevIndex + d)) {
                            cardinality++;
                            break;
                        }
                        if (d > 0 && (text.isLMS(index + d) || text.isLMS(prevIndex + d))) {
                            break;
                        }
                    }
                }

                newTextChars.put(index >>> 1, cardinality);
                prevIndex = index;
            }

            // compact characters of the reduced problem into the right side of the sa
            for (int i = 0, j = 0; ; i++) {
                int c = newTextChars.get(i);
                if (c >= 0) {
                    newTextChars.put(j++, c);
                    if (j == text.numLMS()) {
                        break;
                    }
                }
            }

            return new LMSText(newTextChars.subArray(0, text.numLMS()), cardinality + 1);
        }

        void putSortedLMSIndices(ScannedText text, Buckets buckets, IntArray sa, IntArray lmsSA) {
            IntArray lmsIndices = text.listLMSIndices(sa.subArray(text.numLMS()));
            for (int i = 0; i < lmsSA.length(); i++) {
                lmsSA.put(i, lmsIndices.get(lmsSA.get(i)));
            }

            lmsIndices.fill(-1);

            putLMSIndices(text, buckets, new SortedReverseIterator(lmsSA));
        }

        static class SortedReverseIterator implements LMSIndexIterator {
            private final IntArray indices;
            private int i;

            /**
             * @param indices sorted indices of the LMS suffix.
             */
            SortedReverseIterator(IntArray indices) {
                this.indices = indices;
                this.i = indices.length() - 1;
            }

            @Override
            public boolean hasNext() {
                return i >= 0;
            }

            @Override
            public int next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return indices.getAndPut(i--, -1);
            }
        }
    }
}
