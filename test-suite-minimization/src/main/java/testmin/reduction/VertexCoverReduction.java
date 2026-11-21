package testmin.reduction;

import java.util.ArrayList;
import java.util.List;
import testmin.model.Instance;
import testmin.model.TestCase;

/**
 * Polynomial reduction VERTEX-COVER ≤p TEST-SUITE-MINIMIZATION.
 */
public final class VertexCoverReduction {

    private VertexCoverReduction() {
    }

    public static Instance reduce(Graph graph) {
        int numVertices = graph.numVertices();
        List<List<Integer>> incident = new ArrayList<>(numVertices);
        for (int v = 0; v < numVertices; v++) {
            incident.add(new ArrayList<>());
        }
        List<Graph.Edge> edges = graph.edges();
        for (int j = 0; j < edges.size(); j++) {
            incident.get(edges.get(j).u()).add(j);
            incident.get(edges.get(j).v()).add(j);
        }

        List<TestCase> tests = new ArrayList<>(numVertices);
        for (int v = 0; v < numVertices; v++) {
            int[] covers = incident.get(v).stream().mapToInt(Integer::intValue).toArray();
            tests.add(new TestCase(v, covers));
        }
        return new Instance(edges.size(), tests);
    }
}
