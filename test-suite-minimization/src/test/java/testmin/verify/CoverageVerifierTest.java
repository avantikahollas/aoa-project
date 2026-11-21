package testmin.verify;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import testmin.model.Instance;
import testmin.model.Solution;
import testmin.model.TestCase;

class CoverageVerifierTest {

    private final Instance inst = new Instance(3, List.of(
            new TestCase(0, new int[] {0, 1}),
            new TestCase(1, new int[] {2}),
            new TestCase(2, new int[] {1})));

    @Test
    void acceptsValidCover() {
        assertTrue(CoverageVerifier.coversAll(inst, new Solution(List.of(0, 1))));
        assertTrue(CoverageVerifier.isValid(inst, new Solution(List.of(0, 1)), 2));
    }

    @Test
    void rejectsIncompleteCover() {
        assertFalse(CoverageVerifier.coversAll(inst, new Solution(List.of(0, 2))));
    }

    @Test
    void rejectsOverBudget() {
        assertFalse(CoverageVerifier.isValid(inst, new Solution(List.of(0, 1)), 1));
    }

    @Test
    void rejectsInvalidOrDuplicateIds() {
        assertFalse(CoverageVerifier.coversAll(inst, new Solution(List.of(0, 1, 7))));
        assertFalse(CoverageVerifier.coversAll(inst, new Solution(List.of(0, 1, 1))));
    }
}
