package workshop.ex06_gatherers;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Gatherer;

/// # Exercise 06 / Tasks 2 and 3: Write your own gatherers
/// *(Java 24: stream gatherers)*
///
/// ## Problem
/// `distinct()` compares whole elements, and a stream has no notion of
/// "groups of consecutive elements". Both come up all the time, so we want
/// them as reusable stream operations:
///
/// ```
/// notifications.stream().gather(MoreGatherers.distinctBy(Notification::email))
/// clicks.stream().gather(MoreGatherers.sessions(Duration.ofMinutes(30)))
/// ```
///
/// A `Gatherer` has up to four parts: an **initializer** that creates the
/// state, an **integrator** that gets each element (and the state) and may
/// push results downstream, an optional **combiner** for parallel streams,
/// and an optional **finisher** that runs after the last element.
///
/// ## Task 2: `distinctBy()`
/// Keeps the first element for each key and drops all later elements with
/// the same key, e.g. one newsletter per e-mail address.
///
/// ## Task 3: `sessions()`
/// The clicks of a user arrive in chronological order. A session is a run of
/// clicks in which each click follows the previous one by at most `maxGap`.
/// A longer pause starts a new session. Each session is pushed downstream as
/// a list as soon as it is complete.
///
/// ```
/// 10:00, 10:05, 10:20, 11:30, 11:31   (maxGap = 30 min)
/// → [10:00, 10:05, 10:20], [11:30, 11:31]
/// ```
///
/// ## Constraints
/// - Both gatherers must work on infinite streams: push each result as early
///   as possible instead of collecting all elements first.
/// - Sequential gatherers are fine, no combiner needed.
///
/// ## Hints
/// - `Gatherer.ofSequential(initializer, integrator)` and
///   `Gatherer.ofSequential(initializer, integrator, finisher)`
/// - The integrator is `(state, element, downstream) -> boolean`. Return
///   `false` to stop early, otherwise usually the result of `downstream.push(...)`.
/// - `Gatherer.Integrator.ofGreedy(...)` marks an integrator that never stops
///   early by itself, which lets the stream optimize.
/// - Is the last session complete before the stream ends? Who pushes it?
///
/// ## Verify
/// `make test ex=06`
public final class MoreGatherers {

    private MoreGatherers() {
    }

    public static <T, K> Gatherer<T, ?, T> distinctBy(Function<? super T, ? extends K> key) {
        throw new UnsupportedOperationException("TODO");
    }

    public static Gatherer<Click, ?, List<Click>> sessions(Duration maxGap) {
        throw new UnsupportedOperationException("TODO");
    }
}
