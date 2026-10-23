package workshop.ex05_structured_concurrency;

/// Given code: do not change.
public final class NoQuoteException extends Exception {

    public NoQuoteException(Throwable cause) {
        super("No carrier sent a quote", cause);
    }
}
