package biz.k11i.jackson;

import biz.k11i.jackson.Twitter.Tweet;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

public class JacksonDeserializationBenchmark {

    @State(Scope.Benchmark)
    public static class DataSet {
        final ObjectMapper defaultObjectMapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        final ObjectMapper objectMapperWithAfterburner = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .registerModule(new AfterburnerModule());
        private final byte[] data;

        public DataSet() {
            try (InputStream is = JacksonDeserializationBenchmark.class.getResourceAsStream("tweets.json.gz");
                 GZIPInputStream gis = new GZIPInputStream(is)) {

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int readBytes;
                while ((readBytes = gis.read(buffer)) > 0) {
                    out.write(buffer, 0, readBytes);
                }

                this.data = out.toByteArray();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        InputStream newInputStream() {
            return new ByteArrayInputStream(data);
        }

        InputStreamReader newInputStreamReader() {
            return new InputStreamReader(newInputStream());
        }

        LangCounter newLangCounter() {
            return new LangCounter();
        }
    }

    static class LangCounter {
        private int count;

        void countIfJa(String lang) {
            if ("ja".equals(lang)) {
                count++;
            }
        }

        void verify() {
            if (count != 327) {
                throw new RuntimeException();
            }
        }
    }

    @Benchmark
    public void _1_baseline(DataSet dataSet) throws IOException {
        LangCounter counter = dataSet.newLangCounter();

        try (InputStreamReader in = dataSet.newInputStreamReader();
             BufferedReader reader = new BufferedReader(in)) {

            String line;
            while ((line = reader.readLine()) != null) {
                Tweet tweet = new ObjectMapper()
                        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                        .readValue(line, Tweet.class);
                counter.countIfJa(tweet.getLang());
            }
        }

        counter.verify();
    }

    @Benchmark
    public void _2_reuseObjectMapper(DataSet dataSet) throws IOException {
        LangCounter counter = dataSet.newLangCounter();

        ObjectMapper objectMapper = dataSet.defaultObjectMapper;

        try (InputStreamReader in = dataSet.newInputStreamReader();
             BufferedReader reader = new BufferedReader(in)) {

            String line;
            while ((line = reader.readLine()) != null) {
                Tweet tweet = objectMapper.readValue(line, Tweet.class);
                counter.countIfJa(tweet.getLang());
            }
        }

        counter.verify();
    }

    @Benchmark
    public void _3_useObjectReader(DataSet dataSet) throws IOException {
        LangCounter counter = dataSet.newLangCounter();

        ObjectReader objectReader = dataSet.defaultObjectMapper
                .readerFor(Tweet.class);

        try (InputStreamReader in = dataSet.newInputStreamReader();
             BufferedReader reader = new BufferedReader(in)) {

            String line;
            while ((line = reader.readLine()) != null) {
                Tweet tweet = objectReader.readValue(line);
                counter.countIfJa(tweet.getLang());
            }
        }

        counter.verify();
    }

    @Benchmark
    public void _4_use_readValues(DataSet dataSet) throws IOException {
        LangCounter counter = dataSet.newLangCounter();

        ObjectReader objectReader = dataSet.defaultObjectMapper
                .readerFor(Tweet.class);

        try (InputStream in = dataSet.newInputStream()) {
            for (Iterator<Tweet> it = objectReader.readValues(in); it.hasNext(); ) {
                Tweet tweet = it.next();
                counter.countIfJa(tweet.getLang());
            }
        }

        counter.verify();
    }

    @Benchmark
    public void _5_useAfterburner(DataSet dataSet) throws IOException {
        LangCounter counter = dataSet.newLangCounter();

        ObjectReader objectReader = dataSet.objectMapperWithAfterburner
                .readerFor(Tweet.class);

        try (InputStream in = dataSet.newInputStream()) {
            for (Iterator<Tweet> it = objectReader.readValues(in); it.hasNext(); ) {
                Tweet tweet = it.next();
                counter.countIfJa(tweet.getLang());
            }
        }

        counter.verify();
    }

    @Benchmark
    public void _6_useStreamingAPI(DataSet dataSet) throws IOException {
        LangCounter counter = dataSet.newLangCounter();

        JsonFactory jsonFactory = dataSet.defaultObjectMapper
                .getFactory();

        try (InputStream in = dataSet.newInputStream();
             JsonParser parser = jsonFactory.createParser(in)) {

            JsonToken token;
            while ((token = parser.nextToken()) != null) {
                if (token != JsonToken.START_OBJECT) {
                    continue;
                }

                boolean langFound = false;
                innerLoop:
                while ((token = parser.nextToken()) != null) {
                    switch (token) {
                        case FIELD_NAME:
                            if (langFound) {
                                continue;
                            }

                            if ("lang".equals(parser.getText())) {
                                counter.countIfJa(parser.nextTextValue());
                                langFound = true;
                            }
                            break;

                        case START_ARRAY:
                        case START_OBJECT:
                            parser.skipChildren();
                            break;

                        case END_OBJECT:
                            break innerLoop;

                        default:
                            break;
                    }
                }
            }
        }

        counter.verify();
    }

    public static void main(String[] args) throws IOException {
        new JacksonDeserializationBenchmark()._6_useStreamingAPI(new DataSet());
    }
}
