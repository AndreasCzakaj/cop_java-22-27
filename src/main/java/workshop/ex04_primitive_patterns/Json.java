package workshop.ex04_primitive_patterns;

import java.util.List;
import java.util.Map;

/// Given code: do not change.
///
/// A JSON document. Like in JavaScript, every number is a `double`.
public sealed interface Json {

    record JsonNull() implements Json {
    }

    record JsonBoolean(boolean value) implements Json {
    }

    record JsonNumber(double value) implements Json {
    }

    record JsonString(String value) implements Json {
    }

    record JsonArray(List<Json> elements) implements Json {
    }

    /// Use a `LinkedHashMap` (or another `SequencedMap`) to keep the order of the members.
    record JsonObject(Map<String, Json> members) implements Json {
    }
}
