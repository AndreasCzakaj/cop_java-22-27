package workshop.ex05_structured_concurrency;

import java.time.Duration;
import java.util.List;

/// # Exercise 05 / Task 3: The first answer wins
/// *(Java 27: structured concurrency, 7th preview)*
///
/// ## Problem
/// At checkout we show one shipping quote, from whichever carrier answers
/// first. The current code asks the carriers one after another, so a single
/// slow carrier delays every checkout, and a hanging one blocks it forever.
///
/// ## Task
/// Rewrite `firstQuote()`:
///
/// - Ask all carriers at the same time and return the first successful
///   quote. Cancel the others as soon as it is there.
/// - Carriers that fail are ignored, as long as one succeeds.
/// - If all carriers fail, or none answers within `timeout`, throw a
///   `NoQuoteException`.
///
/// ## Constraints
/// - Use `StructuredTaskScope` with a suitable `Joiner`, and its timeout
///   configuration. No `ExecutorService`, `CompletableFuture`, `Thread` or
///   `ScheduledExecutorService`.
///
/// ## Hints
/// - `StructuredTaskScope.open(joiner, config -> config.withTimeout(...))`
/// - Look at the factory methods of `StructuredTaskScope.Joiner`. Some take a
///   function that turns the failure into the exception that `join()` throws.
///
/// ## Verify
/// `make test ex=05`
public final class ShippingQuotes {

    private final List<Carrier> carriers;
    private final Duration timeout;

    public ShippingQuotes(List<Carrier> carriers, Duration timeout) {
        this.carriers = carriers;
        this.timeout = timeout;
    }

    public Quote firstQuote(Parcel parcel) throws NoQuoteException, InterruptedException {
        RuntimeException lastFailure = null;
        for (var carrier : carriers) {
            try {
                return carrier.quote(parcel);
            } catch (RuntimeException e) {
                lastFailure = e;
            }
        }
        throw new NoQuoteException(lastFailure);
    }
}
