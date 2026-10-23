package workshop.ex03_flexible_constructors;

import java.util.ArrayList;
import java.util.List;

/// Given code: do not change.
///
/// A text report. The title is underlined, then come the lines.
public abstract class Report {

    private final List<String> lines = new ArrayList<>();

    protected Report() {
        var title = title();
        lines.add(title);
        lines.add("=".repeat(title.length()));
    }

    /// Called by the constructor of this class: subclasses have to be ready for that.
    protected abstract String title();

    protected final void addLine(String line) {
        lines.add(line);
    }

    public final String render() {
        return String.join("\n", lines);
    }
}
