package workshop.ex02_recursive_comparison;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Order(
        UUID id,
        String customerEmail,
        List<OrderLine> lines,
        Address billingAddress,
        Address shippingAddress,
        BigDecimal total,
        Instant createdAt) {
}
