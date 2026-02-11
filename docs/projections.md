## Summary

- [Index](index.md)
- Defining Projection Classes
  - [Definition of projection](#definition-of-projection-)
  - [Projection with custom joins](#projection-with-custom-joins-)
  - [Projection using aliases for nested properties](#projection-using-aliases-for-nested-properties-)
  - [Projection using query operators](#projection-using-query-operators-)
- [Filters and Specifications](filters.md)
- [Pagination and Sorting](pagination.md)
- [Executing Queries](execution.md)
- [Custom Select Handlers](custom-select-handlers.md)
- [Custom Filter Handlers](custom-filter-handlers.md)
- [Logging and Debug](logging.md)

## Projection Classes

Projection Classes define the structure of the data that will be retrieved from the database. They act as an explicit contract for the selected fields, preventing unnecessary entity exposure and improving query clarity.

---

### Definition of projection [↑](#summary)

```java
@Projection(of = Customer.class)
public record CustomerProjection(
        @ProjectionField Long id,
        @ProjectionField String name,
        @ProjectionField("address.city.name") String city,
        @ProjectionField("address.city.state.name") String state
) { }
```

> **Note:** Projection classes can be defined as `record` or `class`, depending on your preferences and needs.

In this example, we define the projection `CustomerProjection`, associated with the entity `Customer`. This maps the fields `id`, `name`, `city`, and `state` of the entity `Customer`. The annotation `@Projection(of = Customer.class)` indicates that this projection is associated with the entity `Customer`, and the fields annotated with `@ProjectionField` specify which data should be included in the projection.

It's important to note that fields annotated with `@ProjectionField` can use expressions to access nested properties, such as `address.city.name`, allowing you to project data from complex structures without exposing the entire entity.

The SQL generated for this projection would look something like this:
```sql
select
    customer.id,
    customer.name,
    city.name as city_name,
    state.name as state_name
from customer
inner join address on customer.address = address.id
inner join city ON address.city = city.id
inner join state ON city.state = state.id
```

---

### Projection with custom joins [↑](#summary)

To specify the type of JOIN used to access related data in a projection, you can configure custom joins using the `@ProjectionJoin` annotation within the `joins` attribute of the `@Projection` annotation.

If a join is not explicitly defined, the default behavior is to use INNER JOIN for nested properties. However, when configuring a custom join for a nested property, all nested properties within that property will also use the specified JOIN type.

```java
@Projection(
        of = Customer.class,
        joins = {
                @ProjectionJoin(path = "secondaryAddress", type = JoinType.LEFT)
        }
)
public record CustomerProjection(
        @ProjectionField Long id,
        @ProjectionField String name,
        @ProjectionField("address.city.name") String city,
        @ProjectionField("secondaryAddress.city.name") String secondaryCity
) { }
```

In this example, we added a custom JOIN for the `secondaryAddress` property using a LEFT JOIN. This allows you to control the type of JOIN used to access related data in the projection.

By defining the join for `secondaryAddress` as a LEFT JOIN, all nested properties within it, such as `secondaryAddress.city.name`, will also use the LEFT JOIN to access related data.

The SQL generated for this projection would look something like this:
```sql
select
    customer.id,
    customer.name,
    city.name as city_name,
    secondary_city.name as secondary_city_name
from customer
inner join address on customer.address = address.id
inner join city on address.city = city.id
left join address secondary_address on customer.secondary_address = secondary_address.id
left join city as secondary_city on secondary_address.city = secondary_city.id
```

### Projection using aliases for nested properties [↑](#summary)

It is possible to use the `alias` attribute of the `@ProjectionJoin` annotation to define a custom name for nested properties, making it easier to access the projected data.

```java
@Projection(
        of = Customer.class,
        joins = {
                @ProjectionJoin(path = "address", alias = "mainAddress"),
                @ProjectionJoin(path = "mainAddress.city", alias = "mainCity"),
                @ProjectionJoin(path = "secondaryAddress.city", alias = "secondaryCity", type = JoinType.LEFT)
        }
)
public record CustomerProjection(
        @ProjectionField Long id,
        @ProjectionField String name,
        @ProjectionField("mainCity.name") String city,
        @ProjectionField("secondaryCity.name") String secondaryCity
) { }
```

In this example, we define custom aliases for the nested properties `address` and `address.city` as `mainAddress` and `mainCity`, respectively. The alias is used internally to resolve navigation paths, allowing for greater readability in the definition of nested properties.

It is also possible to use aliases in joins defined later, such as in `mainAddress.city` which uses the previously defined alias `mainAddress`.

You can also combine aliases with custom JOIN types, such as in `secondaryAddress.city`, which uses a LEFT JOIN.

The SQL generated for this projection would look something like this:
```sql
select
    customer.id,
    customer.name,
    main_city.name as city_name,
    secondary_city.name as secondary_city_name
from customer
inner join address as main_address on customer.address = main_address.id
inner join city as main_city on main_address.city = main_city.id
left join secondary_address on customer.secondary_address = secondary_address.id
left join city as secondary_city on secondary_address.city = secondary_city.id
```

---

### Projection using query operators [↑](#summary)

The `@ProjectionField` annotation allows you to perform operations such as counting, summing, averaging, etc., directly on the projection. To do this, simply use the `selectHandler` attribute to specify the desired query operator handler.

```java
@Projection(of = Customer.class)
public record CustomerProjection(
        @ProjectionField(value = "id", selectHandler = CountHandler.class) Long count,
        @ProjectionField Integer age
) { }
```

In this example, we use `CountHandler` to count the number of `Customer` records and project that value onto the `count` field. The `age` field is projected normally.

The SQL generated for this projection would look something like this:
```sql
select
    count(customer.id) as count,
    customer.age
from customer
group by customer.age
```

> Note that if the specified handler requires aggregation, the generated SQL includes a `GROUP BY` clause to group the results based on the projected fields.

Currently, the following aggregate selection and transformation operators are supported:
- AbsHandler
- AvgHandler
- CountHandler
- MaxHandler
- MinHandler
- SumHandler

The library is designed to be extensible. Custom handlers can be created by implementing `ProjectionSelectOperatorHandler` and registering them via `ProjectionSelectOperatorProvider`. This topic will be covered in detail in the [Custom Select Handlers](custom-select-handlers.md) section.

[← Previous: Index](index.md) · [↑ Back to top](#summary) · [Next → Filters and Specifications](filters.md)
