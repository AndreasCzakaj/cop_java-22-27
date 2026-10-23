package workshop.ex04_primitive_patterns;

import workshop.ex04_primitive_patterns.Json.JsonArray;
import workshop.ex04_primitive_patterns.Json.JsonBoolean;
import workshop.ex04_primitive_patterns.Json.JsonNull;
import workshop.ex04_primitive_patterns.Json.JsonNumber;
import workshop.ex04_primitive_patterns.Json.JsonObject;
import workshop.ex04_primitive_patterns.Json.JsonString;

import java.util.stream.Collectors;

/// # Exercise 04 / Task 2: Primitive patterns in record patterns
/// *(Java 27: primitive types in patterns, `instanceof` and `switch`, 5th preview)*
///
/// ## Problem
/// Our REST API answers `{"name":"Ada","age":36.0}`, because every JSON
/// number is a `double`. The frontend now shows "36.0 years". Also, `NaN` and
/// `Infinity` end up in the output, which is not valid JSON.
///
/// ## Task
/// Rewrite `write()` as a single `switch` with record patterns, and fix the
/// numbers:
///
/// - A number that is exactly representable as a `long` is written without a
///   fraction: `36.0` → `36`, `-7.0` → `-7`, `1e15` → `1000000000000000`.
/// - Any other number is written with `Double.toString()`: `2.5` → `2.5`,
///   `1e20` → `1.0E20`.
/// - `NaN` and the infinities throw an `IllegalArgumentException`.
/// - Everything else stays as it is.
///
/// ## Constraints
/// - Deconstruct the records in the `case` labels: `case JsonNumber(long l) -> ...`
/// - No `instanceof`, no casts, no `default`.
/// - Unused pattern variables are unnamed (`_`).
///
/// ## Hints
/// - `case JsonNumber(long l)` only matches if the `double` component converts
///   to `long` without loss.
/// - The cases are tried from top to bottom.
///
/// ## Verify
/// `make test ex=04`
public final class JsonWriter {

    private JsonWriter() {
    }

    public static String write(Json json) {
        return switch (json) {
            case JsonNull _ -> "null";
            case JsonBoolean(boolean value) -> String.valueOf(value);
            case JsonNumber(long value) -> String.valueOf(value);
            case JsonNumber(double value) when Double.isNaN(value) || Double.isInfinite(value) ->
                    throw new IllegalArgumentException(value + " is not allowed in JSON");
            case JsonNumber(double value) -> String.valueOf(value);
            case JsonString(String value) -> quote(value);
            case JsonArray(var elements) -> elements.stream().map(JsonWriter::write).collect(Collectors.joining(",", "[", "]"));
            case JsonObject(var members) -> members.entrySet().stream()
                    .map(member -> quote(member.getKey()) + ":" + write(member.getValue()))
                    .collect(Collectors.joining(",", "{", "}"));
        };
    }

    private static String quote(String text) {
        return '"' + text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + '"';
    }
}
