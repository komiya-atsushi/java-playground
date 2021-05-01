package me.k11i.suffixarray.sais;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Wrapper of the primitive int array.
 */
class IntArray {
    private final int[] array;
    private final int offset;
    private final int length;

    public IntArray(int length) {
        this(new int[length], 0, length);
    }

    public IntArray(int[] array, int offset, int length) {
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    int length() {
        return length;
    }

    int get(int i) {
        return array[offset + i];
    }

    void put(int i, int v) {
        array[offset + i] = v;
    }

    int getAndPut(int i, int newV) {
        int result = array[offset + i];
        array[offset + i] = newV;
        return result;
    }

    void fill(int v) {
        Arrays.fill(array, offset, offset + length, v);
    }

    /**
     * Returns a view of this array.
     */
    IntArray subArray(int offset) {
        return subArray(offset, length - offset);
    }

    IntArray subArray(int offset, int length) {
        return new IntArray(this.array, this.offset + offset, length);
    }

    @Override
    public String toString() {
        return Arrays.stream(array, offset, offset + length)
                .mapToObj(String::valueOf).collect(Collectors.joining(",", "[", "]"));
    }
}
