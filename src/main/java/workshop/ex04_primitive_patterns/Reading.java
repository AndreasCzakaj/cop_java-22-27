package workshop.ex04_primitive_patterns;

import java.time.Instant;

/// Given code: do not change.
///
/// What the sensors in our warehouses send.
public sealed interface Reading {

    record Temperature(String sensorId, double celsius, Instant at) implements Reading {
    }

    record Humidity(String sensorId, double percent, Instant at) implements Reading {
    }

    record BatteryLevel(String sensorId, int percent, Instant at) implements Reading {
    }

    record Heartbeat(String sensorId, Instant at) implements Reading {
    }
}
