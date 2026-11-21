package testmin.model;

import java.util.List;

/**
 * An instance of Test-Suite Minimization: a universe of requirements
 * {@code U = {0, ..., n-1}} and a list of tests, each covering a subset of U.
 */
public final class Instance {

    private final int numRequirements;
    private final List<TestCase> tests;
    private final long totalCoverageSize;

    /**
     * @throws IllegalArgumentException if ids are inconsistent or a test covers
     *                                  a requirement outside {@code [0, numRequirements)}
     */
    public Instance(int numRequirements, List<TestCase> tests) {
        if (numRequirements < 0) {
            throw new IllegalArgumentException("numRequirements must be non-negative: " + numRequirements);
        }
        List<TestCase> copy = List.copyOf(tests); // also rejects null elements
        long total = 0;
        for (int i = 0; i < copy.size(); i++) {
            TestCase t = copy.get(i);
            if (t.id() != i) {
                throw new IllegalArgumentException("Test at index " + i + " has id " + t.id());
            }
            if (t.maxRequirement() >= numRequirements) {
                throw new IllegalArgumentException(
                        "Test " + i + " covers requirement " + t.maxRequirement()
                                + " but only " + numRequirements + " exist");
            }
            total += t.size();
        }
        this.numRequirements = numRequirements;
        this.tests = copy;
        this.totalCoverageSize = total;
    }

    /** n = |U|. */
    public int numRequirements() {
        return numRequirements;
    }

    /** m = number of tests. */
    public int numTests() {
        return tests.size();
    }

    public TestCase test(int id) {
        return tests.get(id);
    }

    /** Unmodifiable view of all tests. */
    public List<TestCase> tests() {
        return tests;
    }

    /** N = sum of |S_i| over all tests: the input size that drives running time. */
    public long totalCoverageSize() {
        return totalCoverageSize;
    }

    /** True iff every requirement is covered by at least one test (i.e. some solution exists). */
    public boolean isFeasible() {
        boolean[] covered = new boolean[numRequirements];
        int remaining = numRequirements;
        for (TestCase t : tests) {
            for (int i = 0; i < t.size(); i++) {
                int r = t.requirement(i);
                if (!covered[r]) {
                    covered[r] = true;
                    remaining--;
                }
            }
        }
        return remaining == 0;
    }
}
