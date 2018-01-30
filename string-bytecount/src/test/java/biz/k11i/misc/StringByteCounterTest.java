package biz.k11i.misc;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(Theories.class)
public class StringByteCounterTest {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @DataPoints
    public static final String[] TEXTS = {
            "",
            "\u0000",
            "Lorem ipsum dolor sit amet",
            "いろはにほへと ちるぬるを",
            "東京都葛飾区",
            "東京都\u845B\uDB40\uDD01飾区", // 艸かんむりに曷
            "🙇",
    };

    @DataPoints
    public static final Charset[] CHARSETS = {
            Charset.forName("UTF-8"),
            Charset.forName("UTF-16"),
            Charset.forName("Windows-31J"),
            Charset.forName("EUC-JP"),
            Charset.forName("ISO2022JP")
    };

    @Theory
    public void variousCharsets(String text, Charset charset) throws CharacterCodingException {
        int byteCount = StringByteCounter.countBytes(text, charset);

        assertThat(
                String.format("text = %s, charset = %s, byteCount = %d", text, charset, byteCount),
                byteCount, is(equalTo(text.getBytes(charset).length)));
    }

    @Test
    public void longText() throws CharacterCodingException {
        int length = 5 * 1024 * 1024;

        String e = "aあ";
        StringBuilder b = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            b.append(e);
        }

        int byteCount = StringByteCounter.countBytes(b.toString(), UTF8);

        assertThat(byteCount, is(equalTo(e.getBytes(UTF8).length * length)));
    }

    @Test
    public void fuzzing() throws CharacterCodingException {
        for (int i = 0; i < 50000; i++) {
            String randomString = generateRandomUnicodeString(64);
            int byteCount = StringByteCounter.countBytes(randomString, UTF8);

            assertThat(
                    String.format("randomString = %s, byteCount = %d", randomString, byteCount),
                    byteCount, is(equalTo(randomString.getBytes(UTF8).length)));
        }
    }

    private String generateRandomUnicodeString(int length) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int codePoint = ThreadLocalRandom.current().nextInt(Character.MAX_CODE_POINT);

            if (!Character.isDefined(codePoint)
                    || (codePoint <= Character.MAX_SURROGATE && Character.isSurrogate((char) codePoint))
                    || Character.getType(codePoint) == Character.PRIVATE_USE) {
                continue;
            }

            b.appendCodePoint(codePoint);
        }

        return b.toString();
    }
}