package workshop.ex05_structured_concurrency;

import java.math.BigDecimal;

/// Given code: do not change.
///
/// The remote services behind the product page. Each call takes a few hundred
/// milliseconds and needs the tenant of the request.
public interface Backends {

    @FunctionalInterface
    interface Prices {
        BigDecimal priceOf(String sku) throws InterruptedException;
    }

    @FunctionalInterface
    interface Stock {
        int available(String sku) throws InterruptedException;
    }

    @FunctionalInterface
    interface Reviews {
        double averageRating(String sku) throws InterruptedException;
    }
}
