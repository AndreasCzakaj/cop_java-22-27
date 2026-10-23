package workshop.ex02_recursive_comparison;

import java.math.RoundingMode;

public final class OrderMapper {

    public OrderDto toDto(Order order) {
        return new OrderDto(
                order.id().toString(),
                order.customerEmail(),
                order.lines().stream().map(OrderMapper::toDto).toList(),
                toDto(order.billingAddress()),
                toDto(order.billingAddress()),
                order.total().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                order.createdAt().toString());
    }

    private static OrderLineDto toDto(OrderLine line) {
        return new OrderLineDto(line.sku(), line.name(), line.quantity(), line.unitPrice(), line.lineTotal());
    }

    private static AddressDto toDto(Address address) {
        return new AddressDto(address.name(), address.street(), address.zip(), address.city(), address.country());
    }
}
