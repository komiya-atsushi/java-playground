package me.k11i.suffixarray.sais;

import java.util.BitSet;

/**
 * Represents the text and also holds character types (L- or S-type) and cumulative frequencies of each character.
 */
class ScannedText implements Text {
    private final Text text;
    private final BitSet charTypes;
    private final int alphabetSize;
    private final int numLMS;
    final int[] cumulativeFreq;

    ScannedText(Text text, BitSet charTypes, int alphabetSize, int[] cumulativeFreq, int numLMS) {
        this.text = text;
        this.charTypes = charTypes;
        this.alphabetSize = alphabetSize;
        this.cumulativeFreq = cumulativeFreq;
        this.numLMS = numLMS;
    }

    /**
     * Classifies all the characters as L- or S-type, and counts the frequency of each character.
     */
    static ScannedText scan(Text text) {
        BitSet charTypes = new BitSet(text.length());
        int[] freq = new int[text.alphabetSize()];
        int numLMS = 0;

        // sentinel character is S-type
        charTypes.set(text.length() - 1, true);
        boolean isRightCharSType = true;

        int rightCh = text.lastChar();
        freq[rightCh]++;

        int maxCh = rightCh;

        for (int i = text.length() - 2; i >= 0; i--) {
            int ch = text.charAt(i);
            freq[ch]++;

            boolean isSType = ch < rightCh || (ch == rightCh && isRightCharSType);
            charTypes.set(i, isSType);

            if (isRightCharSType && !isSType) {
                numLMS++;
            }

            isRightCharSType = isSType;
            rightCh = ch;
            maxCh = Math.max(maxCh, ch);
        }

        // accumulate frequencies
        for (int i = 1; i < maxCh + 1; i++) {
            freq[i] += freq[i - 1];
        }

        return new ScannedText(text, charTypes, maxCh + 1, freq, numLMS);
    }

    @Override
    public int alphabetSize() {
        return alphabetSize;
    }

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public int charAt(int i) {
        return text.charAt(i);
    }

    @Override
    public int lastChar() {
        return text.lastChar();
    }

    /**
     * Returns number of LMS substrings in the text.
     */
    int numLMS() {
        return numLMS;
    }

    /**
     * Returns true if i-th character is the LMS (leftmost S-type) character.
     */
    boolean isLMS(int i) {
        return i > 0 && charTypes.get(i) && !charTypes.get(i - 1);
    }

    /**
     * Returns true if i-th character is the S-type character.
     */
    boolean isSType(int i) {
        return charTypes.get(i);
    }

    /**
     * Creates {@link Buckets} object that points beginning or end of each bucket.
     */
    Buckets newBuckets(IntArray array) {
        return new Buckets(this.cumulativeFreq, this.alphabetSize, array);
    }

    /**
     * Returns iterator that enumerates indices of the LMS character in order of appearance.
     */
    LMSIndexIterator iterateLMSIndices() {
        return LMSIndexIterator.fromCharTypes(charTypes);
    }

    IntArray listLMSIndices() {
        IntArray buf = new IntArray(numLMS);
        return listLMSIndices(buf);
    }

    /**
     * Lists all indices of the LMS character and put its indices into {@code buf}.
     */
    IntArray listLMSIndices(IntArray buf) {
        int count = 0;
        for (LMSIndexIterator i = iterateLMSIndices(); i.hasNext(); ) {
            buf.put(count++, i.next());
        }
        return buf;
    }
}
