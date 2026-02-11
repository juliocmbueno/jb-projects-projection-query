## Summary

- [Index](index.md)
- [Defining Projection Classes](projections.md)
- [Filters and Specifications](filters.md)
- [Pagination and Sorting](pagination.md)
- [Executing Queries](execution.md)
- [Custom Select Handlers](custom-select-handlers.md)
- [Custom Filter Handlers](custom-filter-handlers.md)
- Logging and Debug
  - [INFO — Execution lifecycle & performance](#info--execution-lifecycle--performance-)
  - [DEBUG — Query structure & decisions](#debug--query-structure--decisions-)
  - [TRACE — Detailed internal behavior](#trace--detailed-internal-behavior-)
- [For Spring Boot users](for-springboot-users.md)

## Logging and Debug

The logging system was designed with a framework-first mindset, ensuring:

- No data leakage
- No forced logging implementation
- Full control delegated to the consuming application

> ProjectionQuery emits logs across **three logging levels**, each with a clear purpose.

---

### INFO — Execution lifecycle & performance [↑](#summary)

High-level information about query execution, useful for monitoring and production environments.

Example:

```
INFO  ProjectionProcessor - Executing ProjectionQuery [from=Customer, to=CustomerName, distinct=false, paging=false]
INFO  ProjectionProcessor - ProjectionQuery executed in 28 ms (1 results)
```

---

### DEBUG — Query structure & decisions [↑](#summary)

Provides visibility into how the query was built, without exposing sensitive data.

Example:

```
DEBUG ProjectionProcessor - ProjectionQuery filters summary: 0 specifications, 1 filters
DEBUG ProjectionProcessor - ProjectionQuery orders applied: 0
DEBUG ProjectionProcessor - ProjectionQuery paging applied: first=0, size=10
```

---

### TRACE — Detailed internal behavior [↑](#summary)

Low-level, step-by-step details of query construction.
Ideal for deep debugging and understanding complex filter logic.

Example:

```
TRACE ProjectionProcessor - ProjectionQuery filter added: age GREATER_THAN_OR_EQUAL
TRACE ProjectionProcessor - ProjectionQuery filter added: OR (mainCity equal, secondaryCity equal)
TRACE ProjectionProcessor - ProjectionQuery order added: name ASC
```
> ⚠️ TRACE logs are intentionally verbose and should be enabled only for troubleshooting.

[← Previous: Custom Filter Handlers](custom-filter-handlers.md) · [↑ Back to top](#summary) · [Next → For Spring Boot users](for-springboot-users.md)
