"""Make the report figures from the benchmark CSVs.

Reads results/runtime.csv and results/quality.csv and writes
figures/runtime.png, figures/ratio.png and figures/quality.png.

Runtime model: time ~= a*(k*m) + b*(N + n), with a and b fitted by
non-negative least squares.

Usage: python3 scripts/plot.py
"""
import csv
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt  # noqa: E402
import numpy as np  # noqa: E402
from scipy.optimize import nnls  # noqa: E402

RESULTS = Path("results")
FIGURES = Path("figures")
COLORS = {"sparse": "#1f3f99", "dense": "#c2410c", 0.05: "#1f3f99", 0.1: "#c2410c", 0.2: "#047857"}

plt.rcParams.update({"font.family": "serif", "font.size": 8})


def read(name):
    with open(RESULTS / name, newline="") as f:
        return list(csv.DictReader(f))


def save(fig, name):
    fig.tight_layout()
    fig.savefig(FIGURES / name, dpi=300)
    plt.close(fig)


def runtime_figures():
    rows = read("runtime.csv")
    family = np.array([r["family"] for r in rows])
    m = np.array([int(r["m"]) for r in rows])
    km = np.array([int(r["k"]) * int(r["m"]) for r in rows], dtype=float)
    nn = np.array([int(r["N"]) + int(r["n"]) for r in rows], dtype=float)
    ms = np.array([int(r["median_ns"]) for r in rows]) / 1e6

    (a, b), _ = nnls(np.column_stack([km, nn]), ms)
    model = a * km + b * nn
    r2 = 1 - np.sum((ms - model) ** 2) / np.sum((ms - ms.mean()) ** 2)
    print(f"a = {a * 1e6:.3f} ns, b = {b * 1e6:.3f} ns, R^2 = {r2:.4f}")

    fig, ax = plt.subplots(figsize=(3.5, 2.5))
    for fam in ("sparse", "dense"):
        s = family == fam
        ax.plot(m[s], ms[s], "o", color=COLORS[fam], markersize=3.5, label=f"{fam}, measured")
        ax.plot(m[s], model[s], "--", color=COLORS[fam], linewidth=1, label=f"{fam}, model")
    ax.set_xlabel("Number of tests $m$ (= number of requirements $n$)")
    ax.set_ylabel("Median running time (ms)")
    ax.grid(color="0.9")
    ax.legend(fontsize=6.5, frameon=False)
    save(fig, "runtime.png")

    fig, ax = plt.subplots(figsize=(3.5, 2.3))
    for fam in ("sparse", "dense"):
        s = family == fam
        ax.plot(m[s], ms[s] / model[s], "o-", color=COLORS[fam], markersize=3.5, linewidth=1, label=fam)
    ax.axhline(1.0, color="0.4", linestyle=":", linewidth=1)
    ax.set_xlabel("Number of tests $m$ (= number of requirements $n$)")
    ax.set_ylabel("Measured / model")
    ax.set_ylim(0, 2)
    ax.grid(color="0.9")
    ax.legend(fontsize=6.5, frameon=False)
    save(fig, "ratio.png")


def quality_figure():
    rows = read("quality.csv")
    ratios = {}
    for r in rows:
        ratios.setdefault((float(r["density"]), int(r["m"])), []).append(float(r["ratio"]))

    fig, ax = plt.subplots(figsize=(3.5, 2.5))
    for p in sorted({p for p, _ in ratios}):
        m = sorted(mm for pp, mm in ratios if pp == p)
        ax.plot(m, [np.mean(ratios[(p, x)]) for x in m], "o-", color=COLORS[p],
                markersize=3.5, linewidth=1, label=f"$p={p:g}$, mean")
        ax.plot(m, [np.max(ratios[(p, x)]) for x in m], "^--", color=COLORS[p],
                markersize=3, linewidth=0.8, alpha=0.7, label=f"$p={p:g}$, worst")
    ax.axhline(1.0, color="0.4", linestyle=":", linewidth=1)
    ax.set_xlabel("Number of tests $m$ ($n = 60$ requirements)")
    ax.set_ylabel("Greedy size / optimal size")
    ax.grid(color="0.9")
    ax.legend(fontsize=6, frameon=False, ncol=2, loc="upper left")
    save(fig, "quality.png")


if __name__ == "__main__":
    FIGURES.mkdir(exist_ok=True)
    runtime_figures()
    quality_figure()
    print("Saved figures/runtime.png, figures/ratio.png, figures/quality.png")
