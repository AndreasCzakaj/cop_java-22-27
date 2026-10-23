package workshop.ex06_gatherers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import workshop.support.Source;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Timeout(5)
class GatherersTest {

    @Nested
    class Task1Revenue {

        @Test
        @Disabled("implement the production code first")
        void computesTheMovingAverage() {
            assertThat(Revenue.movingAverage(Stream.of(10, 20, 60, 30, 40), 3)).containsExactly(30.0, 110 / 3.0, 130 / 3.0);
            assertThat(Revenue.movingAverage(Stream.of(10, 20, 60), 2)).containsExactly(15.0, 40.0);
            assertThat(Revenue.movingAverage(Stream.of(10, 20), 3)).isEmpty();
        }

        @Test
        @Disabled("implement the production code first")
        void computesTheRunningTotal() {
            assertThat(Revenue.runningTotal(Stream.of(10, 20, 60, -5))).containsExactly(10, 30, 90, 85);
            assertThat(Revenue.runningTotal(Stream.empty())).isEmpty();
        }

        @Test
        @Disabled("implement the production code first")
        void makesBatches() {
            var ids = IntStream.rangeClosed(1, 7).mapToObj(i -> "A-" + i);

            assertThat(Revenue.batches(ids, 3)).containsExactly(
                    List.of("A-1", "A-2", "A-3"), List.of("A-4", "A-5", "A-6"), List.of("A-7"));
        }

        @Test
        @Disabled("implement the production code first")
        void usesGatherers() {
            var code = Source.ofClass(Revenue.class);

            assertThat(code.count("\\.gather\\(")).as("calls of gather()").isEqualTo(3);
            assertThat(code.contains("\\b(for|while)\\b")).as("loops").isFalse();
            assertThat(code.contains("\\b\\w+\\.toList\\(\\)\\s*;")).as("toList() on the input").isFalse();
        }
    }

    @Nested
    class Task2DistinctBy {

        record Notification(String email, String text) {
        }

        @Test
        @Disabled("implement the production code first")
        void keepsTheFirstElementPerKey() {
            var notifications = Stream.of(
                    new Notification("ada@example.com", "Your order has shipped"),
                    new Notification("grace@example.com", "Welcome!"),
                    new Notification("ada@example.com", "Rate your order"),
                    new Notification("konrad@example.com", "Welcome!"),
                    new Notification("grace@example.com", "Your cart misses you"));

            assertThat(notifications.gather(MoreGatherers.distinctBy(Notification::email)).map(Notification::text))
                    .containsExactly("Your order has shipped", "Welcome!", "Welcome!");
        }

        @Test
        @Disabled("implement the production code first")
        void worksWithAnyKey() {
            assertThat(Stream.of("kiwi", "fig", "plum", "apple", "date", "banana")
                    .gather(MoreGatherers.distinctBy(String::length)))
                    .containsExactly("kiwi", "fig", "apple", "banana");
        }

        @Test
        @Disabled("implement the production code first")
        void worksOnInfiniteStreams() {
            assertThat(Stream.iterate(1, i -> i + 1).gather(MoreGatherers.distinctBy(i -> i % 4)).limit(4))
                    .containsExactly(1, 2, 3, 4);
        }
    }

    @Nested
    class Task3Sessions {

        static final Duration HALF_AN_HOUR = Duration.ofMinutes(30);

        static Click click(String time) {
            return new Click("ada", "/products", Instant.parse("2026-10-23T" + time + ":00Z"));
        }

        @Test
        @Disabled("implement the production code first")
        void splitsClicksAtLongPauses() {
            var clicks = Stream.of(click("10:00"), click("10:05"), click("10:20"), click("11:30"), click("11:31"));

            assertThat(clicks.gather(MoreGatherers.sessions(HALF_AN_HOUR))).containsExactly(
                    List.of(click("10:00"), click("10:05"), click("10:20")),
                    List.of(click("11:30"), click("11:31")));
        }

        @Test
        @Disabled("implement the production code first")
        void aPauseOfExactlyMaxGapContinuesTheSession() {
            var clicks = Stream.of(click("10:00"), click("10:30"), click("11:01"));

            assertThat(clicks.gather(MoreGatherers.sessions(HALF_AN_HOUR))).containsExactly(
                    List.of(click("10:00"), click("10:30")),
                    List.of(click("11:01")));
        }

        @Test
        @Disabled("implement the production code first")
        void handlesEdgeCases() {
            assertThat(Stream.<Click>empty().gather(MoreGatherers.sessions(HALF_AN_HOUR))).isEmpty();
            assertThat(Stream.of(click("10:00")).gather(MoreGatherers.sessions(HALF_AN_HOUR)))
                    .containsExactly(List.of(click("10:00")));
        }

        @Test
        @Disabled("implement the production code first")
        void worksOnInfiniteStreams() {
            // Three clicks 20 minutes apart, then a pause of more than 2 hours, and so on forever.
            var start = click("00:00");
            var clicks = Stream.iterate(0, i -> i + 1).map(i -> new Click(start.user(), start.page(),
                    start.at().plus(Duration.ofMinutes(i / 3 * 180 + i % 3 * 20))));

            assertThat(clicks.gather(MoreGatherers.sessions(HALF_AN_HOUR)).limit(2)).hasSize(2).allSatisfy(session ->
                    assertThat(session).hasSize(3));
        }
    }
}
