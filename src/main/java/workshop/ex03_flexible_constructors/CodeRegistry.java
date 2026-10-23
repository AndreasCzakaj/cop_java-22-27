package workshop.ex03_flexible_constructors;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/// Given code: do not change.
public final class CodeRegistry {

    private static final List<ProductCode> CODES = new CopyOnWriteArrayList<>();

    private CodeRegistry() {
    }

    static void register(ProductCode code) {
        CODES.add(code);
    }

    public static List<String> values() {
        return CODES.stream().map(ProductCode::value).toList();
    }

    public static void clear() {
        CODES.clear();
    }
}
