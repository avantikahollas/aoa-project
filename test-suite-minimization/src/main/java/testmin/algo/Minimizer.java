package testmin.algo;

import testmin.model.InfeasibleInstanceException;
import testmin.model.Instance;
import testmin.model.Solution;

/** Strategy for choosing a subset of tests that covers every requirement. */
public interface Minimizer {

    /**
     * Returns a set of tests covering every requirement of {@code instance}.
     *
     * @throws InfeasibleInstanceException if some requirement is covered by no test
     */
    Solution solve(Instance instance);

    /** Short human-readable name, used in benchmark output. */
    String name();
}
