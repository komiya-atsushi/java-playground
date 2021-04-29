package me.k11i.suffixarray;

public class SuffixArray {
    private final int[] array;
    private final int offset;
    private final int length;

    public SuffixArray(int[] array, int offset, int length) {
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    /**
     * Returns the length of the suffix array without sentinel suffix.
     */
    public int length() { return length; }

    /**
     * Returns the index of the k-th smallest suffix.
     *
     * @param k 0-based rank of the suffix to get.
     */
    public int suffixIndex(int k) {
        if (k < 0) {
            throw new IndexOutOfBoundsException("k should be greater than or equal to 0 but " + k);
        }
        if (k >= length) {
            throw new IndexOutOfBoundsException(String.format("k should be less than %d but %d", length, k));
        }
        return array[offset + k];
    }
}
