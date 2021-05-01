package me.k11i.suffixarray.sais;

/**
 * Holds pointers to unused locations for each bucket.
 */
class Buckets {
    private final int[] cumulativeFreq;
    private final IntArray array;
    private final int[] pointers;

    Buckets(int[] cumulativeFreq, int alphabetSize, IntArray array) {
        this.cumulativeFreq = cumulativeFreq;
        this.array = array;
        this.pointers = new int[alphabetSize];
    }

    /**
     * Initializes all pointers to points beginning or end of each bucket.
     */
    void reset(boolean pointsBeginningOfBucket) {
        if (pointsBeginningOfBucket) {
            pointers[0] = 0;
            System.arraycopy(cumulativeFreq, 0, pointers, 1, pointers.length - 1);
        } else {
            System.arraycopy(cumulativeFreq, 0, pointers, 0, pointers.length);
        }
    }

    /**
     * Moves pointer backward first and then puts a value.
     */
    void backwardAndPut(int ch, int v) {
        int index = --pointers[ch];
        array.put(index, v);
    }

    /**
     * Puts a value first and then moves pointer forward.
     */
    void putAndForward(int ch, int v) {
        int index = pointers[ch]++;
        array.put(index, v);
    }
}
