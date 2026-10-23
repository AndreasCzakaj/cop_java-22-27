/// # Exercise 01: Compact source files - Java as a scripting language
/// *(Java 25: compact source files and instance `main`, `java.lang.IO`;
/// Java 22: launching multi-file source-code programs)*
///
/// ## Problem
/// Ops runs `slowest-endpoints.sh` every morning. It is a bash/awk script from
/// 2019, nobody dares to touch it, and now they need a change. Everyone on the
/// team knows Java, but "a Java program" used to mean a class, a
/// `public static void main(String[] args)`, a dozen imports and a build tool.
///
/// Not anymore: `java SlowestEndpoints.java access.log` compiles and runs a
/// source file in one go, the file does not need a class, and it can use other
/// source files next to it.
///
/// ## Task
/// Rewrite the script as this Java file. Run both and compare:
///
/// ```
/// cd ex01-compact-source
/// ./slowest-endpoints.sh access.log 3
/// java SlowestEndpoints.java access.log 3
/// ```
///
/// Usage: `java SlowestEndpoints.java [log file] [top]`
///
/// - Each log line looks like `2026-10-21T08:00:02Z GET /api/orders/4711 200 87ms`
///   (timestamp, method, path, status, duration).
/// - An endpoint is method + path. In the path, the query string (`?page=2`) is
///   dropped, and every purely numeric segment is replaced by `{id}`:
///   `/api/customers/42/orders` becomes `/api/customers/{id}/orders`.
/// - Print the `top` endpoints (default 5) with the highest average duration.
///   The average is rounded down to whole milliseconds. Equal averages are
///   sorted by method, then path.
/// - Empty lines are ignored. Lines that do not have exactly 5 fields or whose
///   duration is not `<digits>ms` are skipped and counted.
/// - If the log file is missing from the command line, ask for it with the
///   prompt `Log file: `.
/// - If the file does not exist, print `File not found: <file>` to stderr and
///   exit with status 1.
///
/// The output must match the script's output exactly:
///
/// ```
/// Slowest endpoints in access.log (top 3 of 8)
///  1. GET    /api/reports/{id}/pdf          2000 ms    3 requests
///  2. POST   /api/orders                     680 ms    3 requests
///  3. GET    /api/customers/{id}/orders      573 ms    3 requests
/// Skipped 2 malformed lines
/// ```
///
/// The last line is only printed if lines were skipped.
///
/// ## Constraints
/// - No class around `main`, no `public static void main(String[] args)`.
/// - No `import` statements at all.
/// - No `System.out`: use `IO.println()` and `IO.readln()`.
/// - Parsing one line belongs into a record `LogLine` in its own file,
///   `LogLine.java`, next to this one (create it). Its `parse` method returns
///   an `Optional<LogLine>`, which is empty for a malformed line.
///
/// ## Hints
/// - `void main(String[] args) { ... }` is all you need; `void main()` works too.
/// - A compact source file imports the whole module `java.base` automatically:
///   `List`, `Map`, `Files`, `Path`, `Collectors`, ... are just there.
/// - The source launcher finds `LogLine.java` because it is in the same
///   directory. No `javac`, no classpath.
/// - Row format: `"%2d. %-6s %-28s %6d ms %4d requests".formatted(...)`
///
/// ## To discuss
/// - A file without the `.java` extension that starts with
///   `#!/usr/bin/env -S java --source 27` can be run like any shell script.
///   But in this "shebang" mode the launcher compiles only that one file.
///   What would that mean for `LogLine.java`?
///
/// ## Verify
/// `make test ex=01`

record Stats(String method, String path, long average, int requests) {
}

void main(String[] args) throws IOException {
    var file = Path.of(args.length > 0 ? args[0] : IO.readln("Log file: "));
    int top = args.length > 1 ? Integer.parseInt(args[1]) : 5;
    if (!Files.isReadable(file)) {
        System.err.println("File not found: " + file);
        System.exit(1);
    }

    var lines = Files.readAllLines(file).stream().filter(line -> !line.isBlank()).toList();
    var parsed = lines.stream().map(LogLine::parse).flatMap(Optional::stream).toList();

    var stats = parsed.stream()
            .collect(Collectors.groupingBy(line -> List.of(line.method(), line.path())))
            .values().stream()
            .map(requests -> new Stats(requests.getFirst().method(), requests.getFirst().path(),
                    requests.stream().mapToLong(LogLine::millis).sum() / requests.size(), requests.size()))
            .sorted(Comparator.comparingLong(Stats::average).reversed()
                    .thenComparing(Stats::method)
                    .thenComparing(Stats::path))
            .toList();

    var shown = Math.min(top, stats.size());
    IO.println("Slowest endpoints in %s (top %d of %d)".formatted(file.getFileName(), shown, stats.size()));
    for (int rank = 1; rank <= shown; rank++) {
        var row = stats.get(rank - 1);
        IO.println("%2d. %-6s %-28s %6d ms %4d requests".formatted(rank, row.method(), row.path(), row.average(), row.requests()));
    }

    var skipped = lines.size() - parsed.size();
    if (skipped > 0) {
        IO.println("Skipped %d malformed lines".formatted(skipped));
    }
}
