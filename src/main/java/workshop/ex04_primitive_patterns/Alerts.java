package workshop.ex04_primitive_patterns;

import workshop.ex04_primitive_patterns.Reading.BatteryLevel;
import workshop.ex04_primitive_patterns.Reading.Heartbeat;
import workshop.ex04_primitive_patterns.Reading.Humidity;
import workshop.ex04_primitive_patterns.Reading.Temperature;

import java.util.Optional;

/// # Exercise 04 / Task 3: Unnamed variables and patterns
/// *(Java 22: unnamed variables and patterns)*
///
/// ## Problem
/// Most sensor readings are boring. Only two kinds need a human:
///
/// - a temperature of 50 °C or more: `Fire alarm: sensor <id> reports <celsius> °C`
/// - a battery level below 10 percent: `Replace the battery of sensor <id> (<percent> %)`
///
/// ## Task
/// Implement `alertFor()`. It returns the alert, or an empty `Optional` for
/// every other reading.
///
/// ```
/// Temperature("T-17", 51.5, ...)  →  "Fire alarm: sensor T-17 reports 51.5 °C"
/// BatteryLevel("B-03", 7, ...)    →  "Replace the battery of sensor B-03 (7 %)"
/// Heartbeat("T-17", ...)          →  empty
/// ```
///
/// ## Constraints
/// - One `switch` with record patterns, and no `default`: when somebody adds a
///   new kind of `Reading`, the compiler shall tell us to handle it here.
/// - Components you do not use are `_`, and so are the patterns of the
///   readings you are not interested in.
/// - All readings without an alert are handled in a single `case`.
///
/// ## Hints
/// - `case Humidity _, Heartbeat _ -> ...`: several patterns in one `case`
///   label are only allowed if they do not declare variables.
///
/// ## Verify
/// `make test ex=04`
public final class Alerts {

    private Alerts() {
    }

    public static Optional<String> alertFor(Reading reading) {
        throw new UnsupportedOperationException("TODO");
    }
}
