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
/// - Want to go further? Look up "shebang" in JEP 330 and turn the script into
///   an executable file without the `.java` extension. (Not tested.)
///
/// ## Verify
/// `make test ex=01`

// TODO: your script
