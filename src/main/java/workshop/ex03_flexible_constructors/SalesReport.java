package workshop.ex03_flexible_constructors;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

/// # Exercise 03 / Task 2: Initialize fields before `super(...)`
/// *(Java 25: flexible constructor bodies)*
///
/// ## Problem
/// `Report` calls the overridable method `title()` from its constructor, a
/// well-known trap: at that point the fields of `SalesReport` are not
/// assigned yet. The report is titled "Sales report null null".
///
/// Before Java 25 there was no clean way out: the fields could only be
/// assigned after `super()` had returned.
///
/// ## Task
/// Fix the title, so that the report reads
///
/// ```
/// Sales report EMEA 2026-09
/// =========================
/// Keyboard: 1299.00
/// ```
///
/// A blank region is rejected with an `IllegalArgumentException` before
/// `Report` is constructed.
///
/// ## Constraints
/// - `Report` must not change.
/// - `region` and `month` stay `final`; `title()` keeps using them.
///
/// ## Hints
/// - In the prologue (before `super(...)`), a constructor must not read
///   fields, but it may **assign** fields of its own class that have no
///   initializer.
///
/// ## Verify
/// `make test ex=03`
public final class SalesReport extends Report {

    private final String region;
    private final YearMonth month;

    public SalesReport(String region, YearMonth month, Map<String, BigDecimal> revenueByProduct) {
        super();
        this.region = region;
        this.month = month;
        revenueByProduct.forEach((product, revenue) -> addLine(product + ": " + revenue));
    }

    @Override
    protected String title() {
        return "Sales report " + region + " " + month;
    }
}
