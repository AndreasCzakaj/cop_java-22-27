package workshop.ex05_structured_concurrency;

import java.math.BigDecimal;

/// Given code: do not change.
public record ProductPage(String sku, String tenant, BigDecimal price, int stock, double rating) {
}
