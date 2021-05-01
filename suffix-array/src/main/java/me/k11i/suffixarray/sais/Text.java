package me.k11i.suffixarray.sais;

interface Text {
    int alphabetSize();

    int length();

    int charAt(int i);

    default int lastChar() {
        return charAt(length() - 1);
    }
}

/**
 * Represents input text.
 */
class StringText implements Text {
    private final String text;

    StringText(String text) {
        this.text = text;
    }

    @Override
    public int alphabetSize() {
        return (1 << 16) + 1;
    }

    @Override
    public int length() {
        // sentinel character is placed at the end of the text
        return text.length() + 1;
    }

    @Override
    public int charAt(int i) {
        if (i == text.length()) {
            // sentinel character (\u0000)
            return 0;
        }

        // add 1 to characters in the text to make sentinel character the smallest
        return text.charAt(i) + 1;
    }
}

/**
 * Represents reduced problem that consist of LMS (leftmost S-type) substrings.
 */
class LMSText implements Text {
    private final IntArray array;
    private final int alphabetSize;

    public LMSText(IntArray array, int alphabetSize) {
        this.array = array;
        this.alphabetSize = alphabetSize;
    }

    @Override
    public int alphabetSize() {
        return alphabetSize;
    }

    @Override
    public int length() {
        return array.length();
    }

    @Override
    public int charAt(int i) {
        return array.get(i);
    }
}
