package hw4.hash;

import java.util.List;
import java.util.LinkedList;

public class OomageTestUtility {
    public static boolean haveNiceHashCodeSpread(List<Oomage> oomages, int M) {
        /* have hashCodes that would distribute them fairly evenly across
         * M buckets. To do this, convert each oomage's hashcode in the
         * same way as in the visualizer, i.e. (& 0x7FFFFFF) % M.
         * and ensure that no bucket has fewer than N / 50
         * Oomages and no bucket has more than N / 2.5 Oomages.
         */

        List<Oomage>[] buckets = new LinkedList[M];
        int N = oomages.size();
        for (Oomage omage : oomages) {
            int bucketNum = (omage.hashCode() & 0x7FFFFFFF) % M;
            if (buckets[bucketNum] != null) {
                buckets[bucketNum].add(omage);
            } else {
                buckets[bucketNum] = new LinkedList<>();
                buckets[bucketNum].add(omage);
            }
        }

        for (List<Oomage> list : buckets) {
            if (list == null || list.size() < N / 50 || list.size() > N / 2.5) {
                return false;
            }
        }
        return true;
    }
}
