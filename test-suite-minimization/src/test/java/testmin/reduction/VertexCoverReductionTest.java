package testmin.reduction;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import testmin.algo.ExactMinimizer;
import testmin.model.Instance;
import testmin.model.Solution;
import testmin.verify.CoverageVerifier;

/**
 * Empirical check of the reduction's correctness lemma: for every vertex
 * subset S, S is a vertex cover of G iff the tests {t_v : v ∈ S} cover every
 * requirement of the reduced instance.
 */
class VertexCoverReductionTest {

    @Test
    void triangleBuildsExpectedInstance() {
        // Edges: e0={0,1}, e1={1,2}, e2={0,2}
        Graph g = Graph.of(3, new int[][] {{0, 1}, {1, 2}, {0, 2}});
        Instance inst = VertexCoverReduction.reduce(g);

        assertEquals(3, inst.numRequirements());
        assertEquals(3, inst.numTests());
        assertArrayEquals(new int[] {0, 2}, inst.test(0).covers());
        assertArrayEquals(new int[] {0, 1}, inst.test(1).covers());
        assertArrayEquals(new int[] {1, 2}, inst.test(2).covers());
        assertEquals(2, new ExactMinimizer().solve(inst).size()); // min vertex cover of K3 is 2
    }

    @Test
    void everySubsetIsVertexCoverIffItsTestsCoverAllRequirements() {
        Random random = new Random(2026);
        for (int trial = 0; trial < 200; trial++) {
            int numVertices = 1 + random.nextInt(10);
            Graph g = randomGraph(numVertices, random.nextDouble(), random);
            Instance inst = VertexCoverReduction.reduce(g);

            int minVertexCover = Integer.MAX_VALUE;
            for (int mask = 0; mask < (1 << numVertices); mask++) {
                List<Integer> subset = subsetOf(mask, numVertices);
                boolean isCover = g.isVertexCover(subset);
                boolean testsCover = CoverageVerifier.coversAll(inst, new Solution(subset));
                assertEquals(isCover, testsCover, "Lemma fails for subset " + subset + " of " + g.edges());
                if (isCover) {
                    minVertexCover = Math.min(minVertexCover, subset.size());
                }
            }
            assertEquals(minVertexCover, new ExactMinimizer().solve(inst).size());
        }
    }

    private static Graph randomGraph(int n, double p, Random random) {
        List<Graph.Edge> edges = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (random.nextDouble() < p) {
                    edges.add(new Graph.Edge(u, v));
                }
            }
        }
        return new Graph(n, edges);
    }

    private static List<Integer> subsetOf(int mask, int n) {
        List<Integer> subset = new ArrayList<>();
        for (int v = 0; v < n; v++) {
            if ((mask & (1 << v)) != 0) {
                subset.add(v);
            }
        }
        return subset;
    }
}
