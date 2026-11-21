package testmin.algo;

import java.util.ArrayList;
import java.util.List;
import testmin.model.InfeasibleInstanceException;
import testmin.model.Instance;
import testmin.model.Solution;
import testmin.model.TestCase;

/**
 * Exact (optimal) test-suite minimization by branch and bound.
 */
public final class ExactMinimizer implements Minimizer {

    @Override
    public Solution solve(Instance instance) {
        if (!instance.isFeasible()) {
            throw new InfeasibleInstanceException("Some requirement is not covered by any test");
        }
        List<Integer> upperBound = new GreedyMinimizer().solve(instance).selectedTestIds();
        Search search = new Search(instance, upperBound);
        search.run(0);
        return new Solution(search.best);
    }

    @Override
    public String name() {
        return "exact";
    }

    /** Mutable state for one branch-and-bound run. */
    private static final class Search {
        private final Instance instance;
        private final int[][] coveredBy;
        private final int[] coverCount; // how many selected tests cover each requirement
        private final List<Integer> current = new ArrayList<>();
        private int uncovered;
        private List<Integer> best;

        Search(Instance instance, List<Integer> initialBest) {
            this.instance = instance;
            this.coveredBy = CoverageIndex.build(instance);
            this.coverCount = new int[instance.numRequirements()];
            this.uncovered = instance.numRequirements();
            this.best = new ArrayList<>(initialBest);
        }

        /** Invariant: every requirement below {@code firstCandidate} is covered. */
        void run(int firstCandidate) {
            if (uncovered == 0) {
                if (current.size() < best.size()) {
                    best = new ArrayList<>(current);
                }
                return;
            }
            // At least one more test is needed, so only continue if that could beat the best.
            if (current.size() + 1 >= best.size()) {
                return;
            }
            int r = firstCandidate;
            while (coverCount[r] > 0) {
                r++;
            }
            for (int t : coveredBy[r]) {
                add(t);
                current.add(t);
                run(r + 1);
                current.remove(current.size() - 1);
                remove(t);
            }
        }

        private void add(int testId) {
            TestCase t = instance.test(testId);
            for (int i = 0; i < t.size(); i++) {
                if (coverCount[t.requirement(i)]++ == 0) {
                    uncovered--;
                }
            }
        }

        private void remove(int testId) {
            TestCase t = instance.test(testId);
            for (int i = 0; i < t.size(); i++) {
                if (--coverCount[t.requirement(i)] == 0) {
                    uncovered++;
                }
            }
        }
    }
}
