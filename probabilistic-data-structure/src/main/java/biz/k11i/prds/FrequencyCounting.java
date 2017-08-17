package biz.k11i.prds;

import com.clearspring.analytics.stream.frequency.CountMinSketch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;


public class FrequencyCounting {
    public static void main(String[] args) {
        naive(Helper.words());
        countMinSketch(Helper.words());
    }

    static void naive(String[] words) {
        Map<String, LongAdder> freq = new HashMap<>();

        for (String word : words) {
            freq.computeIfAbsent(word,
                    ignore -> new LongAdder()).increment();
        }

        System.out.println(freq.get("dolor"));
        Helper.showMemoryFootprint(freq);
    }

    static void countMinSketch(String[] words) {
        CountMinSketch sketch = new CountMinSketch(10, 30, 1);

        for (String word : words) {
            sketch.add(word, 1);
        }

        // "dolor" の頻度の推定値が返ってくる
        System.out.println(sketch.estimateCount("dolor"));
        Helper.showMemoryFootprint(sketch);
    }
}
