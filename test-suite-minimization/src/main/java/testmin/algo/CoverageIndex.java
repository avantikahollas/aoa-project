package testmin.algo;

import testmin.model.Instance;
import testmin.model.TestCase;

/**
 * Inverted index: for each requirement r, the ids of the tests that cover r.
 */
final class CoverageIndex {

    private CoverageIndex() {
    }

    /** {@code result[r]} lists the ids of tests covering requirement r, in ascending order. */
    static int[][] build(Instance instance) {
        int n = instance.numRequirements();
        int[] counts = new int[n];
        for (TestCase t : instance.tests()) {
            for (int i = 0; i < t.size(); i++) {
                counts[t.requirement(i)]++;
            }
        }
        int[][] coveredBy = new int[n][];
        for (int r = 0; r < n; r++) {
            coveredBy[r] = new int[counts[r]];
        }
        int[] fill = new int[n];
        for (TestCase t : instance.tests()) {
            for (int i = 0; i < t.size(); i++) {
                int r = t.requirement(i);
                coveredBy[r][fill[r]++] = t.id();
            }
        }
        return coveredBy;
    }
}
