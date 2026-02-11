## Summary

- [Index](index.md)
- [Defining Projection Classes](projections.md)
- [Filters and Specifications](filters.md)
- Pagination and Sorting
  - [Pagination](#pagination-)
  - [Sorting](#sorting-)
- [Executing Queries](execution.md)
- [Custom Select Handlers](custom-select-handlers.md)
- [Custom Filter Handlers](custom-filters-handlers.md)
- [Logging and Debug](logging.md)

## Pagination and Sorting

Both pagination and sorting are essential features for controlling the amount of data returned by a query and how that data is organized. `ProjectionQuery` provides a fluent API to apply both features in a simple and expressive way.

> For all the following examples, consider that we have a `Customer` entity and an associated projection `CustomerProjection`, as defined in the [Definition of Projection](projections.md#definition-of-projection-) section.

---

### Pagination [↑](#summary)

To apply pagination to a query, you can use the `paging` method of `ProjectionQuery`. The `paging` method takes two parameters: `first`, which indicates the index of the first record, and `size`, which indicates the number of records to be returned.

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .paging(10, 20);
```

In this example, with `paging(10, 20)`, the query will return 20 records starting from index 10 (i.e., from index 10 to 29).

```sql
select
    id,
    name
from customer
limit 20 offset 10
```

---

### Sorting [↑](#summary)

Sorting can be applied using the `order` method of `ProjectionQuery`. The `order` method takes the property path and the sorting direction (`OrderDirection.ASC` or `OrderDirection.DESC`).

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .order("age", OrderDirection.DESC)
    .order("name", OrderDirection.ASC);
```

In this example, the query will sort the results first by age in descending order and, in case of a tie, by name in ascending order. The SQL generated for this projection would look something like this:

```sql
select
    id,
    name,
    age
from customer
order by 
    age desc, 
    name asc
```

[← Previous: Filters and Specifications](filters.md) · [↑ Back to top](#summary) · [Next → Executing Queries](execution.md)
