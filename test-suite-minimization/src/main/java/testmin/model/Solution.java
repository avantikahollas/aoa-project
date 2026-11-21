package testmin.model;

import java.util.List;

/**
 * A selection of tests, identified by test id, in the order they were chosen.
 */
public record Solution(List<Integer> selectedTestIds) {

    public Solution {
        selectedTestIds = List.copyOf(selectedTestIds);
    }

    /** Number of selected tests: the quantity being minimized. */
    public int size() {
        return selectedTestIds.size();
    }
}
