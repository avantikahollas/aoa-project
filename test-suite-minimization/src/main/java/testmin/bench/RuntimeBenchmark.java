package testmin.bench;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import testmin.algo.GreedyMinimizer;
import testmin.algo.Minimizer;
import testmin.gen.InstanceGenerator;
import testmin.model.Instance;
import testmin.model.Solution;
import testmin.verify.CoverageVerifier;

/**
 * Measures greedy running time against the predicted cost k·m + N + n.
 */
public final class RuntimeBenchmark {

    private static final int[] SIZES = {500, 1000, 2000, 3000, 4000, 6000, 8000, 10000, 12000};
    private static final double SPARSE_AVG_TEST_SIZE = 20.0;
    private static final double DENSE_DENSITY = 0.05;
    private static final int WARMUP_RUNS = 5;
    private static final int REPETITIONS = 21;
    private static final long SEED = 42L;

    private RuntimeBenchmark() {
    }

    public static void main(String[] args) throws IOException {
        Path outDir = Path.of(args.length > 0 ? args[0] : "results");
        Minimizer greedy = new GreedyMinimizer();
        InstanceGenerator generator = new InstanceGenerator(SEED);

        try (CsvWriter csv = new CsvWriter(outDir.resolve("runtime.csv"),
                "family", "m", "n", "N", "k", "predicted_ops", "median_ns")) {
            for (String family : new String[] {"sparse", "dense"}) {
                for (int size : SIZES) {
                    double density = family.equals("sparse")
                            ? Math.min(1.0, SPARSE_AVG_TEST_SIZE / size)
                            : DENSE_DENSITY;
                    Instance inst = generator.generate(size, size, density);
                    Solution sol = greedy.solve(inst);
                    if (!CoverageVerifier.coversAll(inst, sol)) {
                        throw new IllegalStateException("Greedy produced an invalid cover");
                    }
                    long median = medianNanos(greedy, inst);
                    long k = sol.size();
                    long predicted = k * inst.numTests() + inst.totalCoverageSize() + inst.numRequirements();

                    csv.writeRow(family, inst.numTests(), inst.numRequirements(),
                            inst.totalCoverageSize(), k, predicted, median);
                    System.out.printf("%-6s m=n=%-6d N=%-9d k=%-5d pred=%-11d time=%.3f ms%n",
                            family, size, inst.totalCoverageSize(), k, predicted, median / 1e6);
                }
            }
        }
    }

    private static long medianNanos(Minimizer minimizer, Instance inst) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            minimizer.solve(inst);
        }
        long[] times = new long[REPETITIONS];
        for (int i = 0; i < REPETITIONS; i++) {
            long start = System.nanoTime();
            minimizer.solve(inst);
            times[i] = System.nanoTime() - start;
        }
        Arrays.sort(times);
        return times[REPETITIONS / 2];
    }
}
