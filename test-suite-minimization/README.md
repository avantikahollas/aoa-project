# testmin: Regression Test-Suite Minimization

Given tests that each cover a set of requirements, choose the fewest tests that
still cover every requirement. The decision version is NP-complete (reduction
from Vertex Cover); this project implements a greedy heuristic, an exact
baseline, the reduction, and benchmarks.

## Layout

| Package | Contents |
|---|---|
| `testmin.model` | `TestCase`, `Instance`, `Solution` (immutable, validated) |
| `testmin.algo` | `Minimizer` interface, `GreedyMinimizer`, `ExactMinimizer` (branch and bound) |
| `testmin.verify` | `CoverageVerifier`: polynomial-time certificate check (NP membership) |
| `testmin.reduction` | `Graph`, `VertexCoverReduction` (VC ≤p test-suite minimization) |
| `testmin.gen` | `InstanceGenerator`: seeded, always-feasible random instances |
| `testmin.bench` | `RuntimeBenchmark`, `QualityBenchmark` → CSV in `results/` |
| `scripts/plot.py` | Figures from the CSVs |

## Running

Requires Java 17+ and Maven.

```bash
mvn test                                   # unit tests
mvn -q compile
java -cp target/classes testmin.bench.RuntimeBenchmark results
java -cp target/classes testmin.bench.QualityBenchmark results
python3 scripts/plot.py results            # needs matplotlib, numpy, scipy
```

## Complexity

* Greedy: O(k·m + N + n). Here m is the number of tests, n the number of requirements,
  N the total coverage size and k the number of tests selected.
  Approximation ratio ≤ H(d) ≤ ln d + 1, where d is the largest test size.
* Verifier: O(m + n + N).
* Reduction: O(|V| + |E|).
* Exact: exponential worst case; small instances only.

## Results summary (from the included run)

* Runtime: fitted model `time ≈ 0.20 ns·(k·m) + 9.4 ns·(N + n)`, R² = 0.978.
  The per-unit costs differ because the k·m term is a sequential scan while the
  N term does scattered memory updates. The sparse family tracks the model
  closely above m = 3000. The dense family's ratio rises from about 0.5 to 1.1 as
  the data outgrows the CPU caches.
* Quality: greedy was optimal in most small instances. The mean ratio to the optimum
  was 1.05 and the worst was 1.43, far below the theoretical H(d) bound (≥ 2.28).
