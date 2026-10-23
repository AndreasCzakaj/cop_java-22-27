import java.util.Optional;

/// One request of the access log, e.g. `2026-10-21T08:00:02Z GET /api/orders/4711 200 87ms`.
/// The path is normalized: no query string, `{id}` instead of numeric segments.
record LogLine(String method, String path, long millis) {

    static Optional<LogLine> parse(String line) {
        var fields = line.strip().split("\\s+");
        if (fields.length != 5 || !fields[4].matches("\\d+ms")) {
            return Optional.empty();
        }
        var path = fields[2]
                .replaceFirst("\\?.*", "")
                .replaceAll("/\\d+(?=/|$)", "/{id}");
        return Optional.of(new LogLine(fields[1], path, Long.parseLong(fields[4].replace("ms", ""))));
    }
}
