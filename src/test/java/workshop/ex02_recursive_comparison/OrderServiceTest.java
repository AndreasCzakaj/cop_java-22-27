package workshop.ex02_recursive_comparison;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/// # Exercise 02: AssertJ - comparing whole object graphs
/// *(AssertJ `usingRecursiveComparison()`, not tied to a Java version)*
///
/// ## Problem
/// `OrderService` and `OrderMapper` are in production. Their only test is
/// `legacyTest()` below: it is green, it checks a handful of fields one by
/// one, and every new field in `Order` or `OrderDto` is another line that
/// somebody forgets to add. Rumour has it that customers have complained
/// about parcels going to the wrong address.
///
/// Comparing whole objects with `isEqualTo()` does not work either: the IDs
/// are random, the timestamps are "now", `BigDecimal("99.8")` does not equal
/// `BigDecimal("99.80")`, and the order of the lines is not defined.
///
/// AssertJ's recursive comparison compares objects field by field, all the
/// way down, and lets you say which differences do not matter.
///
/// ## Task
/// This time the production code is finished and **you write the tests**.
/// Replace the three `fail("TODO")` tests below. Each one has its own
/// instructions. Every test must compare whole objects with
/// `usingRecursiveComparison()`.
///
/// One of your tests will find a bug. Fix it in the production code (it is a
/// one-liner), then all tests are green.
///
/// ## Constraints
/// - Do not change the production code except for the bug fix.
/// - No `ignoringFields(...)` for fields that can be checked in a meaningful
///   way.
///
/// ## Hints
/// - `assertThat(actual).usingRecursiveComparison()` then, e.g.:
///   `.ignoringFields("id")`, `.comparingOnlyFields(...)`,
///   `.ignoringCollectionOrder()`,
///   `.withComparatorForType(BigDecimal::compareTo, BigDecimal.class)`,
///   `.withEqualsForFields((Instant a, Instant b) -> ..., "createdAt")`,
///   and finally `.isEqualTo(expected)`.
/// - Actual and expected may have **different types**: fields are matched by
///   name, so an `OrderDto` can be compared with an `Order`, and an
///   `AddressDto` with an `Address`.
/// - For collections: `assertThat(list).usingRecursiveFieldByFieldElementComparator(configuration)`
///   with `RecursiveComparisonConfiguration.builder()...build()`.
/// - Read the failure messages: they list every difference at once.
///
/// ## Verify
/// `make test ex=02`. Done when your tests are complete and green, and the bug
/// is fixed.
class OrderServiceTest {

    static final Address ADA_HOME = new Address("Ada Lovelace", "Unter den Linden 1", "10117", "Berlin", "DE");
    static final Address ADA_OFFICE = new Address("Ada Lovelace", "10 Downing St", "SW1A 2AA", "London", "GB");
    static final Customer ADA = new Customer("ada@example.com", "Ada Lovelace", ADA_HOME, ADA_OFFICE);

    static final Product KEYBOARD = new Product("KB-01", "Keyboard", new BigDecimal("49.9"), "Hamburg");
    static final Product MOUSE = new Product("MS-02", "Mouse", new BigDecimal("19.90"), "Hamburg");
    static final Product MONITOR = new Product("MN-27", "Monitor 27\"", new BigDecimal("249"), "Munich");

    final OrderService service = new OrderService(new Catalog(KEYBOARD, MOUSE, MONITOR));
    final OrderMapper mapper = new OrderMapper();

    /// The test that has been in place for years. Leave it as it is.
    @Test
    void legacyTest() {
        var order = service.placeOrder(ADA, Map.of("KB-01", 2, "MN-27", 1));
        var dto = mapper.toDto(order);

        assertThat(dto.id()).isEqualTo(order.id().toString());
        assertThat(dto.customerEmail()).isEqualTo("ada@example.com");
        assertThat(dto.lines()).hasSize(2);
        assertThat(dto.total()).isEqualTo("348.80");
        assertThat(dto.billingAddress().city()).isEqualTo("Berlin");
    }

    /// ## Task 1: placeOrder() creates the expected order
    /// Place an order for Ada: 2 keyboards and 1 monitor. Build the complete
    /// `Order` you expect, with the amounts written as they are printed on the
    /// invoice (`"99.80"`, `"249.00"`, ...), and compare it with the actual one.
    ///
    /// - The ID is random: ignore it.
    /// - `createdAt` must not be ignored: it must be at most one second away
    ///   from the time of your expected order.
    /// - Amounts are equal if they have the same value, whatever their scale.
    /// - The order of the lines does not matter. (Run the test a few times
    ///   without handling this. `Map.of()` has no defined iteration order.)
    @Test
    void placeOrderCreatesTheExpectedOrder() {
        var order = service.placeOrder(ADA, Map.of("KB-01", 2, "MN-27", 1));

        var expected = new Order(null, "ada@example.com",
                List.of(
                        new OrderLine("KB-01", "Keyboard", 2, new BigDecimal("49.90"), new BigDecimal("99.80"), "Hamburg"),
                        new OrderLine("MN-27", "Monitor 27\"", 1, new BigDecimal("249.00"), new BigDecimal("249.00"), "Munich")),
                ADA_HOME, ADA_OFFICE, new BigDecimal("348.80"), Instant.now());

        assertThat(order)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .withEqualsForFields((Instant a, Instant b) -> atMostOneSecondApart(a, b), "createdAt")
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    /// ## Task 2: the DTO contains the data of the order
    /// Map an order to its DTO and compare the DTO **with the order itself**,
    /// not with a hand-written DTO. All fields whose name and meaning are the
    /// same in both types are compared recursively, in one assertion.
    /// Check the remaining fields (`id`, `total`, `createdAt`) with ordinary
    /// assertions.
    @Test
    void theDtoContainsTheDataOfTheOrder() {
        var order = service.placeOrder(ADA, Map.of("KB-01", 2, "MN-27", 1));

        var dto = mapper.toDto(order);

        assertThat(dto)
                .usingRecursiveComparison()
                .ignoringFields("id", "total", "createdAt")
                .isEqualTo(order);
        assertThat(dto.id()).isEqualTo(order.id().toString());
        assertThat(dto.total()).isEqualTo("348.80");
        assertThat(Instant.parse(dto.createdAt())).isEqualTo(order.createdAt());
    }

    /// ## Task 3: splitByWarehouse() creates one order per warehouse
    /// Ada orders 2 keyboards, 1 mouse and 1 monitor. The keyboards and the
    /// mouse are shipped from Hamburg, the monitor from Munich. Split the
    /// order and compare the result with the two orders you expect, in one
    /// assertion on the list.
    ///
    /// - The two orders may come in any order.
    /// - Each has a new random ID; `createdAt` is the one of the original order.
    /// - Same rules for amounts and lines as in task 1.
    @Test
    void splitByWarehouseCreatesOneOrderPerWarehouse() {
        var order = service.placeOrder(ADA, Map.of("KB-01", 2, "MS-02", 1, "MN-27", 1));

        var split = service.splitByWarehouse(order);

        var fromHamburg = new Order(null, "ada@example.com",
                List.of(
                        new OrderLine("KB-01", "Keyboard", 2, new BigDecimal("49.90"), new BigDecimal("99.80"), "Hamburg"),
                        new OrderLine("MS-02", "Mouse", 1, new BigDecimal("19.90"), new BigDecimal("19.90"), "Hamburg")),
                ADA_HOME, ADA_OFFICE, new BigDecimal("119.70"), order.createdAt());
        var fromMunich = new Order(null, "ada@example.com",
                List.of(new OrderLine("MN-27", "Monitor 27\"", 1, new BigDecimal("249.00"), new BigDecimal("249.00"), "Munich")),
                ADA_HOME, ADA_OFFICE, new BigDecimal("249.00"), order.createdAt());

        var configuration = RecursiveComparisonConfiguration.builder()
                .withIgnoredFields("id")
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .withIgnoreCollectionOrder(true)
                .build();

        assertThat(split)
                .usingRecursiveFieldByFieldElementComparator(configuration)
                .containsExactlyInAnyOrder(fromHamburg, fromMunich);
        assertThat(split).extracting(Order::id).doesNotContainNull().doesNotHaveDuplicates().doesNotContain(order.id());
    }

    private static boolean atMostOneSecondApart(Instant a, Instant b) {
        return Duration.between(a, b).abs().compareTo(Duration.ofSeconds(1)) <= 0;
    }
}
