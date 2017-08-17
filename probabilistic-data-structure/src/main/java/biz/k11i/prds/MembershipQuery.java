package biz.k11i.prds;

import com.clearspring.analytics.stream.membership.BloomFilter;
import com.google.common.hash.Funnel;

import java.util.HashSet;
import java.util.Set;

public class MembershipQuery {
    public static void main(String[] args) {
        naive(Helper.words());
        bloomFilter(Helper.words());
        guava_bloomFilter(Helper.words());
    }

    static void naive(String[] words) {
        Set<String> dictionary = new HashSet<>();

        int duplicationCount = 0;
        for (String word : words) {
            if (dictionary.contains(word)) {
                duplicationCount++;
            }
            dictionary.add(word);
        }

        System.out.println(duplicationCount);
        Helper.showMemoryFootprint(dictionary);
    }

    static void bloomFilter(String[] words) {
        BloomFilter filter = new BloomFilter(
                50 /* 想定される要素数 */, 0.01 /* 偽陽性の確率 */);

        int duplicationCount = 0;
        for (String word : words) {
            if (filter.isPresent(word)) {
                duplicationCount++;
            }
            filter.add(word);
        }

        System.out.println(duplicationCount);
        Helper.showMemoryFootprint(filter);
    }

    static void guava_bloomFilter(String[] words) {
        com.google.common.hash.BloomFilter<String> filter = com.google.common.hash.BloomFilter.create(
                (Funnel<String>) (from, into) -> into.putUnencodedChars(from), 50, 0.01);

        int duplicationCount = 0;
        for (String word : words) {
            if (filter.mightContain(word)) {
                duplicationCount++;
            }
            filter.put(word);
        }

        System.out.println(duplicationCount);
        Helper.showMemoryFootprint(filter);
    }
}
