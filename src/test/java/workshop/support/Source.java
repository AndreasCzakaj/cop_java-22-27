package workshop.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/// Looks at the source code of a class so that the tests can check HOW a task
/// was solved (e.g. "uses a gatherer"), not only that it works.
/// Comments, string literals and text blocks are removed before matching.
public final class Source {

    private static final Pattern COMMENTS_AND_STRINGS = Pattern.compile(
            "(?s)\"\"\".*?\"\"\"|\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'|//[^\\n]*|/\\*.*?\\*/");

    private final String code;

    private Source(String code) {
        this.code = COMMENTS_AND_STRINGS.matcher(code)
                .replaceAll(match -> match.group().startsWith("\"") || match.group().startsWith("'") ? "\"\"" : " ");
    }

    public static Source ofClass(Class<?> type) {
        return ofFile(Path.of("src/main/java", type.getName().replace('.', '/') + ".java"));
    }

    public static Source ofFile(Path file) {
        try {
            return new Source(Files.readString(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /// Number of matches of a regular expression, e.g. `"\\.gather\\("`.
    public int count(String regex) {
        return (int) Pattern.compile(regex).matcher(code).results().count();
    }

    public boolean contains(String regex) {
        return count(regex) > 0;
    }

    /// Number of unnamed variables and patterns (`_`).
    public int unnamed() {
        return count("(?<![\\w$])_(?![\\w$])");
    }

    public String code() {
        return code;
    }
}
