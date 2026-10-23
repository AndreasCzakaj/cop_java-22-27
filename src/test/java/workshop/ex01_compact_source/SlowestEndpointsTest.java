package workshop.ex01_compact_source;

import org.junit.jupiter.api.Test;
import workshop.support.Source;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    @Test
    void printsTheTopThreeLikeTheOldScript() throws Exception {
        var result = run("", DIR.resolve("access.log").toString(), "3");

        assertThat(result.stderr()).isEmpty();
        assertThat(result.stdout()).isEqualTo("""
                Slowest endpoints in access.log
                 1. GET /api/reports/{id}/pdf          2000 ms    3 requests
                 2. POST /api/orders                    680 ms    3 requests
                 3. GET /api/customers/{id}/orders      573 ms    3 requests
                """);
        assertThat(result.exitCode()).isZero();
    }

    @Test
    void printsTheTopFiveByDefault() throws Exception {
        var result = run("", DIR.resolve("access.log").toString());

        assertThat(result.stdout()).isEqualTo("""
                Slowest endpoints in access.log
                 1. GET /api/reports/{id}/pdf          2000 ms    3 requests
                 2. POST /api/orders                    680 ms    3 requests
                 3. GET /api/customers/{id}/orders      573 ms    3 requests
                 4. GET /api/products                   153 ms    3 requests
                 5. GET /api/orders                     112 ms    1 requests
                """);
    }

    @Test
    void asksForTheLogFileIfItIsMissing() throws Exception {
        var result = run(DIR.resolve("access.log") + "\n");

        assertThat(result.stdout())
                .startsWith("Log file: ")
                .contains("Slowest endpoints in access.log\n");
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
        assertThat(source.contains("LogLine(\\.|::)parse\\b")).as("LogLine.parse").isTrue();
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
