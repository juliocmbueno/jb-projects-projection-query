## Summary

- [Index](index.md)
- Defining Projection Classes
  - [Definição de projeção](#definição-de-projeção-)
  - [Projeção com joins personalizados](#projeção-com-joins-personalizados-)
  - [Projeção utilizando alias para propriedades aninhadas](#projeção-utilizando-alias-para-propriedades-aninhadas-)
- [Executing Queries](execution.md)
- [Filters and Specifications](filters.md)
- [Pagination and Sorting](pagination.md)
- [Custom Select Handlers](filters.md#custom-select-handlers-)
- [Logging and Debug](logging.md)

## Projection Classes

As classes de projeção funcionam como uma representação visual dos dados que você deseja consultar. Elas são definidas usando a anotação `@Projection` e seus campos são anotados com `@ProjectionField` para indicar quais dados devem ser incluídos na projeção.


[← Previous: Index](index.md) · [↑ Back to top](#summary) · [Next → Executing Queries](execution.md)
