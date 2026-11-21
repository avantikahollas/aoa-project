package testmin.reduction;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple undirected graph with vertices {@code 0..numVertices-1} and an
 * indexed list of edges. Immutable. Self-loops are rejected.
 */
public final class Graph {

    /** An undirected edge {u, v}. */
    public record Edge(int u, int v) {
    }

    private final int numVertices;
    private final List<Edge> edges;

    public Graph(int numVertices, List<Edge> edges) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("numVertices must be non-negative");
        }
        for (Edge e : edges) {
            if (e.u() < 0 || e.u() >= numVertices || e.v() < 0 || e.v() >= numVertices) {
                throw new IllegalArgumentException("Edge endpoint out of range: " + e);
            }
            if (e.u() == e.v()) {
                throw new IllegalArgumentException("Self-loops are not allowed: " + e);
            }
        }
        this.numVertices = numVertices;
        this.edges = List.copyOf(edges);
    }

    public int numVertices() {
        return numVertices;
    }

    public List<Edge> edges() {
        return edges;
    }

    /** True iff every edge has at least one endpoint in {@code vertices}. */
    public boolean isVertexCover(List<Integer> vertices) {
        boolean[] in = new boolean[numVertices];
        for (int v : vertices) {
            in[v] = true;
        }
        for (Edge e : edges) {
            if (!in[e.u()] && !in[e.v()]) {
                return false;
            }
        }
        return true;
    }

    /** Builds a graph from a list of {u, v} pairs. Convenience for tests. */
    public static Graph of(int numVertices, int[][] pairs) {
        List<Edge> list = new ArrayList<>(pairs.length);
        for (int[] p : pairs) {
            list.add(new Edge(p[0], p[1]));
        }
        return new Graph(numVertices, list);
    }
}
