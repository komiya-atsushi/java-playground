package biz.k11i.misc;

import java.nio.*;
import java.nio.charset.*;

public class StringByteCounter {
    public static int countBytes(String string, Charset charset) throws CharacterCodingException {
        if (string.isEmpty()) {
            return 0;
        }

        ByteBuffer buffer = ByteBuffer.allocate(8);
        CharBuffer in = CharBuffer.wrap(string);

        CharsetEncoder encoder = charset.newEncoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE)
                .reset();

        int bytesEncoded = 0;
        while (in.length() > 0) {
            CoderResult cr = encoder.encode(in, buffer, true);

            if (buffer.position() == 0) {
                throw new CharacterCodingException();
            }
            bytesEncoded += buffer.position();
            buffer.rewind();

            if (cr.isUnderflow()) {
                break;
            }
            if (cr.isError()) {
                cr.throwException();
            }
        }

        while (true) {
            CoderResult cr = encoder.flush(buffer);
            bytesEncoded += buffer.position();

            if (cr.isUnderflow()) {
                break;
            }
            if (cr.isError()) {
                cr.throwException();
            }

            buffer.rewind();
        }

        return bytesEncoded;
    }
}
