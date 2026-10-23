package workshop.ex02_recursive_comparison;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                totalOf(lines));
    }

    private static OrderLine toLine(Product product, int quantity) {
        return new OrderLine(product.sku(), product.name(), quantity, product.unitPrice(), product.unitPrice() * quantity);
    }

    private static int totalOf(List<OrderLine> lines) {
        return lines.stream().mapToInt(OrderLine::lineTotal).sum();
    }
}
