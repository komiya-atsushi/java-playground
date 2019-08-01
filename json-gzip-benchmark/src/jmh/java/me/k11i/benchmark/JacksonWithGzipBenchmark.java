package me.k11i.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

@State(Scope.Benchmark)
public class JacksonWithGzipBenchmark {
    @Param({"1", "10"})
    private int numCopies;

    @Param({"1", "6"})
    private int compressionLevel;

    private List<Pojo> objects;
    private ObjectMapper mapper;
    private ObjectMapper afterburner;

    @Setup
    public void setUp() {
        objects = new ArrayList<>(100 * numCopies);
        for (int i = 0; i < numCopies; i++) {
            objects.addAll(Pojo.objects);
        }

        mapper = new ObjectMapper();
        afterburner = new ObjectMapper()
                .registerModule(new AfterburnerModule());
    }

    @Benchmark
    public byte[] baseline() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStreamWithCompressionLevel(compressionLevel, baos)) {
            mapper.writeValue(gzipOut, objects);
            gzipOut.close();
            return baos.toByteArray();
        }
    }

    @Benchmark
    public byte[] afterburner() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStreamWithCompressionLevel(compressionLevel, baos)) {
            afterburner.writeValue(gzipOut, objects);
            gzipOut.close();
            return baos.toByteArray();
        }
    }

    @Benchmark
    public byte[] gzipOS64K() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStreamWithCompressionLevel(compressionLevel, baos, 65536)) {
            mapper.writeValue(gzipOut, objects);
            gzipOut.close();
            return baos.toByteArray();
        }
    }

    @Benchmark
    public byte[] bufferedOS64K() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStreamWithCompressionLevel(compressionLevel, baos);
             BufferedOutputStream buffer = new BufferedOutputStream(gzipOut, 65536)) {
            mapper.writeValue(buffer, objects);
            buffer.flush();
            gzipOut.close();
            return baos.toByteArray();
        }
    }

    @Benchmark
    public byte[] afterburner_gzipOS64K() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStreamWithCompressionLevel(compressionLevel, baos, 65536)) {
            afterburner.writeValue(gzipOut, objects);
            gzipOut.close();
            return baos.toByteArray();
        }
    }

    @Benchmark
    public byte[] bufferedOS64K_gzipOS64K() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStreamWithCompressionLevel(compressionLevel, baos, 65536);
             BufferedOutputStream buffer = new BufferedOutputStream(gzipOut, 65536)) {
            mapper.writeValue(buffer, objects);
            buffer.flush();
            gzipOut.close();
            return baos.toByteArray();
        }
    }

    @Benchmark
    public byte[] afterburner_bufferedOS64K() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStreamWithCompressionLevel(compressionLevel, baos);
             BufferedOutputStream buffer = new BufferedOutputStream(gzipOut, 65536)) {
            afterburner.writeValue(buffer, objects);
            buffer.flush();
            gzipOut.close();
            return baos.toByteArray();
        }
    }

    @Benchmark
    public byte[] afterburner_bufferedOS64K_gzipOS64K() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStreamWithCompressionLevel(compressionLevel, baos, 65536);
             BufferedOutputStream buffer = new BufferedOutputStream(gzipOut, 65536)) {
            afterburner.writeValue(buffer, objects);
            buffer.flush();
            gzipOut.close();
            return baos.toByteArray();
        }
    }
}
