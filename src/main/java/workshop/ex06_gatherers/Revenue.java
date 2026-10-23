package workshop.ex06_gatherers;

import java.util.ArrayList;
import java.util.List;
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
/// - What does `windowSliding(3)` emit for a stream of only two elements?
///
/// ## Verify
/// `make test ex=06`
public final class Revenue {

    private Revenue() {
    }

    /// The average of each `days` consecutive days, e.g. `[10, 20, 60]`, 2 days → `[15.0, 40.0]`.
    /// Empty if there are fewer than `days` days.
    public static List<Double> movingAverage(Stream<Integer> dailyRevenue, int days) {
        var revenue = dailyRevenue.toList();
        var averages = new ArrayList<Double>();
        for (int start = 0; start + days <= revenue.size(); start++) {
            int sum = 0;
            for (int i = start; i < start + days; i++) {
                sum += revenue.get(i);
            }
            averages.add((double) sum / days);
        }
        return averages;
    }

    /// The total so far after each day, e.g. `[10, 20, 60]` → `[10, 30, 90]`.
    public static List<Integer> runningTotal(Stream<Integer> dailyRevenue) {
        var revenue = dailyRevenue.toList();
        var totals = new ArrayList<Integer>();
        int total = 0;
        for (int amount : revenue) {
            total += amount;
            totals.add(total);
        }
        return totals;
    }

    /// Order IDs in batches of `size` for the bulk import into the ERP system. The last batch may be smaller.
    public static List<List<String>> batches(Stream<String> orderIds, int size) {
        var ids = orderIds.toList();
        var batches = new ArrayList<List<String>>();
        for (int start = 0; start < ids.size(); start += size) {
            batches.add(ids.subList(start, Math.min(start + size, ids.size())));
        }
        return batches;
    }
}
