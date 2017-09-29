package biz.k11i.mvn;

import org.apache.maven.index.reader.ChunkReader;
import org.apache.maven.index.reader.IndexReader;
import org.apache.maven.index.reader.Record;
import org.apache.maven.index.reader.RecordExpander;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class MavenIndexReaderDemo {
    public static class MavenArtifact {
        private int updateCount = 1;
        private Record latestRecord;

        public MavenArtifact(Record record) {
            this.latestRecord = record;
        }

        public void updateIfNeeded(Record record) {
            long latestTimestamp = (long) latestRecord.getExpanded().get(Record.FILE_MODIFIED);
            long recordTimestamp = (long) record.getExpanded().get(Record.FILE_MODIFIED);

            if (recordTimestamp > latestTimestamp) {
                latestRecord = record;
            }

            updateCount++;
        }

        public String getKey() {
            return latestRecord.get(Record.GROUP_ID)
                    + "|" + latestRecord.get(Record.ARTIFACT_ID);
        }

        public int getUpdateCount() {
            return updateCount;
        }
    }

    private static Map<String, MavenArtifact> loadLatestArtifacts(IndexReader reader) {
        RecordExpander expander = new RecordExpander();
        Map<String, MavenArtifact> artifacts = new HashMap<>();

        for (ChunkReader chunkReader : reader) {
            for (Map<String, String> recordMap : chunkReader) {
                Record record = expander.apply(recordMap);

                if (record.getType() != Record.Type.ARTIFACT_ADD) {
                    // Artifact の追加以外はすべて無視する
                    continue;
                }

                Map<Record.EntryKey, Object> map = record.getExpanded();

                if (map.get(Record.CLASSIFIER) != null) {
                    // sources や javadoc なども無視する (CLASSIFIER 指定なし = jar を表す)
                    continue;
                }

                // (バージョン情報抜きの) groupId + artifactId で artifact を識別する
                String groupId = map.get(Record.GROUP_ID).toString();
                String artifactId = map.get(Record.ARTIFACT_ID).toString();
                String key = groupId + "|" + artifactId;

                MavenArtifact history = artifacts.get(key);
                if (history == null) {
                    artifacts.put(key, new MavenArtifact(record));
                } else {
                    history.updateIfNeeded(record);
                }
            }
        }

        return artifacts;
    }

    private static Map<String, Long> countArtifactsPerGroup(Set<String> keys) {
        Map<String, LongAdder> artifactCountsPerGroup = new HashMap<>();

        for (String key : keys) {
            String groupId = key.split("\\|")[0];
            artifactCountsPerGroup.computeIfAbsent(groupId, ignore -> new LongAdder())
                    .increment();
        }

        return artifactCountsPerGroup.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().longValue()));
    }

    public static void main(String[] args) throws IOException {
        Path baseDir = Paths.get("tmp/maven-index/cache");
        if (!Files.exists(baseDir)) {
            Files.createDirectories(baseDir);
        }

        IndexReader reader = new IndexReader(null, CachedResourceHandler.getLatest(baseDir));

        Map<String, MavenArtifact> artifacts = loadLatestArtifacts(reader);
        Map<String, Long> artifactCountsPerGroup = countArtifactsPerGroup(artifacts.keySet());

        LongSummaryStatistics artifactsStat = artifacts.values().stream()
                .mapToLong(MavenArtifact::getUpdateCount)
                .summaryStatistics();
        LongSummaryStatistics groupStat = artifactCountsPerGroup.values().stream()
                .mapToLong(l -> l)
                .summaryStatistics();

        List<MavenArtifact> top10Artifacts = artifacts.values().stream()
                .sorted((a, b) -> Integer.compare(b.getUpdateCount(), a.getUpdateCount()))
                .limit(20)
                .collect(Collectors.toList());
        List<Map.Entry<String, Long>> top10Group = artifactCountsPerGroup.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .collect(Collectors.toList());


        System.out.printf("# of artifacts: %d%n", artifacts.size());

        System.out.printf("Mean # of versions: %f%n", artifactsStat.getAverage());
        System.out.printf("Mean # of artifacts per group: %f%n", groupStat.getAverage());

        System.out.println("--- Artifact");
        for (int i = 0; i < top10Artifacts.size(); i++) {
            MavenArtifact artifact = top10Artifacts.get(i);
            System.out.printf("[%d] %s, %d%n", i + 1, artifact.getKey(), artifact.getUpdateCount());
        }

        System.out.println("--- Group");
        for (int i = 0; i < top10Group.size(); i++) {
            Map.Entry<String, Long> entry = top10Group.get(i);
            System.out.printf("[%d] %s, %d%n", i + 1, entry.getKey(), entry.getValue());
        }
    }
}
