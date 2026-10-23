package workshop.ex01_compact_source;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import workshop.support.Source;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/// Runs `ex01-compact-source/SlowestEndpoints.java` with the source launcher,
/// exactly as Ops would: `java SlowestEndpoints.java access.log 3`.
class SlowestEndpointsTest {

    private static final Path DIR = Path.of("ex01-compact-source");
    private static final Path SCRIPT = DIR.resolve("SlowestEndpoints.java");
    private static final Path LOG_LINE = DIR.resolve("LogLine.java");

    @Test
    void printsTheTopThreeLikeTheOldScript() throws Exception {
        var result = run("", DIR.resolve("access.log").toString(), "3");

        assertThat(result.stderr()).isEmpty();
        assertThat(result.stdout()).isEqualTo("""
                Slowest endpoints in access.log (top 3 of 8)
                 1. GET    /api/reports/{id}/pdf          2000 ms    3 requests
                 2. POST   /api/orders                     680 ms    3 requests
                 3. GET    /api/customers/{id}/orders      573 ms    3 requests
                Skipped 2 malformed lines
                """);
        assertThat(result.exitCode()).isZero();
    }

    @Test
    void printsTheTopFiveByDefault() throws Exception {
        var result = run("", DIR.resolve("access.log").toString());

        assertThat(result.stdout()).isEqualTo("""
                Slowest endpoints in access.log (top 5 of 8)
                 1. GET    /api/reports/{id}/pdf          2000 ms    3 requests
                 2. POST   /api/orders                     680 ms    3 requests
                 3. GET    /api/customers/{id}/orders      573 ms    3 requests
                 4. GET    /api/products                   153 ms    3 requests
                 5. GET    /api/orders                     112 ms    1 requests
                Skipped 2 malformed lines
                """);
    }

    @Test
    void sortsEqualAveragesByMethodThenPathAndSkipsNothing(@TempDir Path tmp) throws Exception {
        var log = Files.writeString(tmp.resolve("tiny.log"), """
                2026-10-21T08:00:01Z POST /b 200 10ms
                2026-10-21T08:00:01Z GET /b/7?x=1 200 10ms

                2026-10-21T08:00:01Z GET /a 200 9ms
                2026-10-21T08:00:01Z GET /a 200 12ms
                """);

        var result = run("", log.toString(), "10");

        assertThat(result.stdout()).isEqualTo("""
                Slowest endpoints in tiny.log (top 3 of 3)
                 1. GET    /a                               10 ms    2 requests
                 2. GET    /b/{id}                          10 ms    1 requests
                 3. POST   /b                               10 ms    1 requests
                """);
    }

    @Test
    void asksForTheLogFileIfItIsMissing() throws Exception {
        var result = run(DIR.resolve("access.log") + "\n");

        assertThat(result.stdout())
                .startsWith("Log file: ")
                .contains("Slowest endpoints in access.log (top 5 of 8)");
    }

    @Test
    void reportsAMissingFile() throws Exception {
        var result = run("", "no-such.log");

        assertThat(result.stderr()).contains("File not found: no-such.log");
        assertThat(result.exitCode()).isEqualTo(1);
    }

    @Test
    void isACompactSourceFile() {
        var source = Source.ofFile(SCRIPT);

        assertThat(source.contains("\\bvoid\\s+main\\s*\\(")).as("a main method").isTrue();
        assertThat(source.contains("\\bclass\\s+SlowestEndpoints\\b")).as("a class around main").isFalse();
        assertThat(source.contains("\\bstatic\\s+void\\s+main\\b")).as("a static main method").isFalse();
        assertThat(source.contains("(?m)^\\s*import\\b")).as("import statements").isFalse();
        assertThat(source.contains("System\\.out")).as("System.out").isFalse();
        assertThat(source.contains("\\bIO\\.println\\(")).as("IO.println()").isTrue();
    }

    @Test
    void parsesLinesInASeparateRecord() {
        assertThat(LOG_LINE).as("LogLine.java next to SlowestEndpoints.java").exists();
        assertThat(Source.ofFile(LOG_LINE).contains("\\brecord\\s+LogLine\\b")).as("a record LogLine").isTrue();
        assertThat(Source.ofFile(LOG_LINE).contains("Optional<LogLine>\\s+parse\\s*\\(")).as("Optional<LogLine> parse(...)").isTrue();
        assertThat(Source.ofFile(SCRIPT).contains("LogLine(\\.|::)parse\\b")).as("the script uses LogLine.parse").isTrue();
    }

    private record Result(int exitCode, String stdout, String stderr) {
    }

    private static Result run(String stdin, String... args) throws IOException, InterruptedException {
        var command = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(), SCRIPT.toString()));
        command.addAll(List.of(args));

        var process = new ProcessBuilder(command).start();
        try (var in = process.getOutputStream()) {
            in.write(stdin.getBytes(StandardCharsets.UTF_8));
        }
        // Read both streams before waiting, so that a full pipe cannot block the script.
        var stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        var stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        if (!process.waitFor(30, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new AssertionError("The script did not finish within 30 seconds.");
        }
        return new Result(process.exitValue(), stdout.replace("\r\n", "\n"), stderr);
    }
}
