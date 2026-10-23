package workshop.ex04_primitive_patterns;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import workshop.ex04_primitive_patterns.Json.JsonArray;
import workshop.ex04_primitive_patterns.Json.JsonBoolean;
import workshop.ex04_primitive_patterns.Json.JsonNull;
import workshop.ex04_primitive_patterns.Json.JsonNumber;
import workshop.ex04_primitive_patterns.Json.JsonObject;
import workshop.ex04_primitive_patterns.Json.JsonString;
import workshop.ex04_primitive_patterns.Reading.BatteryLevel;
import workshop.ex04_primitive_patterns.Reading.Heartbeat;
import workshop.ex04_primitive_patterns.Reading.Humidity;
import workshop.ex04_primitive_patterns.Reading.Temperature;
import workshop.support.Source;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class PrimitivePatternsTest {

    @Nested
    class Task1Config {

        final Config config = new Config(Map.ofEntries(
                Map.entry("port", 8080),
                Map.entry("poolSize", 20L),
                Map.entry("retries", 3.0),
                Map.entry("offset", -12.0),
                Map.entry("envPort", " 8443 "),
                Map.entry("timeoutSeconds", 2.5),
                Map.entry("maxUploadBytes", 3_000_000_000L),
                Map.entry("maxDownloadBytes", 1e10),
                Map.entry("host", "localhost"),
                Map.entry("debug", true)));

        @Test
        @Disabled("implement the production code first")
        void readsExactInts() {
            assertThat(config.getInt("port")).isEqualTo(8080);
            assertThat(config.getInt("poolSize")).isEqualTo(20);
            assertThat(config.getInt("retries")).isEqualTo(3);
            assertThat(config.getInt("offset")).isEqualTo(-12);
            assertThat(config.getInt("envPort")).isEqualTo(8443);
        }

        @ParameterizedTest
        @Disabled("implement the production code first")
        @ValueSource(strings = {"timeoutSeconds", "maxUploadBytes", "maxDownloadBytes", "host", "debug"})
        void rejectsEverythingElse(String key) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> config.getInt(key))
                    .withMessageStartingWith(key + ": ")
                    .withMessageEndingWith(" is not an int");
        }

        @Test
        @Disabled("implement the production code first")
        void rejectsAFractionWithAHelpfulMessage() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> config.getInt("timeoutSeconds"))
                    .withMessage("timeoutSeconds: 2.5 is not an int");
        }

        @Test
        @Disabled("implement the production code first")
        void rejectsAMissingKey() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> config.getInt("missing"))
                    .withMessage("missing: null is not an int");
        }

        @Test
        @Disabled("implement the production code first")
        void letsThePatternsDoTheWork() {
            var code = Source.ofClass(Config.class);

            assertThat(code.contains("\\bswitch\\b")).as("a switch").isTrue();
            assertThat(code.contains("\\bcase\\s+int\\s+\\w+")).as("case int ...").isTrue();
            assertThat(code.contains("instanceof\\s+int\\b")).as("... instanceof int ...").isTrue();
            assertThat(code.contains("\\(\\s*int\\s*\\)")).as("a cast to int").isFalse();
            assertThat(code.contains("intValue|longValue|doubleValue|toIntExact|Math\\.")).as("number conversions").isFalse();
            assertThat(code.contains("catch\\s*\\(\\s*\\w+\\s+_\\s*\\)")).as("catch (... _)").isTrue();
        }
    }

    @Nested
    class Task2JsonWriter {

        @Test
        @Disabled("implement the production code first")
        void writesWholeNumbersWithoutAFraction() {
            var person = new LinkedHashMap<String, Json>();
            person.put("name", new JsonString("Ada"));
            person.put("age", new JsonNumber(36));

            assertThat(JsonWriter.write(new JsonObject(person))).isEqualTo("{\"name\":\"Ada\",\"age\":36}");
        }

        @Test
        @Disabled("implement the production code first")
        void writesNumbers() {
            assertThat(JsonWriter.write(new JsonNumber(-7))).isEqualTo("-7");
            assertThat(JsonWriter.write(new JsonNumber(0))).isEqualTo("0");
            assertThat(JsonWriter.write(new JsonNumber(1e15))).isEqualTo("1000000000000000");
            assertThat(JsonWriter.write(new JsonNumber(2.5))).isEqualTo("2.5");
            assertThat(JsonWriter.write(new JsonNumber(1e20))).isEqualTo("1.0E20");
        }

        @ParameterizedTest
        @Disabled("implement the production code first")
        @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
        void rejectsNumbersThatJsonCannotRepresent(double number) {
            assertThatIllegalArgumentException().isThrownBy(() -> JsonWriter.write(new JsonNumber(number)));
        }

        @Test
        @Disabled("implement the production code first")
        void writesEverythingElseAsBefore() {
            var json = new JsonArray(List.of(
                    new JsonNull(),
                    new JsonBoolean(true),
                    new JsonString("say \"hi\"\n"),
                    new JsonArray(List.of()),
                    new JsonObject(Map.of("pi", new JsonNumber(3.14)))));

            assertThat(JsonWriter.write(json)).isEqualTo("[null,true,\"say \\\"hi\\\"\\n\",[],{\"pi\":3.14}]");
        }

        @Test
        @Disabled("implement the production code first")
        void deconstructsTheRecords() {
            var code = Source.ofClass(JsonWriter.class);

            assertThat(code.contains("case\\s+JsonNumber\\s*\\(\\s*long\\s+\\w+\\s*\\)")).as("case JsonNumber(long ...)").isTrue();
            assertThat(code.contains("\\binstanceof\\b")).as("instanceof").isFalse();
            assertThat(code.contains("\\bdefault\\b")).as("default").isFalse();
            assertThat(code.unnamed()).as("unnamed patterns").isPositive();
        }
    }

    @Nested
    class Task3Alerts {

        static final Instant NOW = Instant.parse("2026-10-23T08:00:00Z");

        @Test
        @Disabled("implement the production code first")
        void raisesAFireAlarm() {
            assertThat(Alerts.alertFor(new Temperature("T-17", 51.5, NOW)))
                    .contains("Fire alarm: sensor T-17 reports 51.5 °C");
            assertThat(Alerts.alertFor(new Temperature("T-18", 50.0, NOW)))
                    .contains("Fire alarm: sensor T-18 reports 50.0 °C");
        }

        @Test
        @Disabled("implement the production code first")
        void asksForANewBattery() {
            assertThat(Alerts.alertFor(new BatteryLevel("B-03", 7, NOW)))
                    .contains("Replace the battery of sensor B-03 (7 %)");
        }

        @Test
        @Disabled("implement the production code first")
        void staysQuietOtherwise() {
            assertThat(Alerts.alertFor(new Temperature("T-17", 49.9, NOW))).isEmpty();
            assertThat(Alerts.alertFor(new BatteryLevel("B-03", 10, NOW))).isEmpty();
            assertThat(Alerts.alertFor(new Humidity("H-01", 99.0, NOW))).isEmpty();
            assertThat(Alerts.alertFor(new Heartbeat("T-17", NOW))).isEmpty();
        }

        @Test
        @Disabled("implement the production code first")
        void usesUnnamedPatterns() {
            var code = Source.ofClass(Alerts.class);

            assertThat(code.contains("\\bdefault\\b")).as("default").isFalse();
            assertThat(code.count("\\bcase\\b")).as("number of case labels").isEqualTo(3);
            assertThat(code.unnamed()).as("unnamed variables and patterns").isGreaterThanOrEqualTo(5);
        }
    }
}
