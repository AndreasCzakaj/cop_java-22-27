package workshop.ex05_structured_concurrency;

import java.util.concurrent.Callable;

/// # Exercise 05 / Task 1: From `ThreadLocal` to `ScopedValue`
/// *(Java 25: scoped values)*
///
/// ## Problem
/// Our shop serves several tenants. The web layer binds the tenant of a
/// request with `runAs()`, and every backend call reads it with `tenant()`.
/// That is a `ThreadLocal`, and it has three problems:
///
/// - It is not passed on to other threads. As soon as we fork work to other
///   threads (task 2), the backends fail with "No tenant bound".
/// - It is mutable: any code can call `set()` and change the tenant for
///   everything that follows, and forgetting `remove()` leaks it into the next
///   request that the pooled thread serves.
/// - With a million virtual threads, a million copies add up.
///
/// A `ScopedValue` is bound for the duration of a call, read-only, cheap, and
/// inherited by the subtasks of a `StructuredTaskScope`.
///
/// ## Task
/// Replace the `ThreadLocal` with a `ScopedValue`. The methods keep their
/// signatures and behaviour:
///
/// - `runAs()` binds the tenant while `action` runs. Nested calls bind another
///   tenant for the inner action only.
/// - `tenant()` throws an `IllegalStateException("No tenant bound")` outside
///   of `runAs()`.
///
/// ## Hints
/// - `ScopedValue.newInstance()`, `ScopedValue.where(key, value).call(...)`
/// - `orElseThrow(...)`
///
/// ## Verify
/// `make test ex=05`
public final class RequestContext {

    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();

    private RequestContext() {
    }

    public static <T> T runAs(String tenant, Callable<T> action) throws Exception {
        var previous = TENANT.get();
        TENANT.set(tenant);
        try {
            return action.call();
        } finally {
            TENANT.set(previous);
        }
    }

    public static String tenant() {
        var tenant = TENANT.get();
        if (tenant == null) {
            throw new IllegalStateException("No tenant bound");
        }
        return tenant;
    }
}
