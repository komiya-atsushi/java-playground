package me.k11i.benchmark;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPOutputStreamWithCompressionLevel extends GZIPOutputStream {
    public GZIPOutputStreamWithCompressionLevel(int level, OutputStream out) throws IOException {
        super(out);
        def.setLevel(level);
    }

    public GZIPOutputStreamWithCompressionLevel(int level, OutputStream out, int size) throws IOException {
        super(out, size);
        def.setLevel(level);
    }
}
