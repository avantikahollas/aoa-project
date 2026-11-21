package testmin.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class InstanceTest {

    @Test
    void testCaseSortsAndRemovesDuplicates() {
        TestCase t = new TestCase(0, new int[] {3, 1, 3, 2, 1});
        assertArrayEquals(new int[] {1, 2, 3}, t.covers());
        assertEquals(3, t.size());
    }

    @Test
    void testCaseIsDefensivelyCopied() {
        int[] input = {0, 1};
        TestCase t = new TestCase(0, input);
        input[0] = 99;
        t.covers()[1] = 99;
        assertArrayEquals(new int[] {0, 1}, t.covers());
    }

    @Test
    void rejectsNegativeRequirement() {
        assertThrows(IllegalArgumentException.class, () -> new TestCase(0, new int[] {-1}));
    }

    @Test
    void rejectsOutOfRangeRequirement() {
        List<TestCase> tests = List.of(new TestCase(0, new int[] {0, 5}));
        assertThrows(IllegalArgumentException.class, () -> new Instance(3, tests));
    }

    @Test
    void rejectsIdIndexMismatch() {
        List<TestCase> tests = List.of(new TestCase(1, new int[] {0}));
        assertThrows(IllegalArgumentException.class, () -> new Instance(1, tests));
    }

    @Test
    void computesTotalSizeAndFeasibility() {
        Instance feasible = new Instance(3, List.of(
                new TestCase(0, new int[] {0, 1}),
                new TestCase(1, new int[] {1, 2})));
        assertEquals(4, feasible.totalCoverageSize());
        assertTrue(feasible.isFeasible());

        Instance infeasible = new Instance(3, List.of(new TestCase(0, new int[] {0})));
        assertFalse(infeasible.isFeasible());
    }
}
