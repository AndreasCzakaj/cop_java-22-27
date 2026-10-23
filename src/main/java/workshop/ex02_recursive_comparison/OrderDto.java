package workshop.ex02_recursive_comparison;

import java.util.List;

/// What the REST API returns for an order.
public record OrderDto(
        String id,
        String customerEmail,
        List<OrderLineDto> lines,
        AddressDto billingAddress,
        AddressDto shippingAddress,
        String total,
        String createdAt) {
}
