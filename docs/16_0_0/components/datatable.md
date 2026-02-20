# DataTable

DataTable displays data in tabular format.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.DataTable-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | dataTable
| Component Class | org.primefaces.component.datatable.DataTable
| Component Type | org.primefaces.component.DataTable
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DataTableRenderer
| Renderer Class | org.primefaces.component.datatable.DataTableRenderer

## Attributes

| Name                      | Default            | Type             | Description
|---------------------------|--------------------| ---------------- | ------------------ |
| allowUnsorting            | false              | Boolean          | Defines whether columns are allowed to be unsorted. Default is false.
| ariaRowLabel              | null               | String           | Label to read by screen readers on checkbox selection.
| binding                   | null               | Object           | An el expression that maps to a server side UIComponent instance in a backing bean
| cellEditMode              | eager              | String           | Defines the cell edit behavior, valid values are "eager" (default) and "lazy".
| cellNavigation            | true               | Boolean          | Enables cell navigation with the keyboard for WCAG and screen reader compliance.
| cellSeparator             | null               | String           | Separator text to use in output mode of editable cells with multiple components.
| clientCache               | false              | Boolean          | Caches the next page asynchronously, default is false.
| currentPageReportTemplate | null               | String           | Template of the currentPageReport UI.
| dataLocale                | null               | Object           | Locale to be used in features such as filtering and sorting, defaults to view locale.
| dir                       | ltr                | String           | Defines text direction, valid values are `ltr` and `rtl`.
| disableContextMenuIfEmpty | false              | Boolean          | Decides whether to disable context menu or not if a table has no records.
| draggableColumns          | false              | Boolean          | Columns can be reordered with dragdrop when enabled.
| draggableRows             | false              | Boolean          | When enabled, rows can be reordered using dragdrop.
| draggableRowsFunction     | null               | MethodExpression | Method expression to execute after dragging row.
| editInitEvent             | null               | String           | Defines a client side event to open cell on editable table.
| editMode                  | row                | String           | Defines edit mode, valid values are `row` and `cell`.
| editable                  | false              | Boolean          | Controls incell editing.
| editingRow                | false              | Boolean          | Defines if cell editors of row should be displayed as editable or not.
| emptyMessage              | No records found.  | String           | Text to display when there is no data to display. Alternative is emptyMessage facet.
| escapeText                | true               | Boolean          | Defines if headerText and footerText values on columns are escaped or not. Default is true.
| expandedRow               | false              | Boolean          | Defines if row should be rendered as expanded by default.
| exportTag                 | null               | String           | If XML data exporter in use, this allows customization of the document tag in the XML.
| exportRowTag              | null               | String           | If XML data exporter in use, this allows customization of the row tag in the XML.
| filterBy                  | null               | FilterMeta / Collection<FilterMeta> | Property to be used for default filtering. Expects a single or a collection of FilterMeta.
| filterDelay               | 300                | Integer          | Delay in milliseconds before sending an ajax filter query.
| filterEvent               | keyup              | String           | Event triggering filter for input filters. If "enter" it will only filter after ENTER key is pressed.
| filteredValue             | null               | List             | List to keep filtered data.
| filterNormalize           | false              | Boolean          | Defines if filtering would be done using normalized values (accents will be removed from characters). Default is false.
| first                     | 0                  | Integer          | Index of the first row to be displayed
| frozenColumns             | 0                  | Integer          | Number of columns to freeze from start index 0.
| frozenColumnsAlignment    | left               | String           | Defines Alignment of frozen columns, valid values are `left` (default) and `right` 
| frozenRows                | null               | Object           | Collection to display as fixed in scrollable mode.
| globalFilter              | null               | String           | Value of the global filter to use when filtering by default.
| globalFilterOnly          | false              | Boolean          | When true this will hide all column filters and allow all columns to be filtered by global filter only.
| globalFilterFunction      | null               | MethodExpression | Custom implementation to globally filter a value against a constraint.
| id                        | null               | String           | Unique identifier of the component
| lazy                      | false              | Boolean          | Controls lazy loading. In most cases this is detected automatically based on value-binding to LazyDataModel. So no need to set this explicit.
| liveResize                | false              | Boolean          | Columns are resized live in this mode without using a resize helper.
| liveScroll                | false              | Boolean          | Enables live scrolling.
| liveScrollBuffer          | 0                  | Integer          | Percentage height of the buffer between the bottom of the page and the scroll position to initiate the load for the new chunk. Value is defined in integer and default is 0.
| multiViewState            | false              | Boolean          | Whether to keep table state across views, defaults to false.
| nativeElements            | false              | Boolean          | Uses native radio-checkbox elements for row selection.
| onExpandStart             | null               | String           | Client side callback to execute before expansion.
| onRowClick                | null               | String           | Client side callback to execute after clicking row.
| pageLinks                 | 10                 | Integer          | Maximum number of page links to display.
| paginator                 | false              | Boolean          | Enables pagination.
| paginatorAlwaysVisible    | true               | Boolean          | Defines if paginator should be hidden if total data count is less than number of rows per page.
| paginatorPosition         | both               | String           | Paginator can be positioned at the "top," "bottom," or "both." Default setting is "both."
| paginatorTemplate         | null               | String           | Template of the paginator.
| reflow                    | false              | Boolean          | Reflow mode is a responsive mode to display columns as stacked depending on screen size.
| rendered                  | true               | Boolean          | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| renderEmptyFacets         | false              | Boolean          | Render facets even if their children are not rendered. Default is false. See https://github.com/primefaces/primefaces/issues/4840
| resizableColumns          | false              | Boolean          | Enables column resizing.
| resizeMode                | fit                | String           | Defines the resize behavior, valid values are "fit" (default) and expand.
| rowDragSelector           | td,span:not(.ui-c) | String           | Defines the element used to reorder rows using dragdrop. Default selector is "td,span:not(.ui-c)"
| rowEditMode               | eager              | String           | Defines the row edit behavior, valid values are "eager" (default) and "lazy".
| rowExpandMode             | new                | String           | Defines row expand mode, valid values are "single" and "multiple" (default).
| rowHover                  | false              | Boolean          | Adds hover effect to rows, default is false. Hover is always on when selection is enabled.
| rowIndexVar               | null               | String           | Name of iterator to refer each row index.
| rowKey                    | null               | String           | Unique identifier of a row. Must be defined when using selection together with non-lazy datasource (eg value-attribute bound to a instance of `java.util.List`).
| rowSelector               | null               | String           | Client side check if rowclick triggered row click event not a clickable element in row content.
| rowStatePreserved         | false              | Boolean          | Keeps state of its children on a per-row basis. Default is false.
| rowStyleClass             | null               | String           | Style class for each row.
| rows                      | 0                  | Integer          | Number of rows to display per page.
| rowsPerPageLabel          | null               | String           | Label for the rowsPerPage dropdown.
| rowsPerPageTemplate       | null               | String           | Template of the rowsPerPage dropdown.
| rowTitle                  | null               | String           | Title for each row.
| saveOnCellBlur            | true               | Boolean          | Saves the changes in cell editing on blur, when set to false changes are discarded.
| scrollHeight              | null               | Integer          | Scroll viewport height.
| scrollRows                | 0                  | Integer          | Number of rows to load on live scroll.
| scrollWidth               | null               | Integer          | Scroll viewport width.
| scrollable                | false              | Boolean          | Makes data scrollable with fixed header.
| selectAllFilteredOnly     | false              | Boolean          | When enabled, toggle select will only apply on filtered items. Default is false.
| selection                 | null               | Object           | Reference to the selection data.
| selectionRowMode          | new                | String           | Indicates how rows of a DataTable may be selected, when clicking on the row itself (not the checkbox / radiobutton from p:column). The value `new` always unselects other rows, `add` preserves the currently selected rows, and `none` disables row selection.
| selectionMode             | null               | String           | Enables row selection, valid values are "single" and "multiple". Automatically detected based on value-binding to `selection` property. So no need to set this explicit.
| selectionDisabled         | false              | Boolean          | Disables row selection when true. Example: var="xxx" selectionDisabled="#{xxx.year > 1960}"
| selectionTextDisabled     | true               | Boolean          | Disables text selection on row click.
| selectionPageOnly         | true               | Boolean          | When using a paginator and selection mode is `checkbox`, the select all checkbox in the header will select all rows on the current page if `true`, or all rows on all pages if `false`. When `false`, individual row unselections after "select all" are preserved even across page navigation. Default is `true`.
| showGridlines             | false              | Boolean          | When enabled, cell borders are displayed.
| size                      | regular            | String           | Size of the table content, valid values are "small" and "large". Leave empty for regular size.
| sortMode                  | multiple           | String           | Defines sorting mode, valid values are `single` and `multiple`.
| sortBy                    | null               | SortMeta / Collection<SortMeta> | Property to be used for default sorting. Expects a single or a collection of SortMeta.
| skipChildren              | false              | Boolean          | Ignores processing of children during lifecycle, improves performance if table only has output components.
| stickyHeader              | false              | Boolean          | Sticky header stays in window viewport during scrolling.
| stickyTopAt               | null               | String           | Selector to position on the page according to other fixing elements on the top of the table. Default is null.
| stripedRows               | false              | Boolean          | Whether to display striped rows to visually separate content.
| style                     | null               | String           | Inline style of the component.
| styleClass                | null               | String           | Style class of the component.
| summary                   | null               | String           | Summary attribute for WCAG.
| tabindex                  | null               | String           | Position of the element in the tabbing order.
| tableStyle                | null               | String           | Inline style of the table element.
| tableStyleClass           | null               | String           | Style class of the table element.
| value                     | null               | Object           | Data to display.
| var                       | null               | String           | Name of the request-scoped variable used to refer each data.
| virtualScroll             | false              | Boolean          | Loads data on demand as the scrollbar gets close to the bottom. Default is false.
| widgetVar                 | null               | String           | Name of the client side widget.
| touchable                 | null               | Boolean          | Enable touch support (if the browser supports it). Default is the global primefaces.TOUCHABLE, which can be overwritten on component level.
| partialUpdate             | true               | Boolean          | When disabled, it updates the whole table instead of updating a specific field such as body element in the client requests of the dataTable.
| showSelectAll             | true               | Boolean          | Whether to show the select all checkbox inside the column's header.

## Getting started

Here is the simplest way to get started:

```xhtml
<p:dataTable var="car" value="#{dtBasicView.cars}">

    <p:column field="id" headerText="Id" />

    <p:column field="year" headerText="Year" />

    <p:column field="brand" headerText="Brand" />

    <p:column field="color" headerText="Color" />

</p:dataTable>
```

```java
public class Car {
    private String model;
    private int year;
    private String manufacturer;
    private String color;
    ...
}
```

```java
@Named
@RequestScoped
public class CarBean {
    private List<Car> cars;

    @PostConstruct
    public void init() {
        cars = new ArrayList<Car>();
        cars.add(new Car("myModel",2005,"ManufacturerX","blue"));
        //add more cars
    }

    public List<Car> getCars() {
        return cars;
    }
}
```

In case no children are present in columns, value from `var` and `field` will be displayed.


## Data binding

We suggest you to not store your data model inside a longer-living scope like `@ViewScoped`, to avoid big sessions and serialization.

So either you use a `@RequestScoped` like in the _Getting started_, or use a `LazyDataModel` to load your data on-the-fly.   
PrimeFaces comes with 2 built-in `LazyDataModel`:
- `JPALazyDataModel`: implementation to load the data via JPA
- `DefaultLazyDataModel`: implementation which loads the data via lambda. It also performs better (up to 30%) compared to binding a simple List.


```java
@Named
@RequestScoped
public class CarBean {
    @Inject private CarService carService;

    private LazyDataModel<Car> cars;

    @PostConstruct
    public void init() {
        cars = DefaultLazyDataModel.<Car>builder()
                .valueSupplier((filterBy) -> carService.getCars())
                .rowKeyProvider(Car::getId) // required for selection
                .build();
    }

    public LazyDataModel<Car> getCars() {
        return cars;
    }
}
```

## Header and Footer
Both datatable itself and columns can have custom content in their headers and footers using header
and footer facets respectively. Alternatively for columns there are headerText and footerText
shortcuts to display simple texts.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}">
    <f:facet name="header">
        List of Cars
    </f:facet>
    <p:column>
        <f:facet name="header">
            Model
        </f:facet>
        #{car.model}
        <f:facet name="footer">
            8 digit code
        </f:facet>
    </p:column>
    <p:column headerText="Year" footerText="1960-2010">
        #{car.year}
    </p:column>
    //more columns
    <f:facet name="footer">
        In total there are #{fn:length(carBean.cars)} cars.
    </f:facet>
</p:dataTable>

```
## Pagination
DataTable has a built-in ajax based paginator that is enabled by setting paginator option to true, see
pagination section in dataGrid documentation for more information about customization options.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" paginator="true" rows="10">
    //columns
</p:dataTable>
```
Optionally enabling `clientCache` property loads the next page asynchronously so that when user
clicks the second page, data is displayed instantly from client side.

## Paginator Template
Paginator is customized using `paginatorTemplate` attribute accepting various keys of UI controls.

- FirstPageLink
- LastPageLink
- PreviousPageLink
- NextPageLink
- PageLinks
- CurrentPageReport
- RowsPerPageDropdown
- JumpToPageDropdown
- JumpToPageInput

**Note** that _{RowsPerPageDropdown}_ has its own template, options to display is provided via
rowsPerPageTemplate attribute (e.g. rowsPerPageTemplate="9,12,15").

Also _{CurrentPageReport}_ has it's own template defined with currentPageReportTemplate option.
You can use _{currentPage},{totalPages},{totalRecords},{startRecord},{endRecord}_ keyword
within currentPageReportTemplate. Default is "_{currentPage}_ of _{totalPages}_". Default UI is:

which corresponds to the following template.

- _{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink}_

Here are more examples based on different templates;

- _{CurrentPageReport} {FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}_

- _{PreviousPageLink} {CurrentPageReport} {NextPageLink}_

## Paginator Position
Paginator can be positioned using `paginatorPosition` attribute in three different locations, "top",
"bottom" or "both" (default).

## Custom Content in Paginator
Custom content can be placed inside a paginator using a facet name matching a token in the
template.

```xhtml
<p:dataTable paginatorTemplate="{CurrentPageReport} {MyContent} ...">
    <f:facet name="{MyContent}">
        //Any content here
    </f:facet>
    //...
</p:dataTable>
```

## Sorting
Defining `field` or `sortBy` attribute enables ajax based sorting on that particular column. Sorting cycles through ascending,
descending and unsorted upon clicking on the column header.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}">
    <p:column field="id" headerText="Id" />

    <p:column field="year" headerText="Year" />

    <p:column field="brand" headerText="Brand" />

    <p:column sortBy="#{i18n[car.color]}" headerText="Color" />
</p:dataTable>
```
Instead of using the default sorting algorithm which uses a java comparator, you can plug-in your
own sort method as well.

```java
public int sortByModel(Object car1, Object car2, SortMeta sortMeta) {
    //return -1, 0 , 1 if car1 is less than, equal to or greater than car2
}
```

```xhtml
<p:dataTable var="car" value="#{carBean.cars}">
    <p:column field="model" sortFunction="#{carBean.sortByModel}" headerText="Model">
        <h:outputText value="#{car.model}" />
    </p:column>
    ...more columns
</p:dataTable>
```
Multiple sorting is enabled by default. In this mode, clicking a sort column
while metakey is on adds sort column to the order group. Change attribute `sortMode` to `single` to allow only one column.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" sortMode="multiple">
    //columns
</p:dataTable>
```
DataTable can display data sorted by default, by either setting default attributes on `p:column` or using `sortBy` attribute of datatable.
Table below would be initially displayed as sorted by model.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}">
    <p:column field="model" headerText="Model" sortOrder="asc" sortPriority="1" />
    <p:column field="year" headerText="Year" />
</p:dataTable>
```

Or

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" sortBy="#{carBean.defaultSortBy}">
    <p:column field="model" headerText="Model" />
    <p:column field="year" headerText="Year" />
</p:dataTable>
```

## Filtering
Ajax based filtering is enabled by setting `field` or `filterBy` at column level and providing a list to keep the
filtered sublist. It is suggested to use a scope longer than request like `@ViewScoped` to keep the
filteredValue so that filtered list is still accessible after filtering.
(Attention: Please be aware `@SessionScoped` is not supported for this. Instead, set `multiViewState` to `true` to keep table state including filter across views.)

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" filteredValue="#{carBean.filteredCars}">
    <p:column filterBy="#{car.model}" headerText="Model">
        <h:outputText value="#{car.model}" />
    </p:column>
    ...more columns
</p:dataTable>
```

Filtering is triggered with keyup event and filter inputs can be styled using `filterStyle` ,
`filterStyleClass` attributes. In addition, `filterMatchMode` defines the built-in matcher which is `startsWith` by default.

Following is a basic filtering datatable with these options demonstrated:

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" filteredValue="#{carBean.filteredCars}" widgetVar="carsTable">

    <f:facet name="header">
        <h:outputText value="Search all fields:" />
        <h:inputText id="globalFilter" onkeyup="PrimeFaces.debounce(() => PF('carsTable').filter())" />
    </f:facet>

    <p:column field="model" headerText="Model" filterMatchMode="contains" />

    <p:column field="year" headerText="Year" footerText="startsWith" />

    <p:column field="manufacturer" headerText="Manufacturer" filterMatchMode="exact">
        <f:facet name="filter">
            <p:selectOneMenu onchange="PF('carsTable').filter()" styleClass="ui-custom-filter">
                <f:selectItem itemLabel="All" itemValue="#{null}" noSelectionOption="true" />
                <f:selectItems value="#{carBean.manufacturerOptions}" />
            </p:selectOneMenu>
        </f:facet>
    </p:column>

    <p:column field="color" headerText="Color" filterMatchMode="endsWith" />

    <p:column field="price" headerText="Price" filterMatchMode="exact" />

</p:dataTable>
```
The filter in the header acts as a global filter that searches across all fields. This is achieved by calling the client-side API method `filter()`. The input text must have the id `globalFilter`, which is a special reserved identifier for DataTable. Note the use of `PrimeFaces.debounce(() => PF('carsTable').filter())` - this debounces the filter method to prevent excessive server requests by only triggering 400ms after the last keyup event.

### Custom filters

By default, `<input type="text" />` are used as filters, but you can display your own filters using `filter` facet _(see example below)_.
When a custom filter is used, filtering needs to be called manually from a
preferred event such as `onchange="PF('carsTable').filter()"`. Also defining a converter might be
necessary if the value of the filter facet is not defined.

Please make sure that the filter is using the **same type as the column field** if you are using comparable
filter match modes (like greater than). For example, if the column field is an integer, and you would like to
add a greater than filter, make sure to convert the filter to integer as well. Do so by using the column's `converter`
attribute or adding a `f:converter` to the filter input (see example below).

In case you want to filter `LocalDateTime` or `Date` values, use the converter tag
`<f:convertDateTime type="localDateTime"/>` or `<f:convertDateTime type="date"/>` as child of the filtering `DatePicker`
component.

```xhtml
<p:dataTable id="dataTable" var="car" value="#{tableBean.carsSmall}" widgetVar="carsTable" filteredValue="#{tableBean.filteredCars}">

<p:column field="model" headerText="Model" footerText="contains" filterMatchMode="contains" />

    <p:column field="year" headerText="Year" footerText="lte" filterMatchMode="lte">
        <f:facet name="filter">
            <p:spinner onchange="PF('carsTable').filter()" min="1960" max="2010">
                <f:converter converterId="jakarta.faces.Integer" />
            </p:spinner>
        </f:facet>
        <h:outputText value="#{car.year}" />
    </p:column>

    <p:column field="manufacturer" headerText="Manufacturer" footerText="exact" filterMatchMode="exact">
        <f:facet name="filter">
            <p:selectOneMenu onchange="PF('carsTable').filter()" >
                <f:selectItems value="#{tableBean.manufacturerOptions}" />
            </p:selectOneMenu>
        </f:facet>
        <h:outputText value="#{car.manufacturer}" />
    </p:column>

    <p:column field="color" headerText="Color" footerText="in" filterMatchMode="in">
        <f:facet name="filter">
            <p:selectCheckboxMenu label="Colors" onchange="PF('carsTable').filter()">
                <f:selectItems value="#{tableBean.colors}" />
            </p:selectCheckboxMenu>
        </f:facet>
        <h:outputText value="#{car.color}" />
    </p:column>

    <p:column field="sold" headerText="Status" footerText="equals" filterMatchMode="equals">
        <f:facet name="filter">
            <p:selectOneButton onchange="PF('carsTable').filter()">
                <f:converter converterId="jakarta.faces.Boolean" />
                <f:selectItem itemLabel="All" itemValue="" />
                <f:selectItem itemLabel="Sold" itemValue="true" />
                <f:selectItem itemLabel="Sale" itemValue="false" />
            </p:selectOneButton>
        </f:facet>
        <h:outputText value="#{car.sold? 'Sold': 'Sale'}" />
    </p:column>
</p:dataTable>
```

### Constraints

`filterMatchMode` defines which built-in filtering algorithm would be used per column, valid values
for this attribute are;

- **startsWith** : Checks if column value starts with the filter value.
- **notStartsWith** : Checks if column value does not start with the filter value.
- **endsWith** : Checks if column value ends with the filter value.
- **notEndsWith** : Checks if column value does not end with the filter value.
- **contains** : Checks if column value contains the filter value.
- **notContains** : Checks if column value does not contain the filter value.
- **exact** : Checks if string representations of column value and filter value are the same.
- **notExact** : Checks if string representations of column value and filter value are not the same.
- **lt** : Checks if column value is less than the filter value.
- **lte** : Checks if column value is less than or equals the filter value.
- **gt** : Checks if column value is greater than the filter value.
- **gte** : Checks if column value is greater than or equals the filter value.
- **equals** : Checks if column value equals the filter value.
- **notEquals** : Checks if column value does not equal the filter value.
- **in** : Checks if column value is in the collection of the filter value.
- **notIn** : Checks if column value is not in the collection of the filter value.
- **between** : Checks if column value is within a provided range (`p:datePicker` offers this functionality).
- **notBetween** : Checks if column value is not within a provided range (`p:datePicker` offers this functionality).

In case the built-in methods do not suffice, custom filtering can be implemented using
filterFunction approach.

```xhtml
<p:column filterBy="#{car.price}" filterFunction="#{tableBean.filterByPrice}">
    <h:outputText value="#{car.price}">
        <f:convertNumber currencySymbol="$" type="currency"/>
    </h:outputText>
</p:column>
```
`filterFunction` should be a method with three parameters; column value, filter value and locale.
Return value is a boolean, true accepts the value and false rejects it.

```java
public boolean filterByPrice(Object value, Object filter, Locale locale) {
    //return true or false
}
```
Locale is provided as optional in case you need to use a locale aware method like
`toLowerCase(Locale locale)`. Note that String based filters like `startsWith`, `endsWith` use
`toLowerCase()` already and `dataLocale` attribute is used to provide the locale to use when filtering.

This same principle can be applied globally by implementing a `globalFilterFunction`. It takes the
same parameters of which the first parameter is the row value.

### Default filtering

You might want your `DataTable` to be filtered by default. This can be set up with markup or programmatically. Here are two ways to go about it:

By using `Column#filterValue`:

```xhtml
<p:dataTable var="car" value="#{carBean.cars}">
    <p:column field="model" headerText="Model"  />
    <p:column field="year" headerText="Year" filterValue="20" matchMode="contains"/>
</p:dataTable>
```

Or using `DataTable#filterBy` and `FilterMeta#builder`:

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" filterBy="#{carBean.filterBy}">
    <p:column field="model" headerText="Model" />
    <p:column field="year" headerText="Year" />
</p:dataTable>
```

```java
@PostConstruct
public void init() {
    filterBy = new ArrayList<>();
    filterBy.add(FilterMeta.builder()
            .field("year")
            .filterValue(20)
            .matchMode(MatchMode.CONTAINS)
            .build());
}
```

In case of custom filters (e.g. `SelectOneMenu`), you have the possibilites to use either `Column#filterValue` or custom filter value attribute (e.g. `SelectOneMenu#value`)

## Row Selection

At first, you could implement selection by yourself by simply defining a column with a command component (e.g. commandLink/commandButton). 
Selected row(s) can be set to a server side instance by passing as a parameter or using `<f:setPropertyActionListener />`.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}">
    <p:column headerText="Actions">
        <p:commandButton value="Select" action="#{targetBean.addSelectedCar(car)}" />
    </p:column>
</p:dataTable>
```

But at this point, there is still a lot of work to be done: styling, what if you want checkbox/radiobutton selection etc.
Fortunately, PF comes with built-in features and provides several ways to select row(s) from datatable
  - Row selection (clicking on a row)
  - Column selection (clicking on a radiobutton or checkbox, rendered by PF)

### Selection with Row Click
To enable basic selection, it's important to set `selection` and `rowKey` attributes. 
In previous PF versions, you had to set `selectionMode` to define whether it's single or multiple selection: this is no longer required.
Depending `selection` value-binding, multiple selection will be used if bound to an array or a collection, single selection otherwise.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" selection="#{carBean.selectedCar}" rowKey="#{car.id}">
    ...columns
</p:dataTable>
```

In this example, `selectedCar` property is a single `Car` therefore single selection will be used (e.g. `selectionMode` will be set to `single`)

### Selection with RadioButtons or Checkboxes
Selecting a row with a radio button or a checkbox placed on each row is a common case, datatable has built-in
support for this so that you don't need to deal with low level bits. 

As explained before, `DataTable` component already knows what kind of selection should be used (e.g. single or multiple). 
If single selection, you might want to render radiobuttons, else checkboxes will best fit multiple selection. 
At this point, the only thing left is to create the "selection column" using `selectionBox` property:   

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" selection="#{carBean.selectedCars}" rowKey="#{car.id}">
    <p:column selectionBox="true"/>
    
    ...columns
</p:dataTable>
```

In this example, `selectedCars` property is a `Collection` of `Car` therefore multiple selection will be used and also, 
since there is a "selection column", checkboxes will be rendered

?> **Tip**: Use `selectionRowMode` option to customize the default behavior on row click of a
selection enabled datatable. Default value is "new" that clears previous selections, "add" mode
keeps previous selections same as selecting a row with mouse click when metakey is on and
"none" completely disables selection when clicking on the row itself.

## RowKey
RowKey should be a unique identifier from your data model and used by datatable to find the selected
rows. You must define this key by using the `rowKey` attribute.
    
!> RowKey must not contain a comma `,` as it will break row selection. See [`GitHub #8932`](https://github.com/primefaces/primefaces/issues/8932).

## Dynamic Columns
`Columns` component is used to define the columns programmatically. It requires a collection as the value:

```xhtml
<p:dataTable var="car" value="#{tableBean.cars}">
    <p:columns value="#{tableBean.columns}" var="column" headerText="#{column.header}" sortBy="#{car[column.property]}" filterBy="#{car[column.property]}">
        <h:outputText value="#{car[column.property]}" />
    </p:columns>
</p:dataTable>
```

```java
public class CarBean {
    private List<ColumnModel> columns = new ArrayList<ColumnModel>();
    private List<Car> cars;

    public CarBean() {
        populateColumns();
        cars = //populate cars;
    }
    public void populateColumns() {
        String[] columnKeys = new String[]{"model","year","color"};
        for(String columnKey : columnKeys) {
            columns.add(new ColumnModel(columnKey.toUpperCase(), columnKey));
        }
    }
    //getters and setters

    static public class ColumnModel implements Serializable {
        private String header;
        private String property;

        public ColumnModel(String header, String property) {
            this.header = header;
            this.property = property;
        }
        public String getHeader() {
            return header;
        }
        public String getProperty() {
            return property;
        }
    }
}
```

## Column Grouping
Grouping is defined by ColumnGroup component used to combine datatable header and footers.

```xhtml
<p:dataTable var="sale" value="#{carBean.sales}">
    <p:columnGroup type="header">
        <p:row>
            <p:column rowspan="3" headerText="Manufacturer" />
            <p:column colspan="4" headerText="Sales" />
        </p:row>
        <p:row>
            <p:column colspan="2" headerText="Sales Count" />
            <p:column colspan="2" headerText="Profit" />
        </p:row>
        <p:row>
            <p:column headerText="Last Year" />
            <p:column headerText="This Year" />
            <p:column headerText="Last Year" />
            <p:column headerText="This Year" />
        </p:row>
    </p:columnGroup>
    <p:column>
        #{sale.manufacturer}
    </p:column>
    <p:column>
        #{sale.lastYearProfit}%
    </p:column>
    <p:column>
        #{sale.thisYearProfit}%
    </p:column>
    <p:column>
        #{sale.lastYearSale}$
    </p:column>
    <p:column>
        #{sale.thisYearSale}$
    </p:column>
    <p:columnGroup type="footer">
        <p:row>
            <p:column colspan="3" style="text-align:right" footerText="Totals:"/>
            <p:column footerText="#{tableBean.lastYearTotal}$" />
            <p:column footerText="#{tableBean.thisYearTotal}$" />
        </p:row>
    </p:columnGroup>
</p:dataTable>
```
```java
public class CarBean {
    private List<Sale> sales;

    public CarBean() {
        sales = //create a list of BrandSale objects
    }
    public List<Sale> getSales() {
        return this.sales;
    }
}
```
For frozen columns, use `frozenHeader` , `frozenFooter` , `scrollableHeader` and `scrollableFooter` types.


## Row Grouping
Rows can be grouped in two ways, using headerRow, summaryRow components or with groupRow
attribute on a column.

!> Row Grouping does not work with Lazy Loading and LiveScroll as the grouping needs to know about all rows to properly
group the rows.

```xhtml
<p:dataTable var="car" value="#{dtRowGroupView.cars}">
    <p:headerRow field="brand" />

    <p:column field="year" headerText="Year" />

    <p:column field="color" headerText="Color" />

    <p:column field="id" headerText="Id" />

    <p:summaryRow>
        <p:column colspan="2" style="text-align:right">
            <h:outputText value="Total:" />
        </p:column>
        <p:column>
            <h:outputText value="#{dtRowGroupView.randomPrice}">
                <f:convertNumber type="currency" currencySymbol="$" />
            </h:outputText>
        </p:column>
    </p:summaryRow>
</p:dataTable>
```
Optionally rows can be made toggleable using `HeaderRow#expandable` property.

Alternative approach is using row spans where a row can group multiple rows within the same
group. To enable this method, set groupRow to true on the grouping column.

```xhtml
<p:dataTable var="car" value="#{dtRowGroupView.cars}">
    <p:column field="brand" headerText="Brand" groupRow="true" />
    <p:column field="year" headerText="Year" />
    <p:column field="color" headerText="Color" />
    <p:column field="id" headerText="Id" />
</p:dataTable>
```

## Scrolling
Scrolling makes the header-footer of the table fixed and the body part scrollable. Scrolling is
enabled using `scrollable` property and has 3 modes; x, y and x-y scrolling that are defined by
`scrollHeight` and _scrollWidth._ These two scroll attributes can be specified using integer values
indicating fixed pixels or percentages relative to the container dimensions.

```xhtml
<p:dataTable var="car" value="#{bean.data}" scrollable="true" scrollHeight="150">
    <p:column />
    //columns
</p:dataTable>
```
Simple scrolling renders all data to client whereas virtual scrolling combined with lazy loading is
useful to deal with huge data, in this case data is fetched on-demand. Set `virtualScroll` to enable this
option and provide LazyDataModel;

```xhtml
<p:dataTable var="car" value="#{bean.data}" scrollable="true" scrollHeight="150" virtualScroll="true">
    <p:column />
    //columns
</p:dataTable>
```
## Frozen Rows
Certain rows can be fixed in a scrollable table by using the `frozenRows` attribute that defines the
number of rows to freeze from the start.

```xhtml
<p:dataTable var="car" value="#{bean.data}" scrollable="true" scrollHeight="150" frozenRows="2">
    <p:column />
    //columns
</p:dataTable>
```
## Frozen Columns
Specific columns can be fixed while the rest of them remain as scrollable. `frozenColumns` defines
the number of columns to freeze.


```xhtml
<p:dataTable var="car" value="#{bean.data}" scrollable="true" scrollWidth="200" frozenColumns="2">
    <p:column />
    //columns
</p:dataTable>
```

## Frozen Columns Alignment
Frozen columns can be aligned left or right on the table. The `frozenColumnsAlignemnt` property defines 
their alignment.


```xhtml
<p:dataTable var="car" value="#{bean.data}" scrollable="true" scrollWidth="200" frozenColumns="2" 
             frozenColumnsAlignment="right">
    <p:column />
    //columns
</p:dataTable>
```

## Expandable Rows
`RowToggler` and `RowExpansion` facets are used to implement expandable rows.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}">
    <f:facet name="header">
        Expand rows to see detailed information
    </f:facet>
    <p:column>
        <p:rowToggler />
    </p:column>
    //columns
    <p:rowExpansion>
        //Detailed content of a car
    </p:rowExpansion>
</p:dataTable>
```
p:rowToggler component places an expand/collapse icon, clicking on a collapsed row loads
expanded content with ajax. If you need to display a row as expanded by default, use `expandedRow`
attribute which is evaluated before rendering of each row so value expressions are supported.
Additionally, rowExpandMode attribute defines if multiple rows can be expanded at the same time
or not, valid values are "single" and "multiple" (default).


## Editing

When it comes to incell editing there are two possible options: _row_ and _cell_

### Row Editing

Row editing is the default mode. p:cellEditor is used to define the cell editor of a 
particular column. By adding a _p:rowEditor_ component you get row controls to 
start editing and commit or cancel changes for one row.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" editable="true" editMode="row">
    <f:facet name="header">
        In-Cell Editing
    </f:facet>
    <p:column headerText="Model">
        <p:cellEditor>
            <f:facet name="output">
                <h:outputText value="#{car.model}" />
            </f:facet>
            <f:facet name="input">
                <h:inputText value="#{car.model}"/>
            </f:facet>
        </p:cellEditor>
    </p:column>
    //more columns with cell editors
    <p:column>
        <p:rowEditor />
    </p:column>
</p:dataTable>
```
When the pencil icon is clicked, the row is displayed in editable mode, 
meaning input facets are displayed and output facets are hidden. 
Clicking the tick icon only saves that particular row and the 
cancel icon reverts the changes. Both options are implemented with ajax interaction.

### Cell Editing

**WARNING**: With _cell_ edting mode _required_ attributes and bean validation annotations
do not apply. See this [discussion](https://github.com/orgs/primefaces/discussions/1766)

_p:cellEditor_ is again used to define the cell editor of a particular column. In contrast
to _row_ editing there is no _p:rowEditor_ component and the _editMode_ attribute must
be set to _cell_.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" editable="true" editMode="cell">
    <f:facet name="header">
        In-Cell Editing
    </f:facet>
    <p:column headerText="Model">
        <p:cellEditor>
            <f:facet name="output">
                <h:outputText value="#{car.model}" />
            </f:facet>
            <f:facet name="input">
                <h:inputText value="#{car.model}"/>
            </f:facet>
        </p:cellEditor>
    </p:column>
    //more columns with cell editors


</p:dataTable>
```


## Lazy Loading
Lazy Loading is an approach to deal with huge datasets efficiently, regular AJAX based pagination
works by rendering only a particular page but still requires all data to be loaded into memory.
Lazy loading DataTable renders a particular page similarly but also only loads the page data into memory not the whole dataset.
In order to implement this, you’d need to bind a `org.primefaces.model.LazyDataModel` as the value and
implement `load` and `count` method.
If you have selection enabled, you either need to implement `getRowData` and `getRowKey`, or pass a existing Jakarta Faces `Converter` to the constructor.


```xhtml
<p:dataTable var="car" value="#{carBean.model}" paginator="true" rows="10">
    //columns
</p:dataTable>
```
```java
public class CarBean {
    private LazyDataModel model;

    public CarBean() {
        model = new LazyDataModel() {
            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                // logical row count based on a count query taking filter into account
                return totalElements;
            }

            @Override
            public List<Car> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                // load physical data
                return requestedResultPage;
            }
        };
    }
    public LazyDataModel getModel() {
        return model;
    }
}
```

DataTable calls your load implementation whenever a paging, sorting or filtering occurs with
following parameters:

- **first**: Offset of first data to start from
- **pageSize**: Number of data to load
- **sortBy**: Active sorters map (field as key)
- **filterBy**: Active filters map (field as key).

In addition to `load` method, `count` method needs to be implemented, so that paginator can display itself
according to the logical number of rows to display.

It is suggested to use `field` attribute of column component to define the field names passed as
sortBy and filterBy, otherwise these fields would be tried to get extracted from the value
expression which is not possible in cases like composite components.

To avoid doing a separate count-statement against your datasource you may implement it like this:
(This may help to improve performance but comes at the price of keeping an eye on https://github.com/primefaces/primefaces/issues/1921.)
```java
public class CarBean {
    private LazyDataModel model;

    public CarBean() {
        model = new LazyDataModel() {
            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                return 0;
            }

            @Override
            public List<Car> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                //load physical data - your datasource (eg Spring Data or Apache Solr) has one API-call which returns page including rowCount
                org.springframework.data.domain.Page<Car> requestedResultPage = carRepository.find...;

                // afterwards set rowCount
                setRowCount(requestedResultPage.getTotalElements());

                // return page data
                return requestedResultPage.getContent();                
            }
        };
    }
    public LazyDataModel getModel() {
        return model;
    }
}
```

A third variant may be doing something like this: 

```java
public class CarBean {
    private LazyDataModel model;

    public CarBean() {
        model = new LazyDataModel() {
            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                return 0;
            }

            @Override
            public List<Car> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                // setRowCount and recalculateFirst is required if #count method isnt implemented correctly
                setRowCount(x);
                first = recalculateFirst(first, pageSize, getRowCount());                
                
                // load physical data
                return requestedResultPage;
            }
        };
    }
    public LazyDataModel getModel() {
        return model;
    }
}
```

### JPALazyDataModel

PrimeFaces provides a OOTB implementation for JPA users, which supports basic features.

```java
JPALazyDataModel<MyEntity> lazyDataModel = JPALazyDataModel.<MyEntity> builder()
        .entityClass(MyEntity.class)
        .entityManager(() -> entityManager)
        .build();
```

If you have selection enabled, we need a `rowKey`. 
It's very likely that this is the mapped JPA `@Id`, so we try to auto-lookup per default.
Otherwise you can also define it:

```java
JPALazyDataModel<MyEntity> lazyDataModel = JPALazyDataModel.<MyEntity> builder()
        ...
        .rowKeyField(MyEntity_.id) // JPA MetaModel
        .rowKeyField("id") // or as String
        ...
```

or provide a existing Jakarta Faces `Converter`:

```java
JPALazyDataModel<MyEntity> lazyDataModel = JPALazyDataModel.<MyEntity> builder()
        ...
        .rowKeyConverter(myConverter)
        ...
```

#### Case insensitive searching
Per default the filters are case sensitive but you can also disable it:

```java
JPALazyDataModel<MyEntity> lazyDataModel = JPALazyDataModel.<MyEntity> builder()
        ...
        .caseSensitive(false)
        ...
```

#### Wildcards
Enable wildcard support for characters `*`, `%`, `?` and `_`. Characters `*` and `%` means any characters 
while `?` and `_` mean replace a single character.  For example `Smith*` would find all matches starting with 
the word `Smith`. For single character replacement like `Te?t` would match words `Tent` and `Test`.

```java
JPALazyDataModel<MyEntity> lazyDataModel = JPALazyDataModel.<MyEntity> builder()
        ...
        .wildcardSupport(true)
        ...
```

#### Add global filters
You can add global filters or manipulate generated predicates (from the DataTable columns) via:

```java
JPALazyDataModel<MyEntity> lazyDataModel = JPALazyDataModel.<MyEntity> builder()
        ...
        .filterEnricher((filterBy, cb, cq, root, predicates) -> {
            predicates.add(cb.isNull(root.get("id")));
        })
        ...
```


### Add custom sorting
You can manipulate generated sort orders (from the DataTable columns) via sortEnricher. For example, this enricher will override default null ordering to place nulls next to blank strings:

```java
JPALazyDataModel<MyEntity> lazyDataModel = JPALazyDataModel.<MyEntity> builder()
        ...
        .sortEnricher((filterBy, cb, cq, root, predicates) -> {
            SortMeta sortByExternalIdMeta = sortBy.values().stream()
                .filter(sortByEntry -> sortByEntry.getField().equals("externalId"))
                .findAny()
                .orElse(null);
            if (sortByExternalIdMeta != null) {
                switch (sortByExternalIdMeta.getOrder()) {
                case UNSORTED:
                    break;
                case ASCENDING:
                    orders.clear();
                    orders.add(cb.asc(cb.nullif(cb.trim(root.get("externalId")), cb.literal(""))));
                    break;
                case DESCENDING:
                    orders.clear();
                    orders.add(cb.desc(cb.nullif(cb.trim(root.get("externalId")), cb.literal(""))));
                    break;
                }
            }
        })
        ...
```

Note that `.clear()` removes all applied sorting from the UI, and so will give unexpected results for multiple-sorted DataTables.


#### Add additional filters
You can add your own/custom FilterMeta to manipulate generated predicates:
```java
JPALazyDataModel<MyEntity> lazyDataModel = JPALazyDataModel.<MyEntity>builder()
        ...
        .additionalFilterMeta(() -> {
            return new ArrayList<FilterMeta>(...);
        })
        ...
```


#### `Iterator` and performance considerations
`JPALazyDataModel`, being an extension of `DataModel`, is iterable over the JPA values. This is accomplished lazily by paging through, and therefore querying, the data as needed (calls to `hasNext()` and `next()` from `Iterator` in turn call `load(first, pageSize, sortBy, filterBy)`).

If you know you are going to retrieve all pages of data this can be inefficient, especially for large datasets. Loading the data, as the following, can result in far fewer individual queries to pull the full dataset.
```java
JPALazyDataModel<MyEntity> lazyDataModel = JPALazyDataModel.<MyEntity> builder()
        ...

// Load all sorted and filtered data in one query (plus one for count)
DataTable binding = ...
Map<String, SortMeta> sortMeta = binding.getSortByAsMap();
Map<String, FilterMeta> filterMeta = binding.getFilterByAsMap();
List<MyEntity> result = lazyDataModel.load(0, lazyDataModel.count(filterMeta), sortMeta, filterMeta);

// OR, to load all
Map<String, SortMeta> sortMeta = Collections.emptyMap();
Map<String, FilterMeta> filterMeta = Collections.emptyMap();
List<MyEntity> result = lazyDataModel.load(0, lazyDataModel.count(filterMeta), sortMeta, filterMeta);
```

#### Extend `JPALazyDataModel`

You can also extend the `JPALazyDataModel` and reuse the builder:

```
public class MyJPALazyDataModel<T> extends JPALazyDataModel<T> {

    public static <T> Builder<T, MyJPALazyDataModel<T>> builder() {
        return new Builder<>(new MyJPALazyDataModel<>());
    }
}
```

#### Unsupported features

Due to its implementation based on Criteria API, certain features of `DataTable` simply cannot work with `JPALazyDataModel` but may "fail silently". To aid implementors in avoiding running into issues, we've compiled (and will continue to add to) a list of features in this category:
* `filterFunction` - `p:column`'s `filterFunction` attribute will have no effect if `JPALazyDataModel` is used; alternative filter customizations listed above should be used instead

### FlowLogix JPALazyDataModel (PrimeFaces Community)
`JPALazyDataModel` implementation that's fully integrated with Jakarta EE and `@Inject`able.

```java
@Named
@ViewScoped
public class UserViewer implements Serializable {
    @Inject
    // optional configuration annotation
    @LazyModelConfig(caseInsensitive = true)
    JPALazyDataModel<UserEntity> lazyModel;
}
```
Developer's guide is available at https://docs.flowlogix.com/#section-jpa-lazymodel. Project page is available at https://github.com/flowlogix/flowlogix

### DefaultLazyDataModel

`DefaultLazyDataModel` is a `LazyDataModel` implementation which takes a supplier as datasource and implements filtering and sorting via reflection.
It can be used for cases where you would dynamically load the datasource e.g. from a webservice.

Also it can be used as replacement for binding a simple List as it has some benefits:
1) reflection is a performance boost for big lists, compared to restoring component states, rowindex, loop components, create/apply EL
2) often people store the data source (`List<MyPojo>`) in a `@ViewScoped` bean, which can lead to unexpected serialization, big sessions or old data references. The supplier should directly call your service, without storing the list in the current bean.
3) it allows some additional features like applying additional filtering or sorting via lambda.

```java
private DefaultLazyDataModel<MyPojo> dataModel;

@PostConstruct
public void init() {
    dataModel = DefaultLazyDataModel.<MyPojo>builder()
            .valueSupplier((filterBy) -> service.getListOfMyPojos())
            .rowKeyProvider(MyPojo::getId) // required for selection
            .build();
}
```

## Sticky Header
Sticky Header feature makes the datatable header visible on page scrolling.

```xhtml
<p:dataTable var="car" value="#{carBean.model}" stickyHeader="true">
    //columns
</p:dataTable>
```

## Column Toggler
Visibility of columns can be toggled using the column toggler helper component.

```xhtml
<p:dataTable var="car" value="#{tableBean.cars}">
    <f:facet name="header">
        List of Cars
        <p:commandButton id="toggler" type="button" value="Columns" style="float:right" icon="ui-icon-calculator" />
        <p:columnToggler datasource="cars" trigger="toggler" />
    </f:facet>
    <p:column headerText="Model">
        #{car.model}
    </p:column>
    <p:column headerText="Year" sortBy="year">
        #{car.year}
    </p:column>
    <p:column headerText="Manufacturer" sortBy="manufacturer">
        #{car.manufacturer}
    </p:column>
    <p:column headerText="Color" sortBy="color">
        #{car.color}
    </p:column>
</p:dataTable>
```

On page load, column chooser finds all columns of datatable and generates the ui. If you'd like
some of the columns to be ignored, set `toggleable` option of a column as false and for certain ones to
be hidden by default, set `visible` as false. Optional `toggle` ajax behavior is provided by
columnChooser component to listen to toggle events at server side. Listener of this behavior gets an
_org.primefaces.event.ToggleEvent_ as a parameter that gives the visibility and index of the column
being toggled.

## Add Row
When a new data needs to be added to the datatable, instead of updating the whole table to show it,
just call `addRow()` client side method and it will append the `tr` element only.

```xhtml
<p:dataTable var="car" value="#{dtBasicView.cars}" widgetVar="dt">
    <p:column headerText="Id">
        <h:inputText value="#{car.id}" />
    </p:column>
    <p:column headerText="Year">
        <h:inputText value="#{car.year}" />
    </p:column>
    <p:column headerText="Brand">
        <h:inputText value="#{car.brand}" />
    </p:column>
</p:dataTable>
<p:commandButton value="Add" action="#{dtBasicView.addCar}" oncomplete="PF('dt').addRow()" process="@this"/>
```

## Reordering Rows
Rows of the table can be reordered using drag&drop. Set draggableRows attribute to true to enable
this feature.

Optional `rowReorder` ajax behavior is provided to listen to reorder events at server side. Listener of
this behavior gets an `org.primefaces.event.ReorderEvent` as a parameter that gives the past and
current index of the row being moved.

```xhtml
<p:dataTable var="car" value="#{tableBean.cars}" draggableRows="true">
    <p:ajax event="rowReorder" listener="#{tableBean.onRowReorder}" />
    <p:column headerText="Model">
        #{car.model}
    </p:column>
    //columns
</p:dataTable>
```
```java
public class TableBean {
    //...
    public void onRowReorder(ReorderEvent event) {
        //int from = event.getFromIndex();
        //int end = event.getEndIndex();
    }
}
```

## Reordering Columns
Columns of the table can be reordered using drag&drop as well. Set draggableColumns attribute to
true to enable this feature. Optional `colReorder` ajax behavior is provided to listen to reorder events
at server side.

```xhtml
<p:dataTable var="car" value="#{tableBean.cars}" draggableColumns="true">
    <p:column headerText="Model">
        #{car.model}
    </p:column>
    //columns
</p:dataTable>
```

## Column Order / Priorities
Columns can be ordered via `displayPriority`. Lower value means higher priority.

```xhtml
<p:dataTable var="car" value="#{tableBean.cars}" draggableColumns="true">
    <p:column headerText="Model" displayPriority="4">
        #{car.model}
    </p:column>
    <p:column headerText="Brand" displayPriority="0">
        #{car.brand}
    </p:column>
    //columns
</p:dataTable>
```

## Responsive DataTable
DataTable has two responsive modes: `responsivePriority` and `reflow`.
In priority mode, responsiveness is based on column `responsivePriority` that vary between 1 and 6. Lower value means higher priority.
On the other hand in reflow mode that is enabled by setting reflow to true, all columns will be visible but displayed as stacked.

## Customizing emptyMessage
By default, a standard `emptyMessage` is displayed, which is translated using [PrimeFaces Localization](/core/localization.md)
Alternatively, a custom message can be defined using the `emptyMessage` attribute. If you wish to display HTML content, the `emptyMessage` facet can be utilized:
```
<f:facet name="emptyMessage">
   ....
</f:facet>
```

## Ajax Behavior Events

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| cellEdit | org.primefaces.event.CellEditEvent | When a cell is edited.
| cellEditCancel | org.primefaces.event.CellEditEvent | When a cell edit is cancelled e.g. with escape key
| cellEditInit | org.primefaces.event.CellEditEvent | When a cell edit begins.
| colReorder | - | When columns are reordered.
| colResize | org.primefaces.event.ColumnResizeEvent | When a column is being selected.
| contextMenu | org.primefaces.event.SelectEvent | ContextMenu display.
| filter | org.primefaces.event.data.FilterEvent | On filtering.
| liveScroll | org.primefaces.event.data.PageEvent | On live scroll loading more data.
| page | org.primefaces.event.data.PageEvent | On pagination.
| rowDblselect | org.primefaces.event.SelectEvent | Row selection with double click.
| rowEdit | org.primefaces.event.RowEditEvent | When a row is edited.
| rowEditCancel | org.primefaces.event.RowEditEvent | When row edit is cancelled.
| rowEditInit | org.primefaces.event.RowEditEvent | When a row switches to edit mode
| rowReorder | org.primefaces.event.ReorderEvent | On row reorder.
| rowSelect | org.primefaces.event.SelectEvent | When a row is being selected.
| rowSelectCheckbox | org.primefaces.event.SelectEvent | Row selection with checkbox.
| rowSelectRadio | org.primefaces.event.SelectEvent | Row selection with radio.
| rowToggle | org.primefaces.event.ToggleEvent | Row expand or collapse.
| rowUnselect | org.primefaces.event.UnselectEvent | When a row is being unselected.
| rowUnselectCheckbox | org.primefaces.event.UnselectEvent | Row unselection with checkbox.
| sort | org.primefaces.event.data.SortEvent | When a column is sorted.
| toggleSelect | org.primefaces.event.ToggleSelectEvent | When header checkbox is toggled.
| virtualScroll | org.primefaces.event.data.PageEvent | On virtual scoll loading more data.

For example, datatable below makes an ajax request when a row is selected with a click on row.

```xhtml
<p:dataTable var="car" value="#{carBean.model}">
    <p:ajax event="rowSelect" update="another_component" />
    //columns
</p:dataTable>
```
Moreover `org.primefaces.event.data.PostSortEvent`, `org.primefaces.event.data.PostFilterEvent` and
`org.primefaces.event.data.PostPageEvent`, are available to be used with `f:event` tag.


## Client Side API
Widget: `PrimeFaces.widget.DataTable`

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| addRow() | - | void | Fetches the last row from the backend and inserts a row instead of updating the table itself.
| clearFilters() | - | void | Clears all column filters
| collapseAllRows() | - | void | Collapses all rows that are currently expanded.
| filter() | - | void | Filters the data.
| getPaginator() | - | Paginator | Returns the paginator instance.
| getSelectedRowsCount() | - | Number | Returns number of selected rows.
| selectAllRows() | - | void | Select all rows.
| selectAllRowsOnPage() | - | void | Select all rows on current page.
| selectRow(r, silent) | r : number or tr element as jQuery object, silent : flag to fire row select ajax behavior | void | Selects the given row.
| toggleCheckAll() | - | void | Toggles header checkbox state.
| unselectAllRows() | - | void | Unselects all rows.
| unselectAllRowsOnPage() | - | void | Unselect all rows on current page.
| unselectRow(r, silent) | r : number or tr element as jQuery object, silent : flag to fire row select ajax behavior | void | Unselects the given row.
| toggleFilter(speed, callback)  | speed : speed argument for nested jQuery fadeToggle call, callback : to be executed after animation has completed | void | Show/Hide the filter components in the header row.



## Server Side API
Class: `org.primefaces.component.datatable.DataTable`

| Method |  Return Type | Description |
| --- | --- | --- |
| selectRow(String rowKey) | void | Selects a row
| unselectRow(String rowKey) | void | Unselects a row
| expandRow(String rowKey) | void | Expands a row
| collapseRow(String rowKey) | void | Collapse a row

## Skinning
DataTable resides in a main container element which `style` and `styleClass` options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;


| Class | Applies |
| --- | --- |
| .ui-datatable | Main container element
| .ui-datatable-data | Table body
| .ui-datatable-empty-message | Empty message row
| .ui-datatable-header | Table header
| .ui-datatable-footer | Table footer
| .ui-sortable-column | Sortable columns
| .ui-sortable-column-icon | Icon of a sortable icon
| .ui-expanded-row-content | Content of an expanded row
| .ui-row-toggler | Row toggler for row expansion
| .ui-editable-column | Columns with a cell editor
| .ui-cell-editor | Container of input and output controls of an editable cell
| .ui-cell-editor-input | Container of input control of an editable cell
| .ui-cell-editor-output | Container of output control of an editable cell
| .ui-datatable-even | Even numbered rows
| .ui-datatable-odd | Odd numbered rows
| .ui-datatable-scrollable | Main container element of a scrollable table.
| .ui-datatable-scrollable-header | Header wrapper of a scrollable table.
| .ui-datatable-scrollable-header-box | Header container of a scrollable table.
| .ui-datatable-scrollable-body | Body container of a scrollable table.
| .ui-datatable-scrollable-footer | Footer wrapper of a scrollable table.
| .ui-datatable-scrollable-footer-box | Footer container of a scrollable table.
| .ui-datatable-resizable | Main container element of a resizable table.
| .ui-datatable-frozencolumn | Frozen columns. 
