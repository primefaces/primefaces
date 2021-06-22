# Column

Column is an extended version of the standard column used by various components like datatable,
treetable and more.

## Info

| Name | Value |
| --- | --- |
| Tag | column
| Component Class | org.primefaces.component.column.Column
| Component Type | org.primefaces.component.Column
| Component Family | org.primefaces.component |

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| ariaHeaderText | null | String | Label to read by screen readers, when not specified headerText is used.
| caseSensitiveSort         | false              | Boolean          | Case sensitivity for sorting, insensitive by default.
| colspan | 1 | Integer | Defines the number of columns the column spans.
| displayPriority           | 0                  | Integer          | Defines the display priority, in which order the columns should be displayed. Lower values have more priority.
| draggable | true | Boolean | Defines if column is draggable if draggableColumns is set. Default true.
| exportable | true | Boolean | Defines if the column should be exported by dataexporter.
| exportFooterValue | null | String | Defines if the footer value of column to be exported.
| exportFunction | null | MethodExpr | Custom pluggable exportFunction for data exporter.
| exportHeaderValue | null | String | Defines if the header value of column to be exported.
| exportValue | null | String | Defines the value of the cell to be exported if you want something other than the cell contents or exportFunction.
| field | null | String | Name of the field associated to bean "var". If not specified, filterBy-sortBy values are used to identify the field name.
| filterable | true | Boolean | Boolean value to mark column as filterable.
| filterBy | null | ValueExpr | ValueExpression to be used for filtering.
| filterFunction | null | MethodExpr | Custom implementation to filter a value against a constraint.
| filterMatchMode | startsWith | String | Match mode for filtering.
| filterMaxLength | null | Integer | Maximum number of characters for an input filter.
| filterOptions | null | Object | A collection of selectitems for filter dropdown.
| filterPosition | bottom | String | Location of the column filter with respect to header content. Options are 'bottom'(default) and 'top'.
| filterStyle | null | String | Inline style of the filter element
| filterStyleClass | null | String | Style class of the filter element
| filterValue | null | Object | Value of the filter field.
| footerText | null | String | Shortcut for footer facet.
| groupRow | false | Boolean | Speficies whether to group rows based on the column data.
| headerText | null | String | Shortcut for header facet.
| nullSortOrder             | 1                  | Integer          | Defines where the null values are placed in ascending sort order. Default value is "1"
| resizable | true | Boolean | Specifies resizable feature at column level. Datatable's resizableColumns must be enabled to use this option.
| responsivePriority | 0 | Integer | Responsive priority of the column, lower values have more priority.
| rowspan | 1 | Integer | Defines the number of rows the column spans.
| selectionMode | null | String | Enables selection mode.
| selectRow | true | Boolean | Whether clicking the column selects the row when parent component has row selection enabled, default is true.
| sortable | true | Boolean | Boolean value to mark column as sortable.
| sortBy | null | ValueExpr | ValueExpression to be used for sorting.
| sortFunction              | null               | MethodExpression | Custom pluggable sortFunction.
| sortFunction | null | MethodExpr | Custom pluggable sortFunction.
| sortOrder                 | asc                | String           | Sets sorting order in 'single' sortMode. Default is "ascending"
| sortPriority              | Integer.MAX_VALUE  | Integer          | Sets default sorting priority over the other columns. Lower values have more priority.
| style | null | String | Inline style of the column.
| styleClass | null | String | Style class of the column.
| toggleable | true | Boolean | Defines if column is toggleable by columnToggler component. Default value is true and a false value marks the column as static.
| visible | true | Boolean | Controls the visibilty of the column.
| width | null | String | The width of the column ('px' as default if no length unit is defined)

## Getting Started with Column
As column is a reused component, see documentation of components that use a column.

## Note
Not all attributes of column are implemented by the components that utilize column.
