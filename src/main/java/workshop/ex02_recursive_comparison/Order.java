package workshop.ex02_recursive_comparison;

import java.util.List;
import java.util.UUID;

/// @param total in cents
public record Order(
        UUID id,
        String customerEmail,
        List<OrderLine> lines,
        Address billingAddress,
        Address shippingAddress,
        int total) {
}
