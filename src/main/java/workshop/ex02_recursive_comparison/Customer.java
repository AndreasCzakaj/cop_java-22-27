package workshop.ex02_recursive_comparison;

/// @param shippingAddress `null` if the goods go to the billing address
public record Customer(String email, String name, Address billingAddress, Address shippingAddress) {
}
