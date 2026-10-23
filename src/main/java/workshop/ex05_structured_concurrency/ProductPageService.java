package workshop.ex05_structured_concurrency;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/// # Exercise 05 / Task 2: Fork, join, fail together
/// *(Java 27: structured concurrency, 7th preview)*
///
/// ## Problem
/// The product page calls three backends one after another. Each takes about
/// 300 ms, so the page takes 900 ms, although the calls do not depend on each
/// other.
///
/// Running them with an `ExecutorService` and `Future`s is easy to get wrong:
/// when one call fails, the others keep running and waste resources; when the
/// request is cancelled, nobody cancels the calls; and a thread dump shows
/// three unrelated threads.
///
/// In a `StructuredTaskScope`, subtasks live within a block, like local
/// variables. When the block is left, they are done, one way or another.
///
/// ## Task
/// Rewrite `load()` so that the three backends are called concurrently:
///
/// - The page is ready after the slowest call, not after the sum of all.
/// - If a call fails, `load()` fails at once with an `ExecutionException`
///   whose cause is the original exception, and the calls still running are
///   cancelled (interrupted).
/// - The backends still get the tenant (see task 1).
///
/// ## Constraints
/// - Use `StructuredTaskScope`. No `ExecutorService`, `CompletableFuture` or
///   `Thread`.
///
/// ## Hints
/// - `try (var scope = StructuredTaskScope.open()) { ... }`
/// - `scope.fork(() -> ...)` returns a `Subtask`. Call `get()` on it only after
///   `scope.join()`.
/// - Without arguments, `open()` waits for all subtasks to succeed and cancels
///   the scope as soon as one fails. Other policies are available as
///   `StructuredTaskScope.Joiner`s.
///
/// ## Verify
/// `make test ex=05`
public final class ProductPageService {

    private final Backends.Prices prices;
    private final Backends.Stock stock;
    private final Backends.Reviews reviews;

    public ProductPageService(Backends.Prices prices, Backends.Stock stock, Backends.Reviews reviews) {
        this.prices = prices;
        this.stock = stock;
        this.reviews = reviews;
    }

    public ProductPage load(String sku) throws ExecutionException, InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            var price = scope.fork(() -> prices.priceOf(sku));
            var available = scope.fork(() -> stock.available(sku));
            var rating = scope.fork(() -> reviews.averageRating(sku));
            scope.join();
            return new ProductPage(sku, RequestContext.tenant(), price.get(), available.get(), rating.get());
        }
    }
}
