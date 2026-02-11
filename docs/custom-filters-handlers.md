## Summary

- [Index](index.md)
- [Defining Projection Classes](projections.md)
- [Filters and Specifications](filters.md)
- [Pagination and Sorting](pagination.md)
- [Executing Queries](execution.md)
- [Custom Select Handlers](custom-select-handlers.md)
- Custom Filter Handlers
  - [Creating a Custom Filter Handler](#creating-a-custom-filter-handler-)
  - [Registering the Custom Filter Handler](#registering-the-custom-filter-handler-)
  - [Using the Custom Filter Handler in Projections](#using-the-custom-filter-handler-in-projections-)
- [Logging and Debug](logging.md)

## Custom Filter Handlers

In addition to custom select handlers, the `ProjectionQuery` library also allows you to define custom filter handlers. These handlers enable you to encapsulate custom filtering logic, making it reusable and expressive across different queries.

To create a custom filter handler, you need to implement the `ProjectionFilterOperatorHandler` interface, which defines how the filter expression should be constructed. Once you have implemented your custom filter handler, you can register it with the `ProjectionFilterOperatorProvider` and use it in your query.

---

### Creating a Custom Filter Handler [↑](#summary)

Here we will create a handler that performs a case-insensitive LIKE comparison.

```java
public class LikeIgnoreCaseHandler implements ProjectionFilterOperatorHandler {

  @Override
  public Predicate toPredicate(CriteriaBuilder cb, Path<?> path, Object value) {
    String pattern = value.toString().toLowerCase();

    return cb.like(
      cb.lower(path.as(String.class)),
      pattern
    );
  }
}
```

### Registering the Custom Filter Handler [↑](#summary)

After creating your custom filter handler, you need to register it with `ProjectionFilterOperatorProvider` so that it can be used in your queries.

```java
ProjectionFilterOperatorProvider
    .getInstance()
    .register("likeIgnoreCase", new LikeIgnoreCaseHandler());
```

> **Note:** You only need to register the handler once, usually during application initialization.

### Using the Custom Filter Handler in Projections [↑](#summary)

Once your custom filter handler is registered, you can use it in your queries by specifying the operator name in the filter expression.

```java
ProjectionQuery<CustomerProjection> query = ProjectionQuery
    .from(entityManager, CustomerProjection.class)
    .where("name", "likeIgnoreCase", "John%");
```

**Note:** Even if the value is passed as "John%", the handler converts the value to lowercase and applies the `LOWER` function to the `name` column, ensuring a case-insensitive comparison at the database level.

The SQL generated for this projection would look something like this:

```sql
SELECT 
    id, 
    name
FROM customer
WHERE LOWER(name) LIKE 'john%';
```

[← Previous: Custom Select Handlers](custom-select-handlers.md) · [↑ Back to top](#summary) · [Next → Logging and Debug](logging.md)
