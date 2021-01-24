# Columns

Columns is used by datatable to create columns dynamically.

## Info

| Name | Value |
| --- | --- |
| Tag | columns
| Component Class | org.primefaces.component.column.Columns
| Component Type | org.primefaces.component.Columns
| Component Family | org.primefaces.component |

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| value | null | Object | Data to represent columns.
| var | null | String | Name of iterator to access a column.
| style | null | String | Inline style of the column.
| styleClass | null | String | Style class of the column.
| sortBy | null | ValueExpr | ValueExpression to be used for sorting.
| sortFunction | null | MethodExpr | Custom pluggable sortFunction.
| filterBy | null | ValueExpr | ValueExpression to be used for filtering.
| filterStyle | null | String | Inline style of the filter element
| filterStyleClass | null | String | Style class of the filter element
| filterOptions | null | Object | A collection of selectitems for filter dropdown.
| filterMatchMode | startsWith | String | Match mode for filtering.
| rowspan | 1 | Integer | Defines the number of rows the column spans.
| colspan | 1 | Integer | Defines the number of columns the column spans.
| headerText | null | String | Shortcut for header facet.
| footerText | null | String | Shortcut for footer facet.
| filterMaxLength | null | Integer | Maximum number of characters for an input filter.
| resizable | true | Boolean | Specifies resizable feature at column level. Datatable's resizableColumns must be enabled to use this option.
| width | null | String | Width in pixels or percentage.
| exportable | true | Boolean | Defines if the column should be exported by dataexporter.
| columnIndexVar | null | String | Name of iterator to refer each index.
| filterValue | null | Object | Value of the filter field.
| toggleable | true | Boolean | Defines if columns are toggleable by columnToggler component. Default value is true and a false value marks the column as static.
| draggable | true | Boolean | Defines if columns are draggable if draggableColumns is set. Default true.
| filterFunction | null | MethodExpr | Custom implementation to filter a value against a constraint.
| field | null | String | Name of the field associated to bean "var". If not specified, filterBy-sortBy values are used to identify the field name.
| responsivePriority | 0 | Integer | Responsive rriority of the column, lower values have more priority.
| sortable | true | Boolean | Boolean value to mark column as sortable.
| filterable | true | Boolean | Boolean value to mark column as filterable.
| visible | true | Boolean | Controls the visibilty of the column.
| selectRow | true | Boolean | Whether clicking the column selects the row when parent component has row selection enabled, default is true.
| ariaHeaderText | null | String | Label to read by screen readers, when not specified headerText is used.
| exportFunction | null | MethodExpression | Custom pluggable exportFunction.
| exportValue | null | String | Defines the value of the cell to be exported if something other than the cell contents or exportFunction.
| groupRow | false | Boolean | Speficies whether to group rows based on the column data.
| exportHeaderValue | null | String | Defines if the header value of column to be exported.
| exportFooterValue | null | String | Defines if the footer value of column to be exported.
| nullSortOrder             | 1                  | Integer          | Defines where the null values are placed in ascending sort order. Default value is "1"
| sortOrder                 | asc                | String           | Sets sorting order in 'single' sortMode. Default is "ascending"
| sortFunction              | null               | MethodExpression | Custom pluggable sortFunction.
| sortPriority              | Integer.MAX_VALUE  | Integer          | Sets default sorting priority over the other columns. Lower values have more priority.
| caseSensitiveSort         | false              | Boolean          | Case sensitivity for sorting, insensitive by default.
| displayPriority           | 0                  | Integer          | Defines the display priority, in which order the columns should be displayed. Lower values have more priority.

## Getting Started with Columns
See dynamic columns section in datatable documentation for detailed information.