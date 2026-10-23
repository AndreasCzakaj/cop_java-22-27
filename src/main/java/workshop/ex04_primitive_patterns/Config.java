package workshop.ex04_primitive_patterns;

import java.util.Map;

/// # Exercise 04 / Task 1: Safe narrowing with primitive patterns
/// *(Java 27: primitive types in patterns, `instanceof` and `switch`, 5th preview;
/// Java 22: unnamed variables and patterns)*
///
/// ## Problem
/// The configuration is parsed from JSON and environment variables. A JSON
/// parser gives us an `Integer`, a `Long` or a `Double` for a number,
/// depending on the input, and an environment variable is a `String`.
///
/// `getInt()` turns every `Number` into an `int` with `intValue()`, which
/// silently truncates and overflows: `"timeoutSeconds": 2.5` became 2 seconds,
/// and `"maxUploadBytes": 3000000000` became a negative number. Nobody noticed
/// until production.
///
/// ## Task
/// Rewrite `getInt()`: it returns the value if it is exactly an `int`, i.e.
///
/// - an `Integer`,
/// - a `Long` or a `Double` whose value is exactly representable as an `int`
///   (`30L` and `30.0` are fine, `2.5`, `1e10` and `3_000_000_000L` are not),
/// - a `String` that `Integer.parseInt()` accepts after stripping whitespace.
///
/// Anything else, a missing key included, throws an
/// `IllegalArgumentException` with the message `<key>: <value> is not an int`,
/// e.g. `timeoutSeconds: 2.5 is not an int`.
///
/// ## Constraints
/// - One `switch` over the value with primitive type patterns (`case int i`,
///   `case long l when ...`, ...).
/// - No casts to `int`, no `intValue()`, `Math.toIntExact()`, `Math.floor()`, ...:
///   the patterns do the range and exactness checks.
/// - Exceptions you catch and do not use are unnamed (`_`).
///
/// ## Hints
/// - `x instanceof int i` is true if `x` can be converted to `int` without
///   loss of information, and binds the converted value to `i`.
/// - A binding from a guard (`case long l when l instanceof int i`) can be
///   used on the right side of the `->`.
/// - `case long l` also matches a boxed `Long`.
///
/// ## To discuss
/// - `-0.0 instanceof int` is false. Why? Is that what you want for a config value?
///
/// ## Verify
/// `make test ex=04`
public final class Config {

    private final Map<String, Object> values;

    public Config(Map<String, Object> values) {
        this.values = values;
    }

    public int getInt(String key) {
        Object value = values.get(key);
        return switch (value) {
            case int i -> i;
            case long l when l instanceof int i -> i;
            case double d when d instanceof int i -> i;
            case String text -> {
                try {
                    yield Integer.parseInt(text.strip());
                } catch (NumberFormatException _) {
                    throw notAnInt(key, value);
                }
            }
            case null, default -> throw notAnInt(key, value);
        };
    }

    private static IllegalArgumentException notAnInt(String key, Object value) {
        return new IllegalArgumentException(key + ": " + value + " is not an int");
    }
}
