package workshop.ex03_flexible_constructors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import workshop.support.Source;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.catchThrowable;

class FlexibleConstructorsTest {

    @Nested
    class Task1Isbn {

        @BeforeEach
        void clearRegistry() {
            CodeRegistry.clear();
        }

        @Test
        @Disabled("implement the production code first")
        void acceptsHyphensAndSpaces() {
            assertThat(new Isbn("978-3-16-148410-0").value()).isEqualTo("9783161484100");
            assertThat(new Isbn("978 0 13 468599 1").value()).isEqualTo("9780134685991");
        }

        @Test
        @Disabled("implement the production code first")
        void registersValidIsbns() {
            new Isbn("978-3-16-148410-0");

            assertThat(CodeRegistry.values()).containsExactly("9783161484100");
        }

        @ParameterizedTest
        @Disabled("implement the production code first")
        @ValueSource(strings = {"978-3-16-148410-1", "978-3-16-14841", "ISBN 978-3-16-148410-0"})
        void rejectsInvalidIsbns(String invalid) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Isbn(invalid))
                    .withMessage("Invalid ISBN-13: " + invalid);
        }

        @Test
        @Disabled("implement the production code first")
        void neverRegistersAnInvalidIsbn() {
            catchThrowable(() -> new Isbn("978-3-16-148410-1"));

            assertThat(CodeRegistry.values()).as("registered codes").isEmpty();
        }

        @Test
        @Disabled("implement the production code first")
        void passesALocalVariableToSuper() {
            assertThat(Source.ofClass(Isbn.class).contains("\\bsuper\\(\\s*\\w+\\s*\\)\\s*;"))
                    .as("super(<local variable>);")
                    .isTrue();
        }
    }

    @Nested
    class Task2SalesReport {

        @Test
        @Disabled("implement the production code first")
        void hasATitle() {
            var revenue = new LinkedHashMap<String, BigDecimal>();
            revenue.put("Keyboard", new BigDecimal("1299.00"));
            revenue.put("Mouse", new BigDecimal("358.20"));

            var report = new SalesReport("EMEA", YearMonth.of(2026, 9), revenue);

            assertThat(report.render()).isEqualTo("""
                    Sales report EMEA 2026-09
                    =========================
                    Keyboard: 1299.00
                    Mouse: 358.20""");
        }

        @ParameterizedTest
        @Disabled("implement the production code first")
        @ValueSource(strings = {"", "  "})
        void rejectsABlankRegion(String region) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new SalesReport(region, YearMonth.of(2026, 9), Map.of()));
        }

        @Test
        @Disabled("implement the production code first")
        void keepsTheFieldsFinal() {
            var code = Source.ofClass(SalesReport.class);

            assertThat(code.contains("private\\s+final\\s+String\\s+region\\s*;")).as("final region").isTrue();
            assertThat(code.contains("private\\s+final\\s+YearMonth\\s+month\\s*;")).as("final month").isTrue();
        }
    }

    @Nested
    class Task3OpeningHours {

        @Test
        @Disabled("implement the production code first")
        void parsesTheText() {
            assertThat(new OpeningHours("09:00-17:30"))
                    .isEqualTo(new OpeningHours(LocalTime.of(9, 0), LocalTime.of(17, 30)));
        }

        @Test
        @Disabled("implement the production code first")
        void allowsSpaces() {
            assertThat(new OpeningHours(" 09:00 - 17:30 "))
                    .isEqualTo(new OpeningHours(LocalTime.of(9, 0), LocalTime.of(17, 30)));
        }

        @ParameterizedTest
        @Disabled("implement the production code first")
        @ValueSource(strings = {"09:00", "09:00-12:00-13:00", "9-5", "09:00-noon", ""})
        void rejectsMalformedText(String text) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new OpeningHours(text))
                    .withMessage("Expected \"HH:MM-HH:MM\" but got \"" + text + "\"");
        }

        @Test
        @Disabled("implement the production code first")
        void keepsTheRuleOfTheCanonicalConstructor() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new OpeningHours("17:30-09:00"))
                    .withMessage("Opens at 17:30 but closes at 09:00");
        }

        @Test
        @Disabled("implement the production code first")
        void splitsOnceAndDelegates() {
            var code = Source.ofClass(OpeningHours.class);

            assertThat(code.count("\\.split\\(")).as("calls of split()").isEqualTo(1);
            assertThat(code.contains("\\bthis\\(")).as("this(...)").isTrue();
        }
    }
}
