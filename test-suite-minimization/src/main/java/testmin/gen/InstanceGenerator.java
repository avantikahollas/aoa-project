package testmin.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import testmin.model.Instance;
import testmin.model.TestCase;

/**
 * Seeded random generator of feasible instances.
 */
public final class InstanceGenerator {

    private final Random random;

    public InstanceGenerator(long seed) {
        this.random = new Random(seed);
    }

    public Instance generate(int numTests, int numRequirements, double density) {
        if (numTests < 1 && numRequirements > 0) {
            throw new IllegalArgumentException("Need at least one test to cover requirements");
        }
        if (density < 0.0 || density > 1.0) {
            throw new IllegalArgumentException("density must be in [0, 1]: " + density);
        }

        List<List<Integer>> covers = new ArrayList<>(numTests);
        for (int t = 0; t < numTests; t++) {
            covers.add(sampleRequirements(numRequirements, density));
        }

        boolean[] covered = new boolean[numRequirements];
        for (List<Integer> c : covers) {
            for (int r : c) {
                covered[r] = true;
            }
        }
        for (int r = 0; r < numRequirements; r++) {
            if (!covered[r]) {
                covers.get(random.nextInt(numTests)).add(r);
            }
        }

        List<TestCase> tests = new ArrayList<>(numTests);
        for (int t = 0; t < numTests; t++) {
            tests.add(new TestCase(t, covers.get(t).stream().mapToInt(Integer::intValue).toArray()));
        }
        return new Instance(numRequirements, tests);
    }

    /**
     * Bernoulli(p) sampling of {0..n-1} using geometric skips, so the cost is
     * proportional to the number of items chosen rather than to n.
     */
    private List<Integer> sampleRequirements(int n, double p) {
        List<Integer> chosen = new ArrayList<>();
        if (p <= 0.0) {
            return chosen;
        }
        if (p >= 1.0) {
            for (int r = 0; r < n; r++) {
                chosen.add(r);
            }
            return chosen;
        }
        double logQ = Math.log(1.0 - p);
        long r = -1;
        while (true) {
            double u = 1.0 - random.nextDouble(); // in (0, 1]
            r += 1 + (long) Math.floor(Math.log(u) / logQ);
            if (r >= n) {
                return chosen;
            }
            chosen.add((int) r);
        }
    }
}
