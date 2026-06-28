# DataView

DataView displays data in grid or list layout.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.DataView-1.html)

## Getting started with the DataView
A list of cars will be used throughout the examples.

```java
public class Car {
    private String model;
    private int year;
    private String manufacturer;
    private String color;
    ...
}
```

The code for CarBean that would be used to bind the dataview to the car list.

```java
public class DataViewBean implements Serializable {
     
    private List<Car> cars;
     
    private Car selectedCar;
     
    @ManagedProperty("#{carService}")
    private CarService service;
     
    @PostConstruct
    public void init() {
        cars = service.createCars(48);
    }
 
    public List<Car> getCars() {
        return cars;
    }
 
    public void setService(CarService service) {
        this.service = service;
    }
 
    public Car getSelectedCar() {
        return selectedCar;
    }
 
    public void setSelectedCar(Car selectedCar) {
        this.selectedCar = selectedCar;
    }
}
```

DataView requires a value along with p:dataViewGridItem and p:dataViewListItem components that define the content to display in each format.

```xhtml
<p:dataView var="car" value="#{dataViewBean.cars}">
    <p:dataViewGridItem>
        <p:panel header="#{car.id}" style="text-align:center">
            <h:panelGrid columns="1" style="width:100%">
                <p:graphicImage name="demo/images/car/#{car.brand}.gif"/> 

                <h:outputText value="#{car.brand}" />

                <p:commandLink update=":form:carDetail" oncomplete="PF('carDialog').show()" title="View Detail">
                    <h:outputText styleClass="ui-icon pi pi-search" style="margin:0 auto;" />
                    <f:setPropertyActionListener value="#{car}" target="#{dataViewBean.selectedCar}" />
                </p:commandLink>
            </h:panelGrid>
        </p:panel>
    </p:dataViewGridItem>

    <p:dataViewListItem>
        <h:panelGrid columns="3" style="width:100%" columnClasses="logo,detail">
            <p:graphicImage name="demo/images/car/#{car.brand}-big.gif" /> 

            <p:outputPanel>
                <h:panelGrid columns="2" cellpadding="5">
                    <h:outputText value="Id:" />
                    <h:outputText value="#{car.id}" style="font-weight: bold"/>

                    <h:outputText value="Year:" />
                    <h:outputText value="#{car.year}" style="font-weight: bold"/>

                    <h:outputText value="Color:" />
                    <h:outputText value="#{car.color}" style="font-weight: bold"/>
                </h:panelGrid>
            </p:outputPanel>

            <p:commandLink update=":form:carDetail" oncomplete="PF('carDialog').show()" title="View Detail">
                <h:outputText styleClass="ui-icon pi pi-search" style="margin:0 auto;" />
                <f:setPropertyActionListener value="#{car}" target="#{dataViewBean.selectedCar}" />
            </p:commandLink>
        </h:panelGrid>
    </p:dataViewListItem>
</p:dataView>
```

## Ajax Pagination
DataView has a built-in paginator that is enabled by setting paginator option to true.

```xhtml
<p:dataView var="car" value="#{dataViewBean.cars}" rows="12" paginator="true" rowsPerPageTemplate="6,12,16"
            paginatorTemplate="{CurrentPageReport} {FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}">
    ...
</p:dataView>
```

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
Paginator can be positioned using _paginatorPosition_ attribute in three different locations, "top",
"bottom" or "both" (default).

## Custom Content in Paginator
Custom content can be placed inside a paginator using a facet name matching a token in the
template.

```xhtml
<p:dataView paginatorTemplate="{CurrentPageReport} {MyContent} ...">
    <f:facet name="{MyContent}">
        //Any content here
    </f:facet>
    //...
</p:dataView>
```

## Header
"header" facet is available to place custom content at the header section.
```xhtml
<p:dataView>
    <f:facet name="header">
        //Header Content
    </f:facet>
    //...
</p:dataView>
```

## Client Side API
Widget: _PrimeFaces.widget.DataView_


| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| getPaginator() | - | Paginator | Returns the paginator widget.

## Skinning
DataView resides in a main div container which style and styleClass attributes apply. Following is
the list of structural style classes;


| Class | Applies | 
| --- | --- | 
| .ui-dataview | Main container element
| .ui-dataview-header | Header container.
| .ui-dataview-content | Content container.

As skinning style classes are global, see the main theming section for more information.

**Tips**:

- DataView supports lazy loading data via LazyDataModel, see DataTable lazy loading section for more information.
