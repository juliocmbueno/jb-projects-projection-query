# ProjectionQuery

**ProjectionQuery** is a lightweight, type-safe, and framework-agnostic query projection library for Java.  
It provides an API for building database projections dynamically — without exposing entity models and without relying on complicated frameworks.

The project is split into multiple modules:

- **projection-core** — The core engine containing the projection API.
- **projection-spring** — Spring Data integration (optional - coming soon).
- **projection-examples** — Sample use cases and demonstration scenarios (coming soon).

---

## Motivation

In many enterprise applications, returning entire entities to the client is unnecessary and often harmful.  
ProjectionQuery addresses this by allowing developers to:

- Build **explicit projections**.
- Select **only the fields you need**.
- Reduce data transfer size.
- Avoid exposing full entity structures.
- Generate type-safe, model-independent results.

---

## Modules

### **projection-core**
The main module containing:

- `ProjectionQuery` - It serves as the entry point for the projection engine, allowing you to define filters, specifications, ordering, and pagination in a fluent way. It encapsulates all the configuration required to execute a projection.
- `ProjectionProcessor` - Core engine for executing projection queries
- `@Projection` - Indicates that the annotated class is a projection for a specific entity.
- `@ProjectionField` - Indicates that the annotated field is a projection field with optional aggregation functions.
- `@ProjectionJoin` Indicates a join to be used in a projection. Should be used when there is a need to change the join type or define an alias for the join
- Utility abstractions for type-safe projections

This module is fully independent and can be used in **any** Java application — with or without frameworks.

### **projection-spring** (coming soon)
Provides integration with Spring Data:

- Automatic projection query execution
- Seamless compatibility with `JpaRepository`
- Spring-managed projection transformers

---

##  Installation (Maven)

```xml
<dependency>
    <groupId>io.github.juliocmbueno</groupId>
    <artifactId>projection-core</artifactId>
    <version>1.0.1</version>
</dependency>
```

---
## Basic Usage

First, create a class that will represent your query.
```java
@Projection(of = Customer.class)
public record CustomerBasicData(
        @ProjectionField Long id,
        @ProjectionField String name,
        @ProjectionField("address.city.name") String city,
        @ProjectionField("address.city.state.name") String state
) { }
```

Example using a projection class:
```java
List<CustomerBasicData> customers = processor.execute(CustomerBasicData.class);
```

Example using a fully configured ProjectionQuery:
```java
ProjectionQuery<Customer, CustomerBasicData> query = ProjectionQuery.fromTo(Customer.class, CustomerBasicData.class)
    .filter("address.city.name", ProjectionFilterOperator.EQUAL, "São Paulo")
    .order("name", OrderDirection.ASC)
    .paging(0, 20)
    .distinct();

List<CustomerBasicData> customers = processor.execute(query);
```

---
## Documentation
Full Javadoc is available inside the `projection-core` module and will also be published to Maven Central once the artifacts are public.

---
## Licence
ProjectionQuery is licensed under the Apache License 2.0.

See the LICENSE file for full details.

---
## Contributing
Contributions are welcome!

Feel free to submit issues, suggestions, or pull requests.

---
## Author

Developed and maintained by Júlio Bueno.
