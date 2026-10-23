package workshop.ex05_structured_concurrency;

/// Given code: do not change.
///
/// The API of a parcel service. Some are fast, some are slow, some are down.
@FunctionalInterface
public interface Carrier {

    Quote quote(Parcel parcel) throws InterruptedException;
}
