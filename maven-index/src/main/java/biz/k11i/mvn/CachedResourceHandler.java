package biz.k11i.mvn;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.maven.index.reader.ResourceHandler;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class CachedResourceHandler implements ResourceHandler {
    private static final String MAVEN_REPOSITORY_URL = "https://repo1.maven.org/maven2/.index/";
    private static final String INDEX_PROPERTIES_FILENAME = "nexus-maven-repository-index.properties";
    private static final String INDEX_GZ_FILENAME = "nexus-maven-repository-index.gz";

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    private final Path dir;
    private final List<Closeable> closeables = new ArrayList<>();

    private CachedResourceHandler(Path dir) {
        this.dir = dir;
    }

    private <T extends Closeable> T addClosable(T c) {
        closeables.add(c);
        return c;
    }

    @Override
    public synchronized void close() throws IOException {
        for (Closeable closeable : closeables) {
            closeable.close();
        }
    }

    @Override
    public Resource locate(String name) throws IOException {
        return () -> addClosable(Files.newInputStream(dir.resolve(name), StandardOpenOption.READ));
    }

    public Path path() {
        return dir;
    }

    public static CachedResourceHandler getLatest(Path baseDir) throws IOException {
        // プロパティファイルだけ先にダウンロードする
        byte[] propBytes = downloadProperties();
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(propBytes));

        String timestamp = properties.getProperty("nexus.index.timestamp").split(" ")[0];
        Path dir = baseDir.resolve(timestamp);

        if (Files.isDirectory(dir)) {
            if (Files.isRegularFile(dir.resolve(INDEX_PROPERTIES_FILENAME))
                    && Files.isRegularFile(dir.resolve(INDEX_GZ_FILENAME))) {
                // すでに index をダウンロード済みなら、それを利用する
                return new CachedResourceHandler(dir);
            }
        } else {
            Files.createDirectories(dir);
        }

        // index をダウンロードしてキャッシュを作る
        System.out.println("Download index file...");
        Files.write(dir.resolve(INDEX_PROPERTIES_FILENAME), propBytes);
        downloadAndStoreIndex(dir);

        return new CachedResourceHandler(dir);
    }

    private static byte[] downloadProperties() throws IOException {
        Call call = CLIENT.newCall(new Request.Builder()
                .url(MAVEN_REPOSITORY_URL + INDEX_PROPERTIES_FILENAME)
                .build());
        try (Response response = call.execute()) {
            return response.body().bytes();
        }
    }

    private static void downloadAndStoreIndex(Path dir) throws IOException {
        Call call = CLIENT.newCall(new Request.Builder()
                .url(MAVEN_REPOSITORY_URL + INDEX_GZ_FILENAME)
                .build());

        try (Response response = call.execute();
             InputStream in = response.body().byteStream()) {
            Files.copy(in, dir.resolve(INDEX_GZ_FILENAME));
        }
    }
}
