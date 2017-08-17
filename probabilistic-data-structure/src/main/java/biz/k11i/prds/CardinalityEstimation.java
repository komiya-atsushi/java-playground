package biz.k11i.prds;

import com.clearspring.analytics.hash.MurmurHash;
import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import net.agkn.hll.HLL;

import java.util.HashSet;
import java.util.Set;

public class CardinalityEstimation {
    public static void main(String[] args) {
        naive(Helper.words());
        hyperLogLogPlus(Helper.words());
        agkn_HLL(Helper.words());
    }

    static void naive(String[] words) {
        Set<String> dictionary = new HashSet<>();

        for (String word : words) {
            dictionary.add(word);
        }

        System.out.println(dictionary.size());
        Helper.showMemoryFootprint(dictionary);
    }

    static void hyperLogLogPlus(String[] words) {
        HyperLogLogPlus hll = new HyperLogLogPlus(10);

        for (String word : words) {
            hll.offer(word);
        }

        System.out.println(hll.cardinality());
        Helper.showMemoryFootprint(hll);
    }

    static void agkn_HLL(String[] words) {
        HLL hll = new HLL(10, 1);

        for (String word : words) {
            // オブジェクトを明示的に数値に変換する必要がある
            long val = MurmurHash.hash64(word);
            hll.addRaw(val);
        }

        System.out.println(hll.cardinality());
        Helper.showMemoryFootprint(hll);
    }
}
