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
/// - Parsing is done already: `LogLine.parse(line)` (in `LogLine.java`, next
///   to this file) turns a log line into its endpoint (`GET /api/orders/{id}`)
///   and duration, or into an empty `Optional` for empty and malformed lines.
/// - Print the `top` endpoints (default 5) with the highest average duration,
///   rounded down to whole milliseconds.
/// - If the log file is missing from the command line, ask for it with the
///   prompt `Log file: `.
///
/// The output must match the script's output exactly:
///
/// ```
/// Slowest endpoints in access.log
///  1. GET /api/reports/{id}/pdf          2000 ms    3 requests
///  2. POST /api/orders                    680 ms    3 requests
///  3. GET /api/customers/{id}/orders      573 ms    3 requests
/// ```
///
/// ## Constraints
/// - No class around `main`, no `public static void main(String[] args)`.
/// - No `import` statements at all.
/// - No `System.out`: use `IO.println()` and `IO.readln()`.
/// - Use `LogLine.parse`, do not change `LogLine.java`.
///
/// ## Hints
/// - `void main(String[] args) { ... }` is all you need; `void main()` works too.
/// - A compact source file imports the whole module `java.base` automatically:
///   `List`, `Map`, `Files`, `Path`, `Collectors`, ... are just there.
/// - The source launcher finds `LogLine.java` because it is in the same
///   directory. No `javac`, no classpath.
/// - `Collectors.groupingBy(LogLine::endpoint, Collectors.summarizingLong(LogLine::millis))`
///   gives you the average and the count per endpoint.
/// - `Map.Entry.comparingByValue(comparator)` sorts the entries of a map by value.
/// - Row format: `"%2d. %-32s %6d ms %4d requests".formatted(...)`
///
/// ## To discuss
/// - A file without the `.java` extension that starts with
///   `#!/usr/bin/env -S java --source 27` can be run like any shell script.
///   But in this "shebang" mode the launcher compiles only that one file.
///   What would that mean for `LogLine.java`?
///
/// ## Verify
/// `make test ex=01`

// TODO: your script
