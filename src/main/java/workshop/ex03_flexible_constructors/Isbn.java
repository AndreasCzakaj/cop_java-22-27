package workshop.ex03_flexible_constructors;

/// # Exercise 03 / Task 1: Validate before `super(...)`
/// *(Java 25: flexible constructor bodies)*
///
/// ## Problem
/// An ISBN is accepted with or without hyphens and spaces and is stored as
/// 13 digits. Invalid ISBNs are rejected with an exception, but only AFTER
/// `super(...)` has run: until Java 25, `super(...)` had to be the first
/// statement. By then `ProductCode` has already registered the invalid object
/// in the `CodeRegistry`, and the search index serves garbage.
///
/// The classic workaround is a static helper inside the call,
/// `super(validate(normalize(isbn)))`, which gets unreadable fast.
///
/// ## Task
/// Normalize and validate the ISBN **before** calling `super(...)`, so that an
/// invalid ISBN never reaches `ProductCode`. The error message stays as it is.
///
/// ## Constraints
/// - `ProductCode` and `CodeRegistry` must not change.
/// - Pass a local variable to `super(...)`, not an expression.
///
/// ## Hints
/// - Code before `super(...)` may use the constructor parameters, local
///   variables and static members. It must not use `this`, neither explicitly
///   nor implicitly (instance methods, instance fields). Why?
///
/// ## Verify
/// `make test ex=03`
public final class Isbn extends ProductCode {

    public Isbn(String isbn) {
        var digits = isbn.replace("-", "").replace(" ", "");
        if (!hasValidChecksum(digits)) {
            throw new IllegalArgumentException("Invalid ISBN-13: " + isbn);
        }
        super(digits);
    }

    private static boolean hasValidChecksum(String digits) {
        if (!digits.matches("\\d{13}")) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += (digits.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
        }
        return sum % 10 == 0;
    }
}
