## Summary

- [Index](index.md)
- [Defining Projection Classes](projections.md)
- [Filters and Specifications](filters.md)
- [Pagination and Sorting](pagination.md)
- [Executing Queries](execution.md)
- Custom Select Handlers
  - [Creating a Custom Select Handler](#creating-a-custom-select-handler-)
  - [Registering the Custom Select Handler](#registering-the-custom-select-handler-)
  - [Using the Custom Select Handler in Projections](#using-the-custom-select-handler-in-projections-)
- [Custom Filter Handlers](custom-filter-handlers.md)
- [Logging and Debug](logging.md)
- [For Spring Boot users](for-springboot-users.md)

## Custom Select Handlers

In some cases, you may need to perform custom transformations or calculations on the selected fields in your projection. The `ProjectionQuery` library allows you to define custom select handlers to achieve this.

To create a custom select handler, you need to implement the `ProjectionSelectOperatorHandler` interface, which defines how the selected field expression should be constructed. Once you have implemented your custom select handler, you can register it with the `ProjectionSelectOperatorProvider` and use it in your projections.

--- 

### Creating a Custom Select Handler [↑](#summary)

Here we will create a handler that will select only the numbers from a given property.

```java
public class OnlyNumbersHandler implements ProjectionSelectOperatorHandler {

    @Override
    public boolean aggregate() {
        return false;
    }

    @Override
    public Expression<?> apply(PathResolver pathResolver, CriteriaBuilder cb, Root<?> root, String fieldName) {
        return cb.function(
                "REGEXP_REPLACE",
                String.class,
                pathResolver.resolve(root, fieldName),
                cb.literal("[^0-9]"),
                cb.literal("g")
        );
    }
}
```

In some scenarios, you may need to perform custom transformations or calculations directly in the SELECT clause of your projection.

> **Notes:**
> 1. The SQL function used inside the handler must be supported by your underlying database.
> The example above uses `REGEXP_REPLACE`, which may not be available in all SQL dialects.
> 2. If your handler performs an aggregation (e.g., COUNT, SUM, AVG), you must return `true` in the `aggregate()` method.

---

### Registering the Custom Select Handler [↑](#summary)

After creating your custom select handler, you need to register it with `ProjectionSelectOperatorProvider` so that it can be used in your projections.

```java
ProjectionSelectOperatorProvider
    .getInstance()
    .register(new OnlyNumbersHandler());
```

> **Note:** You only need to register the handler once, usually during application initialization.

### Using the Custom Select Handler in Projections [↑](#summary)

After registering your custom select handler, you can use it in your projections. To do this, simply reference the handler class in the `@ProjectionField` annotation:

```java
@Projection(of = Customer.class)
public record CustomerProjection(
        @ProjectionField Long id,
        @ProjectionField String name,
        @ProjectionField(selectHandler = OnlyNumbersHandler.class) String phoneNumber
) { }
```

The SQL generated for this projection would look something like this:

```sql
select
    id,
    name,
    regexp_replace(phone_number, '[^0-9]', 'g') as phone_number
from customer
```

[← Executing Queries](execution.md) · [↑ Back to top](#summary) · [Next → Custom Filter Handlers](custom-filter-handlers.md)
