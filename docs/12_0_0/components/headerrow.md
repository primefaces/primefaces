# HeaderRow

HeaderRow is a helper component of datatable used for dynamic grouping.

## Info

| Name | Value |
| --- | --- |
| Tag | headerRow
| Component Class | org.primefaces.component.headerrow.HeaderRow
| Component Type | org.primefaces.component.HeaderRow
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.HeaderRowRenderer
| Renderer Class | org.primefaces.component.headerrow.HeaderRowRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
expanded | true | Boolean | Boolean value to specify whether the row group will be rendered expanded or closed.
expandable       | false              | Boolean          | Makes row groups toggleable, default is false.
field | null | String | Name of the field associated to bean "var". If not specified, groupBy value is used to identify the field name.
style | null | String | Inline style of the column.
styleClass | null | String | Style class of the column.
groupBy | null | ValueExpr | ValueExpression to be used for sorting.
rowspan | null | Integer | Defines the number of rows the column spans.
colspan | null | Integer | Defines the number of columns the column spans.

## Getting started with HeaderRow
See DataTable section for more information.