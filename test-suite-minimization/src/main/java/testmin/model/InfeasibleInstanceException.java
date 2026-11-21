package testmin.model;

/** Thrown when some requirement is not covered by any test, so no solution exists. */
public class InfeasibleInstanceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InfeasibleInstanceException(String message) {
        super(message);
    }
}
