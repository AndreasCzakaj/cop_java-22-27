package workshop.ex03_flexible_constructors;

import java.util.Objects;

/// Given code: do not change.
///
/// Every product code registers itself, so that the search index can find it.
public abstract class ProductCode {

    private final String value;

    protected ProductCode(String value) {
        this.value = Objects.requireNonNull(value);
        CodeRegistry.register(this);
    }

    public final String value() {
        return value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + value + "]";
    }
}
