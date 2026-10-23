package workshop.ex02_recursive_comparison;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class OrderService {

    private final Catalog catalog;

    public OrderService(Catalog catalog) {
        this.catalog = catalog;
    }

    /// @param cart quantity per SKU
    public Order placeOrder(Customer customer, Map<String, Integer> cart) {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("The cart is empty.");
        }

        var lines = cart.entrySet().stream()
                .map(item -> toLine(catalog.get(item.getKey()), item.getValue()))
                .toList();

        var shippingAddress = customer.shippingAddress() != null ? customer.shippingAddress() : customer.billingAddress();

        return new Order(UUID.randomUUID(), customer.email(), lines, customer.billingAddress(), shippingAddress,
                totalOf(lines), Instant.now());
    }

    /// Splits an order into one order per warehouse, so that each warehouse ships its part on its own.
    public List<Order> splitByWarehouse(Order order) {
        return order.lines().stream()
                .collect(Collectors.groupingBy(OrderLine::warehouse))
                .values().stream()
                .map(lines -> new Order(UUID.randomUUID(), order.customerEmail(), lines, order.billingAddress(),
                        order.shippingAddress(), totalOf(lines), order.createdAt()))
                .toList();
    }

    private static OrderLine toLine(Product product, int quantity) {
        return new OrderLine(product.sku(), product.name(), quantity, product.unitPrice(),
                product.unitPrice().multiply(BigDecimal.valueOf(quantity)), product.warehouse());
    }

    private static BigDecimal totalOf(List<OrderLine> lines) {
        return lines.stream().map(OrderLine::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
