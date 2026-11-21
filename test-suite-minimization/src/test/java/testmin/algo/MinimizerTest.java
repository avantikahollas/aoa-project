package testmin.algo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import testmin.gen.InstanceGenerator;
import testmin.model.InfeasibleInstanceException;
import testmin.model.Instance;
import testmin.model.Solution;
import testmin.model.TestCase;
import testmin.verify.CoverageVerifier;

class MinimizerTest {

    private final GreedyMinimizer greedy = new GreedyMinimizer();
    private final ExactMinimizer exact = new ExactMinimizer();

    @Test
    void emptyInstanceNeedsNoTests() {
        Instance inst = new Instance(0, List.of());
        assertEquals(0, greedy.solve(inst).size());
        assertEquals(0, exact.solve(inst).size());
    }

    @Test
    void singleTestCoveringEverythingIsChosen() {
        Instance inst = new Instance(4, List.of(
                new TestCase(0, new int[] {0, 1}),
                new TestCase(1, new int[] {0, 1, 2, 3}),
                new TestCase(2, new int[] {2, 3})));
        assertEquals(List.of(1), greedy.solve(inst).selectedTestIds());
        assertEquals(1, exact.solve(inst).size());
    }

    @Test
    void infeasibleInstanceIsRejected() {
        Instance inst = new Instance(2, List.of(new TestCase(0, new int[] {0})));
        assertThrows(InfeasibleInstanceException.class, () -> greedy.solve(inst));
        assertThrows(InfeasibleInstanceException.class, () -> exact.solve(inst));
    }

    /**
     * Classic instance where greedy is suboptimal: the big "middle" test lures
     * greedy first, but the two halves alone are optimal.
     */
    @Test
    void greedyCanBeSuboptimal() {
        // Requirements 0..5. Halves {0,1,2} and {3,4,5}; lure {1,2,4,5} is chosen first.
        Instance inst = new Instance(6, List.of(
                new TestCase(0, new int[] {0, 1, 2}),
                new TestCase(1, new int[] {3, 4, 5}),
                new TestCase(2, new int[] {1, 2, 4, 5})));
        assertEquals(3, greedy.solve(inst).size());
        assertEquals(2, exact.solve(inst).size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void randomInstancesSatisfyCorrectnessAndBounds(long seed) {
        InstanceGenerator gen = new InstanceGenerator(seed);
        for (int trial = 0; trial < 20; trial++) {
            Instance inst = gen.generate(15, 30, 0.15);
            Solution g = greedy.solve(inst);
            Solution opt = exact.solve(inst);

            assertTrue(CoverageVerifier.coversAll(inst, g), "greedy must cover everything");
            assertTrue(CoverageVerifier.coversAll(inst, opt), "exact must cover everything");
            assertTrue(opt.size() <= g.size(), "optimum can never exceed greedy");
            assertEquals(bruteForceOptimum(inst), opt.size(), "branch and bound must be optimal");

            int maxSize = inst.tests().stream().mapToInt(TestCase::size).max().orElse(0);
            assertTrue(g.size() <= harmonic(maxSize) * opt.size() + 1e-9, "greedy within H(d)·OPT");
        }
    }

    /** Independent check of the exact solver: try every subset of tests. */
    private static int bruteForceOptimum(Instance inst) {
        int m = inst.numTests();
        int best = Integer.MAX_VALUE;
        for (int mask = 0; mask < (1 << m); mask++) {
            int size = Integer.bitCount(mask);
            if (size >= best) {
                continue;
            }
            List<Integer> ids = new java.util.ArrayList<>();
            for (int t = 0; t < m; t++) {
                if ((mask & (1 << t)) != 0) {
                    ids.add(t);
                }
            }
            if (CoverageVerifier.coversAll(inst, new Solution(ids))) {
                best = size;
            }
        }
        return best;
    }

    private static double harmonic(int d) {
        double h = 0;
        for (int i = 1; i <= d; i++) {
            h += 1.0 / i;
        }
        return h;
    }
}
