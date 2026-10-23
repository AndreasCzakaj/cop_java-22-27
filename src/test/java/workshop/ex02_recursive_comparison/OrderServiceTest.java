package workshop.ex02_recursive_comparison;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/// # Exercise 02: AssertJ - comparing whole objects
/// *(AssertJ `usingRecursiveComparison()`, not tied to a Java version)*
///
/// ## Problem
/// `OrderService.placeOrder()` is in production. Its only test is
/// `legacyTest()` below: it is green, it checks a handful of fields one by
/// one, and every new field in `Order` is another line that somebody forgets
/// to add. Rumour has it that customers have complained about parcels going
/// to the wrong address.
///
/// Comparing whole objects with `isEqualTo()` does not work either: the ID is
/// random, and the order of the lines is not defined.
///
/// AssertJ's recursive comparison compares objects field by field, all the
/// way down, and lets you say which fields to ignore or to compare.
///
/// ## Task
/// This time the production code is finished and **you write the tests**.
/// Replace the three `fail("TODO")` tests below. Each one has its own
/// instructions. Every test compares a whole `Order` with
/// `usingRecursiveComparison()`.
///
/// One of your tests will find a bug. Fix it in `OrderService` (it is a
/// one-liner), then all tests are green.
///
/// ## Constraints
/// - Do not change the production code except for the bug fix.
///
/// ## Hints
/// - `assertThat(actual).usingRecursiveComparison()` then, e.g.:
///   `.ignoringFields("id")`, `.comparingOnlyFields("customerEmail")`,
///   `.ignoringCollectionOrder()`, and finally `.isEqualTo(expected)`.
/// - Fields you ignore can be `null` in the expected order.
/// - Read the failure messages: they list every difference at once.
///
/// ## Verify
/// `make test ex=02`. Done when your tests are complete and green, and the bug
/// is fixed.
class OrderServiceTest {

    static final Address ADA_HOME = new Address("Ada Lovelace", "Unter den Linden 1", "10117", "Berlin", "DE");
    static final Address ADA_OFFICE = new Address("Ada Lovelace", "10 Downing St", "SW1A 2AA", "London", "GB");
    static final Customer ADA = new Customer("ada@example.com", "Ada Lovelace", ADA_HOME, ADA_OFFICE);

    static final Product KEYBOARD = new Product("KB-01", "Keyboard", 4990);
    static final Product MOUSE = new Product("MS-02", "Mouse", 1990);
    static final Product MONITOR = new Product("MN-27", "Monitor 27\"", 24900);

    final OrderService service = new OrderService(new Catalog(KEYBOARD, MOUSE, MONITOR));

    /// The test that has been in place for years. Leave it as it is.
    @Test
    @Disabled("remove @Disabled to see the legacy test pass")
    void legacyTest() {
        var order = service.placeOrder(ADA, Map.of("KB-01", 2, "MN-27", 1));

        assertThat(order.id()).isNotNull();
        assertThat(order.customerEmail()).isEqualTo("ada@example.com");
        assertThat(order.lines()).hasSize(2);
        assertThat(order.total()).isEqualTo(34880);
        assertThat(order.billingAddress().city()).isEqualTo("Berlin");
    }

    /// ## Task 1: the order has the expected customer data and total
    /// Place an order for Ada: 2 keyboards and 1 monitor. Build the `Order`
    /// you expect and compare it with the actual one.
    ///
    /// - The ID is random: ignore it.
    /// - Ignore the lines as well, task 3 takes care of them.
    /// - Everything else is compared.
    @Test
    @Disabled("remove @Disabled to write this test")
    void placeOrderCreatesTheExpectedOrder() {
        fail("TODO");
    }

    /// ## Task 2: compare only what matters here
    /// Sometimes a test is only about a few fields. Place the same order as in
    /// task 1 and compare it with an expected order, but **only** the fields
    /// `customerEmail` and `total`. All other fields are not compared.
    @Test
    @Disabled("remove @Disabled to write this test")
    void placeOrderChargesTheCustomer() {
        fail("TODO");
    }

    /// ## Task 3: the order also contains the expected lines
    /// Like task 1, but this time the lines are compared as well: only the ID
    /// is ignored.
    ///
    /// - The order of the lines does not matter. (Run the test a few times
    ///   without handling this. `Map.of()` has no defined iteration order.)
    @Test
    @Disabled("remove @Disabled to write this test")
    void placeOrderCreatesTheExpectedLines() {
        fail("TODO");
    }
}
