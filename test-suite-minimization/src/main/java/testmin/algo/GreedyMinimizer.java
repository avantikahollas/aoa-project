package testmin.algo;

import java.util.ArrayList;
import java.util.List;
import testmin.model.InfeasibleInstanceException;
import testmin.model.Instance;
import testmin.model.Solution;
import testmin.model.TestCase;

/**
 * Greedy test-suite minimization: repeatedly select the test that covers the
 * most still-uncovered requirements (ties broken by smallest id).
 */
public final class GreedyMinimizer implements Minimizer {

    @Override
    public Solution solve(Instance instance) {
        int m = instance.numTests();
        int n = instance.numRequirements();
        int[][] coveredBy = CoverageIndex.build(instance);

        // gain[t] = number of requirements covered by t that are not yet covered.
        int[] gain = new int[m];
        for (int t = 0; t < m; t++) {
            gain[t] = instance.test(t).size();
        }

        boolean[] covered = new boolean[n];
        int remaining = n;
        List<Integer> selected = new ArrayList<>();

        while (remaining > 0) {
            int best = argMax(gain);
            if (best < 0 || gain[best] == 0) {
                throw new InfeasibleInstanceException(
                        remaining + " requirement(s) are not covered by any test");
            }
            selected.add(best);

            TestCase chosen = instance.test(best);
            for (int i = 0; i < chosen.size(); i++) {
                int r = chosen.requirement(i);
                if (!covered[r]) {
                    covered[r] = true;
                    remaining--;
                    for (int t : coveredBy[r]) {
                        gain[t]--;
                    }
                }
            }
        }
        return new Solution(selected);
    }

    /** Index of the largest value (first one on ties), or -1 for an empty array. */
    private static int argMax(int[] values) {
        int best = -1;
        int bestValue = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > bestValue) {
                bestValue = values[i];
                best = i;
            }
        }
        return best;
    }

    @Override
    public String name() {
        return "greedy";
    }
}
