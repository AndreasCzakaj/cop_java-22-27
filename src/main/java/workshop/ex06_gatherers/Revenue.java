package workshop.ex06_gatherers;

import java.util.List;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/// # Exercise 06 / Task 1: The built-in gatherers
/// *(Java 24: stream gatherers)*
///
/// ## Problem
/// The sales dashboard needs a few numbers that streams could not compute
/// before Java 24: a moving average needs the neighbours of an element, a
/// running total needs the previous result, and batches need several elements
/// at once. So the code below collects the stream into a list and falls back
/// to index loops. It works, but every new figure means another loop with
/// another off-by-one error waiting to happen.
///
/// `Stream.gather(...)` adds custom intermediate operations to streams, and
/// `java.util.stream.Gatherers` comes with the most common ones.
///
/// ## Task
/// Rewrite the three methods as stream pipelines with `gather(...)`. They work
/// already, the tests describe what they do. Keep them green.
///
/// ## Constraints
/// - One stream pipeline per method, no loops, no `toList()` on the input
///   before gathering.
///
/// ## Hints
/// - `Gatherers.windowSliding(n)`, `Gatherers.windowFixed(n)`,
///   `Gatherers.scan(initial, function)`
///
/// ## Verify
/// `make test ex=06`
public final class Revenue {

    private Revenue() {
    }

    /// The average of each `days` consecutive days, e.g. `[10, 20, 60]`, 2 days → `[15.0, 40.0]`.
    /// Empty if there are fewer than `days` days.
    public static List<Double> movingAverage(Stream<Integer> dailyRevenue, int days) {
        return dailyRevenue
                .gather(Gatherers.windowSliding(days))
                .filter(window -> window.size() == days)
                .map(window -> window.stream().mapToInt(Integer::intValue).average().orElseThrow())
                .toList();
    }

    /// The total so far after each day, e.g. `[10, 20, 60]` → `[10, 30, 90]`.
    public static List<Integer> runningTotal(Stream<Integer> dailyRevenue) {
        return dailyRevenue
                .gather(Gatherers.scan(() -> 0, Integer::sum))
                .toList();
    }

    /// Order IDs in batches of `size` for the bulk import into the ERP system. The last batch may be smaller.
    public static List<List<String>> batches(Stream<String> orderIds, int size) {
        return orderIds
                .gather(Gatherers.windowFixed(size))
                .toList();
    }
}
