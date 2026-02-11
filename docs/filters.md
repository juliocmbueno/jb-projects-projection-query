## Summary

- [Index](index.md)
- [Defining Projection Classes](projections.md)
- Filters and Specifications
  - [Filter using the ProjectionFilters utility class](#filter-using-the-projectionfilters-utility-class-)
  - [Filter using the "of" method of the ProjectionFilters utility class](#filter-using-the-of-method-of-the-projectionfilters-utility-class-)
  - [Filter Composition (AND / OR)](#filter-composition-and--or-)
  - [Filter using path, operator and value](#filter-using-path-operator-and-value-)
  - [Using multiple filters](#using-multiple-filters-)
  - [Filters with the ProjectionSpecification interface](#filters-with-the-projectionspecification-interface-)
- [Pagination and Sorting](pagination.md)
- [Executing Queries](execution.md)
- [Custom Select Handlers](custom-select-handlers.md)
- [Custom Filters Handlers](custom-filters-handlers.md)
- [Logging and Debug](logging.md)

## Filters and Specifications

Filters and specifications define the criteria that determine which records will be returned by the query. They allow you to construct WHERE clauses in a declarative way, decoupled from the entity.

Both filters and specifications can be applied through the `ProjectionQuery` class. It is through this class that you can build complex queries, combining projections, filters, specifications, sorting, and pagination.

> For all the following examples, consider that we have a `Customer` entity and an associated projection `CustomerProjection`, as defined in the [Definition of Projection](projections.md#definition-of-projection-) section.

---

### Filter using the `ProjectionFilters` utility class. [↑](#summary)

The `ProjectionFilters` class provides utility methods for creating filters in a more fluid and readable way. It supports constructing filters using typed operators or operators represented as `String`, which is especially useful in dynamic scenarios.

It also centralizes all filtering operations, simplifying the filter creation process.

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter(
        ProjectionFilters.like("name", "John%")
    );
```

In this example, we are using the `like` method of the `ProjectionFilters` class to create a filter that selects customers whose name begins with "John".

The SQL generated for this projection would look something like this:

```sql
select
    id,
    name
from customer
where name like 'John%'
```

Currently the `ProjectionFilters` class supports the following filtering operators:
- [and / or](#filter-composition-and--or-)
- equal
- notEqual
- greaterThan
- greaterThanOrEqual
- lessThan
- lessThanOrEqual
- like
- in
- notIn
- between

---

### Filter using the `of` method of the `ProjectionFilters` utility class. [↑](#summary)

It's also possible to create filters using the `of` method of the `ProjectionFilters` class. This can be useful for scenarios where operators are defined dynamically, such as in REST APIs.

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter(
        ProjectionFilters.of("name", ProjectionFilterOperator.LIKE, "John%")
    );
```

If you prefer, you can also use the filtering operator as a string.

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter(
        ProjectionFilters.of("name", 'like', "John%")
    );
```

Note that if the filtering operator is passed as a string, the library will map that string to the corresponding operator, provided it is registered in the `ProjectionFilterOperatorProvider`.

The SQL generated for this projection would look something like this:

```sql
select
    id,
    name
from customer
where name like 'John%'
```

The library was designed to be extensible. Custom filter operators can be created by implementing `ProjectionFilterOperatorHandler` and registering them through the `ProjectionFilterOperatorProvider`.. This topic will be covered in detail in the [Custom Filters Handlers](custom-filters-handlers.md) section.

---

### Filter Composition (AND / OR) [↑](#summary)

Filter composition is an essential feature for creating complex queries. The `ProjectionFilters` class supports filter composition using the `and` and `or` methods, allowing you to combine multiple filtering conditions.

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter(
        ProjectionFilters.or(
            ProjectionFilters.equal("mainAddress.city.id", 1),
            ProjectionFilters.equal("secondaryAddress.city.id", 2)
        )
    );
```

In this example, we are creating a filter that selects customers whose primary address city has ID 1 or whose secondary address city has ID 2.

The SQL generated for this projection would look something like this:
```sql
select
    customer.id,
    customer.name
from customer
inner join address on customer.address = address.id
left join address secondary_address on customer.secondary_address = secondary_address.id
where (
      address.city = 1
      or secondary_address.city = 2
)
```

---

### Filter using path, operator and value [↑](#summary)

It is also possible to create filters without using the `ProjectionFilters` class, by directly passing the property path, the filtering operator, and the value to the `filter` method of the `ProjectionQuery` class.

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter("name", ProjectionFilterOperator.LIKE, "John%");
```

Or using the filtering operator as a string:

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter("name", 'like', "John%");
```

---

### Using multiple filters [↑](#summary)

Multiple filters can be applied to a query simply by chaining calls to the `filter` method of the `ProjectionQuery` class.

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter("name", "like", "John%")
    .filter("age", "greater_than", 30);
```

Another way is to pass multiple filters as varargs to the `filter` method of the `ProjectionQuery` class.

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter(
        ProjectionFilters.like("name", "John%"),
        ProjectionFilters.greaterThan("age", 30)
    );
```

In both examples, the SQL generated for this projection would look something like this:

```sql
select
    id,
    name
from customer
where 
    name like 'John%'
    and age > 30
```

### Filters with the `ProjectionSpecification` interface [↑](#summary)

The `ProjectionSpecification` interface is a powerful way to encapsulate filtering logic in a separate class. It allows you to define complex and reusable filtering criteria, promoting better code organization.

> ProjectionSpecification works similarly to the `Specification` interface in Spring Data JPA, but is designed to be independent of any specific framework, allowing it to be used in various contexts.

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
        .fromTo(Customer.class, CustomerProjection.class)
        .filter(
                (criteriaBuilder, query, root, pathResolver) ->
                        criteriaBuilder.ge(pathResolver.resolve(root, "age"), 18)
        );
```

In this example, we are using a lambda expression to implement the `ProjectionSpecification` interface. The expression receives a `CriteriaBuilder`, a `CriteriaQuery`, the query root, and a `PathResolver` to resolve property paths. The filtering logic is defined within the lambda expression, allowing for the flexible creation of complex filters.

The SQL generated for this projection would look something like this:
```sql
select
    id,
    name
from customer
where age >= 18
```

> An important detail is that `ProjectionSpecification` is always applied before filters added via `filter(...)`. This ensures that the main filtering logic is consolidated before the application of complementary criteria.

So, considering the following example:

```java
ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
        .fromTo(Customer.class, CustomerProjection.class)
        .filter(ProjectionFilters.like("name", "John%"))
        .filter(
                (criteriaBuilder, query, root, pathResolver) ->
                        criteriaBuilder.ge(pathResolver.resolve(root, "age"), 18)
        );
```

Even if the specification is defined after the name filter, the library will ensure that the specification is applied first, resulting in an SQL query similar to this:

```sql
select
    id,
    name
from customer
where 
    age >= 18
    and name like 'John%'
```

[← Previous: Defining Projection Classes](projections.md) · [↑ Back to top](#summary) · [Next → Pagination and Sorting](pagination.md)
