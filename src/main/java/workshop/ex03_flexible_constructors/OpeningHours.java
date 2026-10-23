package workshop.ex03_flexible_constructors;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/// # Exercise 03 / Task 3: Prepare the arguments of `this(...)`
/// *(Java 25: flexible constructor bodies)*
///
/// ## Problem
/// Opening hours come from a CSV file as text like `"09:00-17:30"`. A
/// constructor that takes this text has to delegate to the canonical
/// constructor with `this(...)`. Until Java 25 that call had to be the first
/// statement, so the text was split twice inside the call, and malformed text
/// like `"09:00"` caused an `ArrayIndexOutOfBoundsException`.
///
/// ## Task
/// Implement the constructor `OpeningHours(String text)`:
///
/// - `"09:00-17:30"` opens at 9:00 and closes at 17:30. Spaces around the
///   `-` and at both ends are allowed: `" 09:00 - 17:30 "` is fine.
/// - Text that does not consist of exactly two times separated by one `-`
///   throws an `IllegalArgumentException` with the message
///   `Expected "HH:MM-HH:MM" but got "<text>"`.
/// - The rule that `opens` is before `closes` stays in the canonical
///   constructor and must not be duplicated.
///
/// ## Constraints
/// - Call `split` only once.
/// - Delegate to the canonical constructor with `this(...)`.
///
/// ## Verify
/// `make test ex=03`
public record OpeningHours(LocalTime opens, LocalTime closes) {

    public OpeningHours {
        if (!opens.isBefore(closes)) {
            throw new IllegalArgumentException("Opens at " + opens + " but closes at " + closes);
        }
    }

    public OpeningHours(String text) {
        var times = text.split("-");
        var malformed = new IllegalArgumentException("Expected \"HH:MM-HH:MM\" but got \"" + text + "\"");
        if (times.length != 2) {
            throw malformed;
        }
        LocalTime opens;
        LocalTime closes;
        try {
            opens = LocalTime.parse(times[0].strip());
            closes = LocalTime.parse(times[1].strip());
        } catch (DateTimeParseException _) {
            throw malformed;
        }
        this(opens, closes);
    }
}
