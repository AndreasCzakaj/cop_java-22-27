package workshop.ex02_recursive_comparison;

import java.math.BigDecimal;

public record OrderLine(String sku, String name, int quantity, BigDecimal unitPrice, BigDecimal lineTotal, String warehouse) {
}
