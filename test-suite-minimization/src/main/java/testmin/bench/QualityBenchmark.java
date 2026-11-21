package testmin.bench;

import java.io.IOException;
import java.nio.file.Path;
import testmin.algo.ExactMinimizer;
import testmin.algo.GreedyMinimizer;
import testmin.gen.InstanceGenerator;
import testmin.model.Instance;
import testmin.model.TestCase;

/**
 * Compares the greedy solution size to the optimum (from {@link ExactMinimizer})
 * on small random instances, together with the theoretical bound H(d).
 */
public final class QualityBenchmark {

    private static final int[] NUM_TESTS = {10, 15, 20, 25, 30, 35, 40};
    private static final int NUM_REQUIREMENTS = 60;
    private static final double[] DENSITIES = {0.05, 0.10, 0.20};
    private static final int TRIALS_PER_SETTING = 30;
    private static final long SEED = 7L;

    private QualityBenchmark() {
    }

    public static void main(String[] args) throws IOException {
        Path outDir = Path.of(args.length > 0 ? args[0] : "results");
        GreedyMinimizer greedy = new GreedyMinimizer();
        ExactMinimizer exact = new ExactMinimizer();
        InstanceGenerator generator = new InstanceGenerator(SEED);

        try (CsvWriter csv = new CsvWriter(outDir.resolve("quality.csv"),
                "m", "n", "density", "trial", "greedy", "optimal", "ratio", "harmonic_bound")) {
            for (double density : DENSITIES) {
                for (int m : NUM_TESTS) {
                    int worse = 0;
                    for (int trial = 0; trial < TRIALS_PER_SETTING; trial++) {
                        Instance inst = generator.generate(m, NUM_REQUIREMENTS, density);
                        int g = greedy.solve(inst).size();
                        int opt = exact.solve(inst).size();
                        if (g > opt) {
                            worse++;
                        }
                        csv.writeRow(m, NUM_REQUIREMENTS, density, trial, g, opt,
                                String.format("%.4f", (double) g / opt),
                                String.format("%.4f", harmonic(maxTestSize(inst))));
                    }
                    System.out.printf("density=%.2f m=%-3d greedy worse than optimal in %d/%d trials%n",
                            density, m, worse, TRIALS_PER_SETTING);
                }
            }
        }
    }

    private static int maxTestSize(Instance inst) {
        int max = 0;
        for (TestCase t : inst.tests()) {
            max = Math.max(max, t.size());
        }
        return max;
    }

    /** H(d) = 1 + 1/2 + ... + 1/d. */
    static double harmonic(int d) {
        double h = 0.0;
        for (int i = 1; i <= d; i++) {
            h += 1.0 / i;
        }
        return h;
    }
}
