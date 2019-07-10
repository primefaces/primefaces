# DataTable

DataTable displays data in tabular format.

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
| ------------------------- | ------------------ | ---------------- | ------------------ |
| ariaRowLabel              | null               | String           | Label to read by screen readers on checkbox selection.
| binding                   | null               | Object           | An el expression that maps to a server side UIComponent instance in a backing bean
| caseSensitiveSort         | false              | Boolean          | Case sensitivity for sorting, insensitive by default.
| cellEditMode              | eager              | String           | Defines the cell edit behavior, valid values are "eager" (default) and "lazy".
| cellSeparator             | null               | String           | Separator text to use in output mode of editable cells with multiple components.
| clientCache               | false              | Boolean          | Caches the next page asynchronously, default is false.
| currentPageReportTemplate | null               | String           | Template of the currentPageReport UI.
| dataLocale                | null               | Object           | Locale to be used in features such as filtering and sorting, defaults to view locale.
| dir                       | ltr                | String           | Defines text direction, valid values are _ltr_ and _rtl_.
| disableContextMenuIfEmpty | false              | Boolean          | Decides whether to disable context menu or not if a table has no records.
| disabledSelection         | false              | Boolean          | Disables row selection when true. Overrides p:column's disabledSelection attr. Example: var="xxx" disabledSelection="#{xxx.year > 1960}"
| disabledTextSelection     | true               | Boolean          | Disables text selection on row click.
| draggableColumns          | false              | Boolean          | Columns can be reordered with dragdrop when enabled.
| draggableRows             | false              | Boolean          | When enabled, rows can be reordered using dragdrop.
| draggableRowsFunction     | null               | MethodExpression | Method expression to execute after dragging row.
| editInitEvent             | null               | String           | Defines a client side event to open cell on editable table.
| editMode                  | row                | String           | Defines edit mode, valid values are _row_ and _cell_.
| editable                  | false              | Boolean          | Controls incell editing.
| editingRow                | false              | Boolean          | Defines if cell editors of row should be displayed as editable or not.
| emptyMessage              | No records found.  | String           | Text to display when there is no data to display. Alternative is emptyMessage facet.
| escapeText                | true               | Boolean          | Defines if headerText and footerText values on columns are escaped or not. Default is true.
| expandableRowGroups       | false              | Boolean          | Makes row groups toggleable, default is false.
| expandedRow               | false              | Boolean          | Defines if row should be rendered as expanded by default.
| filterBy                  | null               | List             | List of FilterState objects to filter table by default.
| filterDelay               | 300                | Integer          | Delay in milliseconds before sending an ajax filter query.
| filterEvent               | keyup              | String           | Event to invoke filtering for input filters.
| filteredValue             | null               | List             | List to keep filtered data.
| first                     | 0                  | Integer          | Index of the first row to be displayed
| frozenColumns             | 0                  | Integer          | Number of columns to freeze from start index 0.
| frozenRows                | null               | Object           | Collection to display as fixed in scrollable mode.
| globalFilter              | null               | String           | Value of the global filter to use when filtering by default.
| globalFilterFunction      | null               | MethodExpression | Custom implementation to globally filter a value against a constraint.
| id                        | null               | String           | Unique identifier of the component
| initMode                  | load               | String           | Defines when the datatable is initiated at client side, valid values are "load" (default) and "immediate".
| lazy                      | false              | Boolean          | Controls lazy loading.
| liveResize                | false              | Boolean          | Columns are resized live in this mode without using a resize helper.
| liveScroll                | false              | Boolean          | Enables live scrolling.
| liveScrollBuffer          | 0                  | Integer          | Percentage height of the buffer between the bottom of the page and the scroll position to initiate the load for the new chunk. Value is defined in integer and default is 0.
| meaning                   | null               | String           | Values are placed at the end in ascending mode and at beginning in descending mode. Set to "-1" for the opposite behavior.
| multiViewState            | false              | Boolean          | Whether to keep table state across views, defaults to false.
| nativeElements            | false              | Boolean          | Uses native radio-checkbox elements for row selection.
| nullSortOrder             | 1                  | Integer          | Defines where the null values are placed in ascending sort order. Default value is "1"
| onExpandStart             | null               | String           | Client side callback to execute before expansion.
| onRowClick                | null               | String           | Client side callback to execute after clicking row.
| pageLinks                 | 10                 | Integer          | Maximum number of page links to display.
| paginator                 | false              | Boolean          | Enables pagination.
| paginatorAlwaysVisible    | true               | Boolean          | Defines if paginator should be hidden if total data count is less than number of rows per page.
| paginatorPosition         | both               | String           | Position of the paginator.
| paginatorTemplate         | null               | String           | Template of the paginator.
| reflow                    | false              | Boolean          | Reflow mode is a responsive mode to display columns as stacked depending on screen size.
| rendered                  | true               | Boolean          | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| resizableColumns          | false              | Boolean          | Enables column resizing.
| resizeMode                | fit                | String           | Defines the resize behavior, valid values are "fit" (default) and expand.
| rowDragSelector           | td,span:not(.ui-c) | String           | Defines the element used to reorder rows using dragdrop. Default selector is "td,span:not(.ui-c)"
| rowEditMode               | eager              | String           | Defines the row edit behavior, valid values are "eager" (default) and "lazy".
| rowExpandMode             | new                | String           | Defines row expand mode, valid values are "single" and "multiple" (default).
| rowHover                  | false              | Boolean          | Adds hover effect to rows, default is false. Hover is always on when selection is enabled.
| rowIndexVar               | null               | String           | Name of iterator to refer each row index.
| rowKey                    | null               | String           | Unique identifier of a row.
| rowSelectMode             | new                | String           | Defines row selection mode for multiple selection. Valid values are "new", "add" and "checkbox".
| rowSelector               | null               | String           | Client side check if rowclick triggered row click event not a clickable element in row content.
| rowStatePreserved         | false              | Boolean          | Keeps state of its children on a per-row basis. Default is false.
| rowStyleClass             | null               | String           | Style class for each row.
| rows                      | null               | Integer          | Number of rows to display per page.
| rowsPerPageLabel          | null               | String           | Label for the rowsPerPage dropdown.
| rowsPerPageTemplate       | null               | String           | Template of the rowsPerPage dropdown.
| saveOnCellBlur            | true               | Boolean          | Saves the changes in cell editing on blur, when set to false changes are discarded..
| scrollHeight              | null               | Integer          | Scroll viewport height.
| scrollRows                | 0                  | Integer          | Number of rows to load on live scroll.
| scrollWidth               | null               | Integer          | Scroll viewport width.
| scrollable                | false              | Boolean          | Makes data scrollable with fixed header.
| selection                 | null               | Object           | Reference to the selection data.
| selectionMode             | null               | String           | Enables row selection, valid values are “single” and “multiple”.
| skipChildren              | false              | Boolean          | Ignores processing of children during lifecycle, improves performance if table only has output components.
| sortBy                    | null               | Object           | Property to be used for default sorting.
| sortField                 | null               | String           | Name of the field to pass lazy load method for sorting. If not specified, sortBy expression is used to extract the name.
| sortMode                  | single             | String           | Defines sorting mode, valid values are _single_ and _multiple_.
| sortOrder                 | ascending          | String           | “ascending” or “descending”.
| stickyHeader              | false              | Boolean          | Sticky header stays in window viewport during scrolling.
| stickyTopAt               | null               | String           | Selector to position on the page according to other fixing elements on the top of the table. Default is null.
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

## Getting started with the DataTable
We will be using the same Car and CarBean classes described in DataGrid section.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}">
    <p:column>
        <f:facet name="header">
            <h:outputText value="Model" />
        </f:facet>
        <h:outputText value="#{car.model}" />
    </p:column>
    //more columns
</p:dataTable>
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
Optionally enabling clientCache property loads the next page asynchronously so that when user
clicks the second page, data is displayed instantly from client side.

## Paginator Template
Paginator is customized using paginatorTemplateOption that accepts various keys of UI controls.

- FirstPageLink
- LastPageLink
- PreviousPageLink
- NextPageLink
- PageLinks
- CurrentPageReport
- RowsPerPageDropdown
- JumpToPageDropdown
- JumpToPageInput

**Note** that _{RowsPerPageDropdown}_ has it’s own template, options to display is provided via
rowsPerPageTemplate attribute (e.g. rowsPerPageTemplate="9,12,15").

Also _{CurrentPageReport}_ has it’s own template defined with currentPageReportTemplate option.
You can use _{currentPage},{totalPages},{totalRecords},{startRecord},{endRecord}_ keyword
within currentPageReportTemplate. Default is "_{currentPage}_ of _{totalPages}_". Default UI is;

which corresponds to the following template.

- _{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink}_

Here are more examples based on different templates;

- _{CurrentPageReport} {FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}_

- _{PreviousPageLink} {CurrentPageReport} {NextPageLink}_

## Paginator Position
Paginator can be positoned using _paginatorPosition_ attribute in three different locations, "top",
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
Defining _sortBy_ attribute enables ajax based sorting on that particular column.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}">
    <p:column sortBy="#{car.model}" headerText="Model">
        <h:outputText value="#{car.model}" />
    </p:column>
    ...more columns
</p:dataTable>
```
Instead of using the default sorting algorithm which uses a java comparator, you can plug-in your
own sort method as well.

```java
public int sortByModel(Object car1, Object car2) {
    //return -1, 0 , 1 if car1 is less than, equal to or greater than car2
}
```

```xhtml
<p:dataTable var="car" value="#{carBean.cars}">
    <p:column sortBy="#{car.model}" sortFunction="#{carBean.sortByModel}" headerText="Model">
        <h:outputText value="#{car.model}" />
    </p:column>
    ...more columns
</p:dataTable>
```
Multiple sorting is enabled by setting _sortMode_ to _multiple_. In this mode, clicking a sort column
while metakey is on adds sort column to the order group.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" sortMode="multiple">
    //columns
</p:dataTable>
```
DataTable can display data sorted by default, to implement this use the _sortBy_ option of datatable
and the optional _sortOrder._ Table below would be initially displayed as sorted by model.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" sortBy="#{car.model}">
    <p:column sortBy="#{car.model}" headerText=”Model”>
        <h:outputText value="#{car.model}" />
    </p:column>
    <p:column sortBy="#{car.year}" headerText="Year">
        <h:outputText value="#{car.year}" />
    </p:column>
    ...more columns
</p:dataTable>
```
## Filtering
Ajax based filtering is enabled by setting _filterBy_ at column level and providing a list to keep the
filtered sublist. It is suggested to use a scope longer than request like viewscope to keep the
filteredValue so that filtered list is still accessible after filtering.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" filteredValue="#{carBean.filteredCars}">
    <p:column filterBy="#{car.model}" headerText="Model">
        <h:outputText value="#{car.model}" />
    </p:column>
    ...more columns
</p:dataTable>
```
Filtering is triggered with keyup event and filter inputs can be styled using _filterStyle_ ,
_filterStyleClass_ attributes. If you’d like to use a dropdown instead of an input field to only allow
predefined filter values use _filterOptions_ attribute and a collection/array of selectitems as value. In
addition, _filterMatchMode_ defines the built-in matcher which is _startsWith_ by default.

Following is a basic filtering datatable with these options demonstrated;


```xhtml
<p:dataTable var="car" value="#{carBean.cars}" filteredValue="#{carBean.filteredCars}" widgetVar="carsTable">
    <f:facet name="header">
        <p:outputPanel>
            <h:outputText value="Search all fields:" />
            <h:inputText id="globalFilter" onkeyup="PF('carsTable').filter()" />
        </p:outputPanel>
    </f:facet>
    <p:column filterBy="#{car.model}" headerText="Model" filterMatchMode="contains">
        <h:outputText value="#{car.model}" />
    </p:column>
    <p:column filterBy="#{car.year}" headerText="Year" footerText="startsWith">
        <h:outputText value="#{car.year}" />
    </p:column>
    <p:column filterBy="#{car.manufacturer}" headerText="Manufacturer" filterOptions="#{carBean.manufacturerOptions}" filterMatchMode="exact">
        <h:outputText value="#{car.manufacturer}" />
    </p:column>
    <p:column filterBy="#{car.color}" headerText="Color" filterMatchMode="endsWith">
        <h:outputText value="#{car.color}" />
    </p:column>
    <p:column filterBy="#{car.price}" headerText="Price" filterMatchMode="exact">
        <h:outputText value="#{car.price}" />
    </p:column>
</p:dataTable>
```
Filter located at header is a global one applying on all fields, this is implemented by calling client
side API method called _filter(),_ important part is to specify the id of the input text as _globalFilter_
which is a reserved identifier for datatable.

In addition to default filtering with generated elements, custom elements can also be used as a filter
facet. Example below uses custom filter components in combination with generated elements.
When a custom component is used as a filter facet, filtering needs to be called manually from a
preferred event such as onchange="PF('carsTable').filter()". Also defining a converter might be
necessary if the value of the filter facet is not defined.

```xhtml
<p:dataTable id="dataTable" var="car" value="#{tableBean.carsSmall}" widgetVar="carsTable" filteredValue="#{tableBean.filteredCars}">
    <p:column id="modelColumn" filterBy="#{car.model}" headerText="Model" footerText="contains" filterMatchMode="contains">
        <h:outputText value="#{car.model}" />
    </p:column>
    <p:column id="yearColumn" filterBy="#{car.year}" headerText="Year" footerText="lte" filterMatchMode="lte">
        <f:facet name="filter">
            <p:spinner onchange="PF('carsTable').filter()" min="1960" max="2010">
                <f:converter converterId="javax.faces.Integer" />
            </p:spinner>
        </f:facet>
        <h:outputText value="#{car.year}" />
    </p:column>
    <p:column id="manufacturerColumn" filterBy="#{car.manufacturer}" headerText="Manufacturer" footerText="exact" filterMatchMode="exact">
        <f:facet name="filter">
            <p:selectOneMenu onchange="PF('carsTable').filter()" >
                <f:selectItems value="#{tableBean.manufacturerOptions}" />
            </p:selectOneMenu>
        </f:facet>
        <h:outputText value="#{car.manufacturer}" />
    </p:column>
    <p:column id="colorColumn" filterBy="#{car.color}" headerText="Color" footerText="in" filterMatchMode="in">
        <f:facet name="filter">
            <p:selectCheckboxMenu label="Colors" onchange="PF('carsTable').filter()">
                <f:selectItems value="#{tableBean.colors}" />
            </p:selectCheckboxMenu>
        </f:facet>
        <h:outputText value="#{car.color}" />
    </p:column>
    <p:column id="soldColumn" filterBy="#{car.sold}" headerText="Status" footerText="equals" filterMatchMode="equals">
        <f:facet name="filter">
            <p:selectOneButton onchange="PF('carsTable').filter()">
            <f:converter converterId="javax.faces.Boolean" />
            <f:selectItem itemLabel="All" itemValue="" />
            <f:selectItem itemLabel="Sold" itemValue="true" />
            <f:selectItem itemLabel="Sale" itemValue="false" />
            </p:selectOneButton>
        </f:facet>
        <h:outputText value="#{car.sold? 'Sold': 'Sale'}" />
    </p:column>
</p:dataTable>
```

_filterMatchMode_ defines which built-in filtering algorithm would be used per column, valid values
for this attribute are;

- **startsWith** : Checks if column value starts with the filter value.
- **endsWith** : Checks if column value ends with the filter value.
- **contains** : Checks if column value contains the filter value.
- **exact** : Checks if string representations of column value and filter value are same.
- **lt** : Checks if column value is less than the filter value.
- **lte** : Checks if column value is less than or equals the filter value.
- **gt** : Checks if column value is greater than the filter value.
- **gte** : Checks if column value is greater than or equals the filter value.
- **equals** : Checks if column value equals the filter value.
- **in** : Checks if column value is in the collection of the filter value.

In case the built-in methods do not suffice, custom filtering can be implemented using
filterFunction approach.

```xhtml
<p:column filterBy="#{car.price}" filterFunction="#{tableBean.filterByPrice}">
    <h:outputText value="#{car.price}">
        <f:convertNumber currencySymbol="$" type="currency"/>
    </h:outputText>
</p:column>
```
_filterFunction_ should be a method with three parameters; column value, filter value and locale.
Return value is a boolean, true accepts the value and false rejects it.

```java
public boolean filterByPrice(Object value, Object filter, Locale locale) {
    //return true or false
}
```
Locale is provided as optional in case you need to use a locale aware method like
_toLowerCase(Locale locale)._ Note that | String | based filters like startsWith, endsWith uses
toLowerCase already and _dataLocale_ attribute is used to provide the locale to use when filtering.

This same principle can be applied globally by implementing a globalFilterFunction. It takes the
same parameters of which the first parameter is the row value.

## Row Selection
There are several ways to select row(s) from datatable. Let’s begin by adding a Car reference for
single selection and a Car array for multiple selection to the CarBean to hold the selected data.

```java
public class CarBean {
    private List<Car> cars;
    private Car selectedCar;
    private Car[] selectedCars;

    public CarBean() {
        cars = new ArrayList<Car>();
        //populate cars
    }
    //getters and setters
}
```
#### Single Selection with a Command Component
This method is implemented with a command component such as commandLink or
commandButton. Selected row can be set to a server side instance by passing as a parameter if you
are using EL 2.2 or using f:setPropertyActionListener.


```xhtml
<p:dataTable var="car" value="#{carBean.cars}">
    <p:column>
        <p:commandButton value="Select">
            <f:setPropertyActionListener value="#{car}" target="#{carBean.selectedCar}" />
        </p:commandButton>
    </p:column>
    ...columns
</p:dataTable>
```
#### Single Selection with Row Click
Previous method works when the button is clicked, if you’d like to enable selection wherever the
row is clicked, use _selectionMode_ option.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" selectionMode="single" selection="#{carBean.selectedCar}" rowKey="#{car.id}">
    ...columns
</p:dataTable>
```
#### Multiple Selection with Row Click
Multiple row selection is similar to single selection but selection should reference an array or a list
of the domain object displayed and user needs to use press modifier key(e.g. ctrl) during selection *.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" selectionMode="multiple" selection="#{carBean.selectedCars}" rowKey="#{car.id}" >
    ...columns
</p:dataTable>
```
#### Single Selection with RadioButton
Selection a row with a radio button placed on each row is a common case, datatable has built-in
support for this method so that you don’t need to deal with h:selectOneRadios and low level bits. In
order to enable this feature, define a column with _selectionMode_ set as single.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" selection="#{carBean.selectedCar}" rowKey="#{car.id}">
    <p:column selectionMode="single"/>
    ...columns
</p:dataTable>
```
#### Multiple Selection with Checkboxes
Similar to how radio buttons are enabled, define a selection column with a multiple selectionMode.
DataTable will also provide a selectAll checkbox at column header.

```xhtml
<p:dataTable var="car" value="#{carBean.cars}" selection="#{carBean.selectedCars}" rowKey="#{car.id}" >
    <p:column selectionMode="multiple"/>
    ...columns
</p:dataTable>
```

**Tip**: Use rowSelectMode option to customize the default behavior on row click of a multiple
selection enabled datatable. Default value is "new" that clears previous selections, "add" mode
keeps previous selections same as selecting a row with mouse click when metakey is on and
"checkbox" mode allows row selection with checkboxes only.

## RowKey
RowKey should a unique identifier from your data model and used by datatable to find the selected
rows. You can either define this key by using the rowKey attribute or by binding a data model
which implements _org.primefaces.model.SelectableDataModel_.

## Dynamic Columns
Dynamic columns is handy in case you can’t know how many columns to render. Columns
component is used to define the columns programmatically. It requires a collection as the value, two
iterator variables called _var_ and _columnIndexVar_.

```xhtml
<p:dataTable var="cars" value="#{tableBean.cars}">
    <p:columns value="#{tableBean.columns}" var="column" sortBy="#{column.property}" filterBy="#{column.property}">
        <f:facet name="header">
            #{column.header}
        </f:facet>
        <h:outputText value="#{cars[column.property]}" />
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
For frozen columns, use _frozenHeader_ , _frozenFooter_ , _scrollableHeader_ and _scrollableFooter_ types.


## Row Grouping
Rows can be grouped in two ways, using headerRow, summaryRow components or with groupRow
attribute on a column.

```xhtml
<p:dataTable var="car" value="#{dtRowGroupView.cars}" sortBy="#{car.brand}">
    <p:headerRow>
        <p:column colspan="3">
            <h:outputText value="#{car.brand}" />
        </p:column>
    </p:headerRow>
    <p:column headerText="Year">
        <h:outputText value="#{car.year}" />
    </p:column>
    <p:column headerText="Color">
        <h:outputText value="#{car.color}" />
    </p:column>
    <p:column headerText="Id">
        <h:outputText value="#{car.id}" />
    </p:column>
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
Optionally rows can be made toggleable using _expandableRowGroups_ property.

Alternative approach is using row spans where a row can group multiple rows within the same
group. To enable this method, set groupRow to true on the grouping column.

```xhtml
<p:dataTable var="car" value="#{dtRowGroupView.cars}" sortBy="#{car.brand}">
    <p:column headerText="Brand" groupRow="true">
        <h:outputText value="#{car.brand}" />
    </p:column>
    <p:column headerText="Year">
        <h:outputText value="#{car.year}" />
    </p:column>
    <p:column headerText="Color">
        <h:outputText value="#{car.color}" />
    </p:column>
    <p:column headerText="Id">
        <h:outputText value="#{car.id}" />
    </p:column>
</p:dataTable>
```

## Scrolling
Scrolling makes the header-footer of the table fixed and the body part scrollable. Scrolling is
enabled using _scrollable_ property and has 3 modes; x, y and x-y scrolling that are defined by
_scrollHeight_ and _scrollWidth._ These two scroll attributes can be specified using integer values
indicating fixed pixels or percentages relative to the container dimensions.

```xhtml
<p:dataTable var="car" value="#{bean.data}" scrollable="true" scrollHeight="150">
    <p:column />
    //columns
</p:dataTable>
```
Simple scrolling renders all data to client whereas virtual scrolling combined with lazy loading is
useful to deal with huge data, in this case data is fetched on-demand. Set _virtualScroll_ to enable this
option and provide LazyDataModel;

```xhtml
<p:dataTable var="car" value="#{bean.data}" scrollable="true" scrollHeight="150" virtual="true">
    <p:column />
    //columns
</p:dataTable>
```
## Frozen Rows
Certain rows can be fixed in a scrollable table by using the _frozenRows_ attribute that defines the
number of rows to freeze from the start.

```xhtml
<p:dataTable var="car" value="#{bean.data}" scrollable="true" scrollHeight="150" frozenRows="2">
    <p:column />
    //columns
</p:dataTable>
```
## Frozen Columns
Specific columns can be fixed while the rest of them remain as scrollable. _frozenColumns_ defines
the number of columns to freeze from the start.


```xhtml
<p:dataTable var="car" value="#{bean.data}" scrollable="true" scrollWidth="200" frozenColumns="2">
    <p:column />
    //columns
</p:dataTable>
```
## Expandable Rows
_RowToggler_ and _RowExpansion_ facets are used to implement expandable rows.

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
expanded content with ajax. If you need to display a row as expanded by default, use _expandedRow_
attribute which is evaluated before rendering of each row so value expressions are supported.
Additionally, rowExpandMode attribute defines if multiple rows can be expanded at the same time
or not, valid values are "single" and "multiple" (default).

## Editing
Incell editing provides an easy way to display editable data. _p:cellEditor_ is used to define the cell
editor of a particular column. There are two types of editing, _row_ and _cell_. Row editing is the
default mode and used by adding a _p:rowEditor_ component as row controls.


```xhtml
<p:dataTable var="car" value="#{carBean.cars}" editable="true">
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
When pencil icon is clicked, row is displayed in editable mode meaning input facets are displayed
and output facets are hidden. Clicking tick icon only saves that particular row and cancel icon
reverts the changes, both options are implemented with ajax interaction.

Another option for incell editing is cell editing, in this mode a cell switches to edit mode when it is
clicked, losing focus triggers an ajax event to save the change value.

## Lazy Loading
Lazy Loading is an approach to deal with huge datasets efficiently, regular ajax based pagination
works by rendering only a particular page but still requires all data to be loaded into memory. Lazy
loading datatable renders a particular page similarly but also only loads the page data into memory
not the whole dataset. In order to implement this, you’d need to bind a
_org.primefaces.model.LazyDataModel_ as the value and implement _load_ method and enable _lazy_
option. Also it is required to implement _getRowData_ and _getRowKey_ if you have selection enabled.


```xhtml
<p:dataTable var="car" value="#{carBean.model}" paginator="true" rows="10" lazy="true">
    //columns
</p:dataTable>
```
```java
public class CarBean {
    private LazyDataModel model;

    public CarBean() {
        model = new LazyDataModel() {
            @Override
            public void load(int first, int pageSize, | String | sortField,
            SortOrder sortOrder, Map<String,Object> filters) {
            //load physical data
            }
        };
        int totalRowCount = //logical row count based on a count query
        model.setRowCount(totalRowCount);
    }
    public LazyDataModel getModel() {
        return model;
    }
}
```
DataTable calls your load implementation whenever a paging, sorting or filtering occurs with
following parameters;

- **first**: Offset of first data to start from
- **pageSize**: Number of data to load
- **sortField**: Name of sort field
- **sortOrder**: SortOrder enum.
- **filter**: Filter map with field name as key (e.g. "model" for filterBy="#{car.model}") and value.

In addition to load method, totalRowCount needs to be provided so that paginator can display itself
according to the logical number of rows to display.

It is suggested to use _field_ attribute of column component to define the field names passed as
sortField and filterFields, otherwise these fields would be tried to get extracted from the value
expression which is not possible in cases like composite components.

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
some of the columns to be ignored, set _toggleable_ option of a column as false and for certain ones to
be hidden by default, set _visible_ as false. Optional _toggle_ ajax behavior is provided by
columnChooser component to listen to toggle events at server side. Listener of this behavior gets an
_org.primefaces.event.ToggleEvent_ as a parameter that gives the visibility and index of the column
being toggled.

## Add Row
When a new data needs to be added to the datatable, instead of updating the whole table to show it,
just call addRow() client side method and it will append the tr element only.

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

Optional _rowReorder_ ajax behavior is provided to listen to reorder events at server side. Listener of
this behavior gets an _org.primefaces.event.ReorderEvent_ as a parameter that gives the past and
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
true to enable this feature. Optional _colReorder_ ajax behavior is provided to listen to reorder events
at server side.

```xhtml
<p:dataTable var="car" value="#{tableBean.cars}" draggableColumns="true">
    <p:column headerText="Model">
        #{car.model}
    </p:column>
    //columns
</p:dataTable>
```
## Responsive DataTable
DataTable has two responsive modes; priority and reflow. In priority mode, responsiveness is based
on column priorities that vary between 1 and 6. Lower value means higher priority. On the other
hand in reflow mode that is enabled by setting reflow to true, all columns will be visible but
displayed as stacked.


## Ajax Behavior Events

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| page | org.primefaces.event.data.PageEvent | On pagination.
| sort | org.primefaces.event.data.SortEvent | When a column is sorted.
| filter | org.primefaces.event.data.FilterEvent | On filtering.
| rowSelect | org.primefaces.event.SelectEvent | When a row is being selected.
| rowUnselect | org.primefaces.event.UnselectEvent | When a row is being unselected.
| rowEdit | org.primefaces.event.RowEditEvent | When a row is edited.
| rowEditInit | org.primefaces.event.RowEditEvent | When a row switches to edit mode
| rowEditCancel | org.primefaces.event.RowEditEvent | When row edit is cancelled.
| colResize | org.primefaces.event.ColumnResizeEvent | When a column is being selected.
| toggleSelect | org.primefaces.event.ToggleSelectEvent | When header checkbox is toggled.
| colReorder | - | When columns are reordered.
| rowSelectRadio | org.primefaces.event.SelectEvent | Row selection with radio.
| rowSelectCheckbox | org.primefaces.event.SelectEvent | Row selection with checkbox.
| rowUnselectCheckbox | org.primefaces.event.UnselectEvent | Row unselection with checkbox.
| rowDblselect | org.primefaces.event.SelectEvent | Row selection with double click.
| rowToggle | org.primefaces.event.ToggleEvent | Row expand or collapse.
| contextMenu | org.primefaces.event.SelectEvent | ContextMenu display.
| cellEdit | org.primefaces.event.CellEditEvent | When a cell is edited.
| cellEditInit | org.primefaces.event.CellEditEvent | When a cell edit begins.
| cellEditCancel | org.primefaces.event.CellEditEvent | When a cell edit is cancelled e.g. with escape key
| rowReorder | org.primefaces.event.ReorderEvent | On row reorder.

For example, datatable below makes an ajax request when a row is selected with a click on row.

```xhtml
<p:dataTable var="car" value="#{carBean.model}">
    <p:ajax event=”rowSelect” update=”another_component” />
    //columns
</p:dataTable>
```
Moreover _org.primefaces.event.data.PostSortEvent, org.primefaces.event.data.PostFilterEvent_ and
_org.primefaces.event.data.PostPageEvent,_ are available to be used with f:event tag.


## Client Side API
Widget: _PrimeFaces.widget.DataTable_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| getPaginator() | - | Paginator | Returns the paginator insance.
| clearFilters() | - | void | Clears all column filters
| getSelectedRowsCount() | - | Number | Returns number of selected rows.
| selectRow(r, silent) | r : number or tr element as jQuery object, silent : flag to fire row select ajax behavior | void | Selects the given row.
| unselectRow(r, silent) | r : number or tr element as jQuery object, silent : flag to fire row select ajax behavior | void | Unselects the given row.
| unselectAllRows() | - | void | Unselects all rows.
| toggleCheckAll() | - | void | Toggles header checkbox state.
| filter() | - | void | Filters the data.
| selectAllRows() | - | void | Select all rows.
| selectAllRowsOnPage() | - | void | Select all rows on current page.
| unselectAllRowsOnPage() | - | void | Unselect all rows on current page.
| addRow() | - | void | Fetches the last row from the backend and inserts a row instead of updating the table itself.

## Skinning
DataTable resides in a main container element which _style_ and _styleClass_ options apply. As skinning
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