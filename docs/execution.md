
## Summary

- [Index](index.md)
- [Defining Projection Classes](projections.md)
- [Filters and Specifications](filters.md)
- [Pagination and Sorting](pagination.md)
- Executing Queries
  - [Basic Execution](#basic-execution-)
  - [Execution with ProjectionQuery](#execution-with-projectionquery-)
  - [Paginated Execution](#paginated-execution-)
- [Custom Select Handlers](custom-select-handlers.md)
- [Custom Filter Handlers](custom-filter-handlers.md)
- [Logging and Debug](logging.md)
- [For Spring Boot users](for-springboot-users.md)

## Executing Queries

After defining your projection and applying filters, sorting, or pagination, the next step is to execute the query and retrieve the results.

> 1. For all the following examples, consider that we have a `Customer` entity and an associated projection `CustomerProjection`, as defined in the [Definition of Projection](projections.md#definition-of-projection-) section.
> 2. In the following examples, we assume a JPA context in which the `ProjectionProcessor` receives an `EntityManager` instance, responsible for executing the queries.

---

### Basic execution [↑](#summary)

To execute a query, you can use the `execute` method of the `ProjectionProcessor` class. This method accepts either a projection class or a `ProjectionQuery` instance and returns the results according to the projection definition.

```java
ProjectionProcessor processor = new ProjectionProcessor(entityManager);
List<CustomerProjection> customers = processor.execute(CustomerProjection.class);
```

In this example, the `execute` method is called with the projection class `CustomerProjection`, and it will return a list of `CustomerProjection` objects containing the projected data.

--- 
### Execution with `ProjectionQuery` [↑](#summary)

If you are using the `ProjectionQuery` class to build your query, the execution process is similar. You can pass the `ProjectionQuery` instance to the `execute` method of the `ProjectionProcessor`.

```java
ProjectionProcessor processor = new ProjectionProcessor(entityManager);

ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter("age", "greater_than", 18)
    .order("name", OrderDirection.ASC)
    .paging(0, 20)
    .distinct();

List<CustomerProjection> customers = processor.execute(query);
```

### Paginated Execution [↑](#summary)

If you want to obtain the results in paginated format, you can use the `executePageable` method of the `ProjectionProcessor`, which returns a `ProjectionPage` object containing the results and pagination information.

```java
ProjectionProcessor processor = new ProjectionProcessor(entityManager);

ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
        .fromTo(Customer.class, CustomerProjection.class)
        .filter("age", "greater_than", 18)
        .order("name", OrderDirection.ASC)
        .paging(0, 20)
        .distinct();

ProjectionPage<CustomerProjection> page = processor.executePageable(query);
```

`ProjectionPage` contains the result list along with pagination metadata, such as the total number of records, current page index, page size, and related information.

> In the case of a paginated query, `ProjectionProcessor` executes one query to count the total number of records matching the applied filters and another query to fetch the records for the requested page.

[← Previous: Pagination and Sorting](pagination.md) · [↑ Back to top](#summary) · [Next → Custom Select Handlers](custom-select-handlers.md)
