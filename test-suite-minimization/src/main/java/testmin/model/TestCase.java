package testmin.model;

import java.util.Arrays;

/**
 * One regression test and the set of requirements it covers.
 */
public final class TestCase {

    private final int id;
    private final int[] covers;

    /**
     * @param id     test id; must equal this test's index in its {@link Instance}
     * @param covers requirement ids covered by this test (duplicates are removed)
     * @throws IllegalArgumentException if {@code id} or any requirement id is negative
     */
    public TestCase(int id, int[] covers) {
        if (id < 0) {
            throw new IllegalArgumentException("Test id must be non-negative: " + id);
        }
        int[] sorted = covers.clone();
        Arrays.sort(sorted);
        if (sorted.length > 0 && sorted[0] < 0) {
            throw new IllegalArgumentException("Requirement ids must be non-negative: " + sorted[0]);
        }
        this.id = id;
        this.covers = dedupSorted(sorted);
    }

    private static int[] dedupSorted(int[] sorted) {
        if (sorted.length == 0) {
            return sorted;
        }
        int unique = 1;
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i] != sorted[unique - 1]) {
                sorted[unique++] = sorted[i];
            }
        }
        return unique == sorted.length ? sorted : Arrays.copyOf(sorted, unique);
    }

    public int id() {
        return id;
    }

    /** Number of distinct requirements this test covers. */
    public int size() {
        return covers.length;
    }

    /** The {@code i}-th covered requirement id (ascending order). Allocation-free access for hot loops. */
    public int requirement(int i) {
        return covers[i];
    }

    /** Defensive copy of the covered requirement ids, in ascending order. */
    public int[] covers() {
        return covers.clone();
    }

    /** Largest requirement id covered, or -1 if this test covers nothing. */
    public int maxRequirement() {
        return covers.length == 0 ? -1 : covers[covers.length - 1];
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TestCase other && id == other.id && Arrays.equals(covers, other.covers);
    }

    @Override
    public int hashCode() {
        return 31 * Integer.hashCode(id) + Arrays.hashCode(covers);
    }

    @Override
    public String toString() {
        return "t" + id + Arrays.toString(covers);
    }
}
