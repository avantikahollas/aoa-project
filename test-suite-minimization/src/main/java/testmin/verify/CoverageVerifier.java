package testmin.verify;

import testmin.model.Instance;
import testmin.model.Solution;
import testmin.model.TestCase;

/**
 * Polynomial-time certificate checker for the decision version of
 * Test-Suite Minimization ("can at most k tests cover every requirement?").
 */
public final class CoverageVerifier {

    private CoverageVerifier() {
    }

    /**
     * True iff {@code solution} names distinct, valid test ids whose union covers
     * every requirement of {@code instance}.
     */
    public static boolean coversAll(Instance instance, Solution solution) {
        boolean[] used = new boolean[instance.numTests()];
        boolean[] covered = new boolean[instance.numRequirements()];
        int remaining = instance.numRequirements();

        for (int id : solution.selectedTestIds()) {
            if (id < 0 || id >= instance.numTests() || used[id]) {
                return false;
            }
            used[id] = true;
            TestCase t = instance.test(id);
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

    /** Decision-version check: valid cover using at most {@code budget} tests. */
    public static boolean isValid(Instance instance, Solution solution, int budget) {
        return solution.size() <= budget && coversAll(instance, solution);
    }
}
