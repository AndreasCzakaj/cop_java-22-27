package workshop.ex02_recursive_comparison;

/// @param unitPrice in cents
/// @param lineTotal in cents
public record OrderLine(String sku, String name, int quantity, int unitPrice, int lineTotal) {
}
