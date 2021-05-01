package me.k11i.suffixarray.sais;

import java.util.BitSet;
import java.util.NoSuchElementException;

interface LMSIndexIterator {
    boolean hasNext();

    /**
     * Returns index of the next LMS character/suffix.
     */
    int next();

    static LMSIndexIterator fromCharTypes(BitSet charTypes) {
        return new CharTypesIterator(charTypes);
    }

    class CharTypesIterator implements LMSIndexIterator {
        private final BitSet charTypes;

        private int index = 1; // first character of the text can't be a LMS character
        private int next = -1;
        private boolean isPrevSType;

        CharTypesIterator(BitSet charTypes) {
            this.charTypes = charTypes;
            isPrevSType = charTypes.get(0);
        }

        @Override
        public boolean hasNext() {
            if (next >= 0) {
                return true;
            }

            while (index < charTypes.length()) {
                boolean isSType = charTypes.get(index++);
                if (!isPrevSType && isSType) {
                    isPrevSType = true;
                    next = index - 1;
                    return true;
                }
                isPrevSType = isSType;
            }

            return false;
        }

        @Override
        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int result = next;
            next = -1;
            return result;
        }
    }
}
