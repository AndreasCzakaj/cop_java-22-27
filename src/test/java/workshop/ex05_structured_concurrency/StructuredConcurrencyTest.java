package workshop.ex05_structured_concurrency;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import workshop.support.Source;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.catchThrowable;

@Timeout(10)
class StructuredConcurrencyTest {

    static final String FORBIDDEN = "\\b(ExecutorService|Executors|CompletableFuture|Thread)\\b";

    @Nested
    class Task1RequestContext {

        @Test
        @Disabled("implement the production code first")
        void bindsTheTenantWhileTheActionRuns() throws Exception {
            assertThat(RequestContext.runAs("acme", RequestContext::tenant)).isEqualTo("acme");
        }

        @Test
        @Disabled("implement the production code first")
        void bindsAnotherTenantForANestedAction() throws Exception {
            var tenants = RequestContext.runAs("acme", () -> List.of(
                    RequestContext.tenant(),
                    RequestContext.runAs("globex", RequestContext::tenant),
                    RequestContext.tenant()));

            assertThat(tenants).containsExactly("acme", "globex", "acme");
        }

        @Test
        @Disabled("implement the production code first")
        void hasNoTenantOutside() throws Exception {
            RequestContext.runAs("acme", RequestContext::tenant);

            assertThatIllegalStateException().isThrownBy(RequestContext::tenant).withMessage("No tenant bound");
        }

        @Test
        @Disabled("implement the production code first")
        void passesTheTenantOnToSubtasks() throws Exception {
            var tenant = RequestContext.runAs("acme", () -> {
                try (var scope = StructuredTaskScope.open()) {
                    var subtask = scope.<String>fork(RequestContext::tenant);
                    scope.join();
                    return subtask.get();
                }
            });

            assertThat(tenant).isEqualTo("acme");
        }

        @Test
        @Disabled("implement the production code first")
        void usesAScopedValue() {
            var code = Source.ofClass(RequestContext.class);

            assertThat(code.contains("\\bThreadLocal\\b")).as("ThreadLocal").isFalse();
            assertThat(code.contains("\\bScopedValue<String>")).as("ScopedValue<String>").isTrue();
        }
    }

    @Nested
    class Task2ProductPageService {

        final Backend<BigDecimal> prices = new Backend<>(300, () -> new BigDecimal("49.90"));
        final Backend<Integer> stock = new Backend<>(300, () -> 12);
        final Backend<Double> reviews = new Backend<>(300, () -> 4.5);

        ProductPageService service() {
            return new ProductPageService(prices::call, stock::call, reviews::call);
        }

        @Test
        @Disabled("implement the production code first")
        void loadsThePage() throws Exception {
            var page = RequestContext.runAs("acme", () -> service().load("KB-01"));

            assertThat(page).isEqualTo(new ProductPage("KB-01", "acme", new BigDecimal("49.90"), 12, 4.5));
            assertThat(List.of(prices.tenant, stock.tenant, reviews.tenant)).containsOnly("acme");
        }

        @Test
        @Disabled("implement the production code first")
        void callsTheBackendsConcurrently() throws Exception {
            var start = System.nanoTime();
            RequestContext.runAs("acme", () -> service().load("KB-01"));

            assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofMillis(800));
        }

        @Test
        @Disabled("implement the production code first")
        void failsAtOnceAndCancelsTheOtherCalls() throws Exception {
            var failure = new IllegalStateException("Stock service is down");
            var failingStock = new Backend<Integer>(50, () -> { throw failure; });
            var slowReviews = new Backend<>(5_000, () -> 4.5);
            var service = new ProductPageService(prices::call, failingStock::call, slowReviews::call);

            var start = System.nanoTime();
            var thrown = catchThrowable(() -> RequestContext.runAs("acme", () -> service.load("KB-01")));

            assertThat(thrown).isInstanceOf(ExecutionException.class).cause().isSameAs(failure);
            assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofMillis(1_000));
            assertThat(slowReviews.interrupted.await(1, TimeUnit.SECONDS)).as("the slow call was cancelled").isTrue();
        }

        @Test
        @Disabled("implement the production code first")
        void usesAStructuredTaskScope() {
            var code = Source.ofClass(ProductPageService.class);

            assertThat(code.contains("\\bStructuredTaskScope\\b")).as("StructuredTaskScope").isTrue();
            assertThat(code.contains(FORBIDDEN)).as("executors, futures or threads").isFalse();
        }
    }

    @Nested
    class Task3ShippingQuotes {

        static final Parcel PARCEL = new Parcel(2.5, "DE");

        static Quote quoteBy(String carrier) {
            return new Quote(carrier, new BigDecimal("4.99"), 2);
        }

        @Test
        @Disabled("implement the production code first")
        void returnsTheFirstQuote() throws Exception {
            var slow = new Backend<>(5_000, () -> quoteBy("Snail Mail"));
            var fast = new Backend<>(200, () -> quoteBy("Speedy"));
            var down = new Backend<Quote>(10, () -> { throw new IllegalStateException("503 Service Unavailable"); });
            var quotes = new ShippingQuotes(List.of(slow::call, down::call, fast::call), Duration.ofSeconds(3));

            var start = System.nanoTime();
            var quote = quotes.firstQuote(PARCEL);

            assertThat(quote).isEqualTo(quoteBy("Speedy"));
            assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofMillis(1_000));
            assertThat(slow.interrupted.await(1, TimeUnit.SECONDS)).as("the slow carrier was cancelled").isTrue();
        }

        @Test
        @Disabled("implement the production code first")
        void failsIfAllCarriersFail() {
            var down = new Backend<Quote>(10, () -> { throw new IllegalStateException("503 Service Unavailable"); });
            var broken = new Backend<Quote>(50, () -> { throw new IllegalArgumentException("Unknown country"); });
            var quotes = new ShippingQuotes(List.of(down::call, broken::call), Duration.ofSeconds(3));

            assertThat(catchThrowable(() -> quotes.firstQuote(PARCEL))).isInstanceOf(NoQuoteException.class);
        }

        @Test
        @Disabled("implement the production code first")
        void givesUpAfterTheTimeout() {
            var slow = new Backend<>(5_000, () -> quoteBy("Snail Mail"));
            var slower = new Backend<>(8_000, () -> quoteBy("Tortoise Express"));
            var quotes = new ShippingQuotes(List.of(slow::call, slower::call), Duration.ofMillis(300));

            var start = System.nanoTime();
            var thrown = catchThrowable(() -> quotes.firstQuote(PARCEL));

            assertThat(thrown).isInstanceOf(NoQuoteException.class);
            assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofMillis(1_500));
        }

        @Test
        @Disabled("implement the production code first")
        void usesAStructuredTaskScopeWithAJoiner() {
            var code = Source.ofClass(ShippingQuotes.class);

            assertThat(code.contains("\\bJoiner\\b")).as("a Joiner").isTrue();
            assertThat(code.contains("\\bwithTimeout\\b")).as("withTimeout").isTrue();
            assertThat(code.contains(FORBIDDEN)).as("executors, futures or threads").isFalse();
        }
    }

    /// A fake remote service: it waits, remembers the tenant, notices when it is cancelled.
    static final class Backend<T> {

        interface Answer<T> {
            T get();
        }

        final long millis;
        final Answer<T> answer;
        final CountDownLatch interrupted = new CountDownLatch(1);
        volatile String tenant;

        Backend(long millis, Answer<T> answer) {
            this.millis = millis;
            this.answer = answer;
        }

        T call(Object ignoredRequest) throws InterruptedException {
            try {
                tenant = RequestContext.tenant();
            } catch (IllegalStateException _) {
                tenant = "<no tenant bound>";
            }
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                interrupted.countDown();
                throw e;
            }
            return answer.get();
        }
    }
}
