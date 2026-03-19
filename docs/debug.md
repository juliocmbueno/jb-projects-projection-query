## Summary

- [Index](index.md)
- [Defining Projection Classes](projections.md)
- [Filters and Specifications](filters.md)
- [Pagination and Sorting](pagination.md)
- [Executing Queries](execution.md)
- [Custom Select Handlers](custom-select-handlers.md)
- [Custom Filter Handlers](custom-filter-handlers.md)
- [Logging](logging.md)
- Debug
  - [SQL Preview](#sql-preview-)
  - [Two Operating Modes](#two-operating-modes-)
  - [Enhanced Mode (Recommended)](#enhanced-mode-recommended-)
  - [Basic Mode (Fallback)](#basic-mode-fallback-)
  - [Comparison: Enhanced vs Basic Mode](#comparison-enhanced-vs-basic-mode-)
  - [Best Practices](#best-practices-)
- [For Spring Boot users](for-springboot-users.md)

## Debug

The debug utilities provide tools for analyzing and troubleshooting queries during development. The primary feature is **SQL Preview**, which allows you to see the exact SQL that will be generated without executing the query against the database.

> **Important:** Debug utilities are intended for development and debugging purposes only. Do not use in production code paths or performance-critical sections.

---

### SQL Preview [↑](#summary)

SQL Preview allows you to inspect the SQL query that ProjectionQuery will generate before actually executing it. This is invaluable for:

- **Debugging** - Verify query correctness
- **Learning** - Understand how ProjectionQuery translates to SQL
- **Testing** - Validate SQL structure in automated tests
- **Documentation** - Generate SQL examples

To use SQL Preview, create a `ProjectionProcessorDebug` instance with your `EntityManagerFactory`:

```java
EntityManagerFactory entityManagerFactory = // ... your EntityManagerFactory

ProjectionProcessorDebug debug = new ProjectionProcessorDebug(entityManagerFactory);

ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter("age", ProjectionFilterOperator.GREATER_THAN, 18)
    .order("name", OrderDirection.ASC)
    .paging(0, 20);

String sql = debug.previewSQL(query);
System.out.println(sql);
```

**Output:**
```sql
SELECT
    c1_0.id,
    c1_0.name,
    c1_0.age
FROM
    customer c1_0
WHERE
    c1_0.age > ?
ORDER BY
    c1_0.name
LIMIT ?
```

---

### Two Operating Modes [↑](#summary)

SQL Preview operates in two modes, automatically detecting which one to use based on available dependencies:

1. **Enhanced Mode** - Zero-cost SQL extraction using Hypersistence Utils
2. **Basic Mode** - Fallback mode that executes query with LIMIT 1

The mode is selected automatically at runtime. If Hypersistence Utils is available on the classpath, Enhanced Mode is used. Otherwise, Basic Mode is used as fallback.

---

### Enhanced Mode (Recommended) [↑](#summary)

Enhanced Mode provides the best experience by extracting SQL directly from Hibernate's query compilation without executing any database operations.

#### Setup

Add this **optional** dependency to your project:

**Maven:**
```xml
<dependency>
    <groupId>io.hypersistence</groupId>
    <artifactId>hypersistence-utils-hibernate-63</artifactId>
    <version>3.15.2</version>
</dependency>
```

> **Note:** Choose the correct version for your Hibernate:
> - Hibernate 6.3+: `hypersistence-utils-hibernate-63`
> - Hibernate 6.0-6.2: `hypersistence-utils-hibernate-60`
> - Hibernate 5.x: `hypersistence-utils-hibernate-55`

#### Benefits

- ✅ **Zero database access** - No queries executed
- ✅ **No transaction required** - Works without active transaction
- ✅ **Instant results** - Typically ~0.1ms per preview
- ✅ **Safe for any environment** - Can be used anywhere
- ✅ **Returns formatted SQL** - Ready to read and analyze

#### Example

```java
ProjectionProcessorDebug debug = new ProjectionProcessorDebug(entityManagerFactory);

ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter("status", ProjectionFilterOperator.EQUAL, "ACTIVE")
    .filter("address.city.name", ProjectionFilterOperator.LIKE, "São%");

String sql = debug.previewSQL(query);
System.out.println(sql);
```

**Output:**
```sql
SELECT
    c1_0.id,
    c1_0.name,
    c1_0.status
FROM
    customer c1_0
INNER JOIN
    address a1_0 ON c1_0.address_id=a1_0.id
INNER JOIN
    city c2_0 ON a1_0.city_id=c2_0.id
WHERE
    c1_0.status=?
    AND c2_0.name LIKE ?
```

---

### Basic Mode (Fallback) [↑](#summary)

Basic Mode is used when Hypersistence Utils is not available. It works by executing the query with `LIMIT 1` to force Hibernate to compile and log the SQL statement.

#### Setup

Enable SQL logging in your application configuration:

**application.properties:**
```properties
# Show SQL statements
logging.level.org.hibernate.SQL=DEBUG

# Format SQL for readability (optional)
spring.jpa.properties.hibernate.format_sql=true
```

**application.yml:**
```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG

spring:
  jpa:
    properties:
      hibernate:
        format_sql: true
```

#### Characteristics

- Executes query with `LIMIT 1`
- Fetches exactly 1 row (or 0 if no data)
- Requires active database connection
- SQL appears in logs
- Returns informational message

#### Example

```java
ProjectionProcessorDebug debug = new ProjectionProcessorDebug(entityManagerFactory);

ProjectionQuery<Customer, CustomerProjection> query = ProjectionQuery
    .fromTo(Customer.class, CustomerProjection.class)
    .filter("age", ProjectionFilterOperator.GREATER_THAN, 18);

String message = debug.previewSQL(query);
System.out.println(message);
```

**Console Output:**

The SQL will appear in your logs:
```
Hibernate: 
    select
        c1_0.id,
        c1_0.name,
        c1_0.age 
    from
        customer c1_0 
    where
        c1_0.age>? 
    limit ?
```

And the method returns:
```
SQL preview generated via logging

┌───────────────────────────────────────────────────────────────┐
│  Current Mode: Query Execution (LIMIT 1)                      │
│  • Fetches 1 row from database                                │
│  • Minimal performance impact                                 │
│  • Requires active database connection                        │
├───────────────────────────────────────────────────────────────┤
│  For Zero-Cost Preview:                                       │
│  Add Hypersistence Utils dependency                           │
│                                                               │
│  Note: check compatible version                               │
│  <dependency>                                                 │
│    <groupId>io.hypersistence</groupId>                        │
│    <artifactId>hypersistence-utils-hibernate-63</artifactId>  │
│    <version>3.15.2</version>                                  │
│  </dependency>                                                │
├───────────────────────────────────────────────────────────────┤
│  To See SQL in Logs:                                          │
│  logging.level.org.hibernate.SQL=DEBUG                        │
│  or                                                           │
│  spring.jpa.properties.hibernate.format_sql=true              │
└───────────────────────────────────────────────────────────────┘
```

---


### Comparison: Enhanced vs Basic Mode [↑](#summary)

| Feature | Enhanced Mode | Basic Mode |
|---------|--------------|------------|
| **Dependency** | Hypersistence Utils | None (built-in) |
| **Database Access** | ❌ No | ✅ Yes (1 row) |
| **Transaction Required** | ❌ No | ✅ Yes |
| **Performance** | ~0.1ms | Query execution time |
| **Output Format** | SQL string | Logs + message |
| **Use in CI/CD** | ✅ Recommended | ⚠️ Requires DB |
| **Production Safe** | ✅ Yes | ⚠️ Caution |

---

### Best Practices [↑](#summary)

✅ **DO:**
- Use in development and debugging
- Add Hypersistence Utils for best experience
- Preview SQL before executing complex queries
- Use for test assertions
- Generate documentation examples

❌ **DON'T:**
- Use in production code paths
- Use in performance-critical sections
- Rely on Basic Mode in CI/CD (add Hypersistence Utils)
- Execute sensitive queries for preview
- Use as query validation (use test database)

---

[← Previous: Logging](logging.md) · [↑ Back to top](#summary) · [Next → For Spring Boot users](for-springboot-users.md)
