## Summary

- [Index](index.md)
- [Defining Projection Classes](projections.md)
- [Filters and Specifications](filters.md)
- [Pagination and Sorting](pagination.md)
- [Executing Queries](execution.md)
- [Custom Select Handlers](custom-select-handlers.md)
- [Custom Filter Handlers](custom-filter-handlers.md)
- [Logging](logging.md)
- [Debug](debug.md)
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
    private final ProjectionProcessorDebug projectionProcessorDebug;
    private final ProjectionFilterOperatorProvider filterProvider;
    private final ProjectionSelectOperatorProvider selectOperatorProvider;

    public MyService(
            ProjectionProcessor projectionProcessor,
            ProjectionProcessorDebug projectionProcessorDebug,
            ProjectionFilterOperatorProvider filterProvider,
            ProjectionSelectOperatorProvider selectOperatorProvider
    ){
        this.projectionProcessor = projectionProcessor;
        this.projectionProcessorDebug = projectionProcessorDebug;
        this.filterProvider = filterProvider;
        this.selectOperatorProvider = selectOperatorProvider;
    }
}
```

---

[← Debug](debug.md) · [↑ Back to top](#summary)
