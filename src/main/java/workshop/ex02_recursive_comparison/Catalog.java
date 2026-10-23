package workshop.ex02_recursive_comparison;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Catalog {

    private final Map<String, Product> products;

    public Catalog(Product... products) {
        this.products = Stream.of(products).collect(Collectors.toMap(Product::sku, Function.identity()));
    }

    public Product get(String sku) {
        var product = products.get(sku);
        if (product == null) {
            throw new NoSuchElementException("Unknown product: " + sku);
        }
        return product;
    }
}
