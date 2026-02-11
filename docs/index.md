## Summary

- Index
  - [ProjectionQuery](#projectionquery-)
  - [Installation (Maven)](#installation-maven-)
  - [Basic Usage](#basic-usage-)
- [Defining Projection Classes](projections.md)
- [Filters and Specifications](filters.md)
- [Pagination and Sorting](pagination.md)
- [Executing Queries](execution.md)
- [Custom Select Handlers](custom-select-handlers.md)
- [Custom Filter Handlers](custom-filter-handlers.md)
- [Logging and Debug](logging.md)
- [For Spring Boot users](for-springboot-users.md)

# ProjectionQuery [↑](#summary)

**ProjectionQuery** is a lightweight, type-safe, and framework-agnostic query projection library for Java.  
It provides an API for building database projections dynamically — without exposing entity models and without relying on complicated frameworks.

The project is split into multiple modules:

- **projection-core** — The core engine containing the projection API.
- **projection-spring** — Spring Data integration.
- **projection-examples** — Sample use cases and demonstration scenarios.

Project Repository: [https://github.com/juliocmbueno/jb-projects-projection-query](https://github.com/juliocmbueno/jb-projects-projection-query)

---

##  Installation (Maven) [↑](#summary)

> In the following installation, be sure to use the latest version.

### standalone usage
```xml
<dependency>
    <groupId>io.github.juliocmbueno</groupId>
    <artifactId>projection-core</artifactId>
    <version>x.x.x</version>
</dependency>
```

### spring usage
```xml
<dependency>
    <groupId>io.github.juliocmbueno</groupId>
    <artifactId>projection-spring-data</artifactId>
    <version>x.x.x</version>
</dependency>
```

---
## Basic Usage [↑](#summary)

### Defining a projection
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

> In the following examples, we assume a JPA context in which the `ProjectionProcessor` receives an `EntityManager` instance, responsible for executing the queries.

**1. Example using a projection class:**
```java
ProjectionProcessor processor = new ProjectionProcessor(entityManager);
List<CustomerBasicData> customers = processor.execute(CustomerBasicData.class);
```

**2. Example using a fully configured ProjectionQuery:**
```java
ProjectionProcessor processor = new ProjectionProcessor(entityManager);

ProjectionQuery<Customer, CustomerBasicData> query = ProjectionQuery
    .fromTo(Customer.class, CustomerBasicData.class)
    .filter("address.city.name", ProjectionFilterOperator.EQUAL, "São Paulo")
    .order("name", OrderDirection.ASC)
    .paging(0, 20)
    .distinct();

List<CustomerBasicData> customers = processor.execute(query);
```

**3. Example using ProjectionPage result:**
```java
ProjectionProcessor processor = new ProjectionProcessor(entityManager);

ProjectionQuery<Customer, CustomerBasicData> query = ProjectionQuery
    .fromTo(Customer.class, CustomerBasicData.class)
    .paging(0, 20);

ProjectionPage<CustomerBasicData> page = processor.executePageable(query);
```
---

> This is a basic introduction. More advanced topics such as 
> specifications, complex filters, sorting, pagination, and integration with Spring Data JPA
> are covered in the next sections.

[↑ Back to top](#summary) · [Next → Defining Projection Classes](projections.md)
