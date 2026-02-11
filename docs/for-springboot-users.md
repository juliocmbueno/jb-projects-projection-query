## Summary

- [Index](index.md)
- [Defining Projection Classes](projections.md)
- [Filters and Specifications](filters.md)
- [Pagination and Sorting](pagination.md)
- [Executing Queries](execution.md)
- [Custom Select Handlers](custom-select-handlers.md)
- [Custom Filter Handlers](custom-filter-handlers.md)
- [Logging and Debug](logging.md)
- For Spring Boot users

## For Spring Boot users

The `projection-spring-data` module provides seamless integration with Spring Data repositories.

The following components can be managed by Spring:

- ProjectionProcessor
- ProjectionFilterOperatorProvider
- ProjectionSelectOperatorProvider

> **Note:** The `projection-spring-data` module automatically configures these components, so no additional manual setup is required.

Example:

```java
@Service
public class MyService {

    private final ProjectionProcessor projectionProcessor;
    private final ProjectionFilterOperatorProvider filterProvider;
    private final ProjectionSelectOperatorProvider selectOperatorProvider;

    public MyService(
            ProjectionProcessor projectionProcessor,
            ProjectionFilterOperatorProvider filterProvider,
            ProjectionSelectOperatorProvider selectOperatorProvider
    ){
        this.projectionProcessor = projectionProcessor;
        this.filterProvider = filterProvider;
        this.selectOperatorProvider = selectOperatorProvider;
    }
}
```

[← Logging and Debug](logging.md) · [↑ Back to top](#summary)
