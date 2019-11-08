# Drag&Drop

Drag&Drop utilities of PrimeFaces consists of two components; Draggable and Droppable.

##  Draggable

### Info

| Name | Value |
| --- | --- |
| Tag | draggable
| Component Class | org.primefaces.component.dnd.Draggable
| Component Type | org.primefaces.component.Draggable
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DraggableRenderer
| Renderer Class | org.primefaces.component.dnd.DraggableRenderer

### Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget
| proxy | false | Boolean | Displays a proxy element instead of actual element.
| dragOnly | false | Boolean | Specifies a draggable that can’t be dropped.
| for | null | String | Id of the component to add draggable behavior
| disabled | false | Boolean | Disables draggable behavior when true.
| axis | null | String | Specifies drag axis, valid values are ‘x’ and ‘y’.
| containment | null | String | Constraints dragging within the boundaries of containment element
| helper | null | String | Helper element to display when dragging
| revert | false | Boolean | Reverts draggable to it’s original position when not dropped onto a valid droppable
| snap | false | Boolean | Draggable will snap to edge of near elements
| snapMode | null | String | Specifies the snap mode. Valid values are ‘both’, ‘inner’ and ‘outer’.
| snapTolerance | 20 | Integer | Distance from the snap element in pixels to trigger snap.
| zindex | null | Integer | ZIndex to apply during dragging.
| handle | null | String | Specifies a handle for dragging.
| opacity | 1 | Double | Defines the opacity of the helper during dragging.
| stack | null | String | In stack mode, draggable overlap is controlled automatically using the provided selector, dragged item always overlays other draggables.
| grid | null | String | Dragging happens in every x and y pixels.
| scope | null | String | Scope key to match draggables and droppables.
| cursor | crosshair | String | CSS cursor to display in dragging.
| dashboard | null | String | Id of the dashboard to connect with.
| appendTo | null | String | A search expression to define which element to append the draggable helper to.
| onStart | null | String | Client side callback to execute when dragging starts.
| onDrag | null | String | Client side callback to execute while dragging.
| onStop | null | String | Client side callback to execute when dragging stops.

### Getting started with Draggable
Any component can be enhanced with draggable behavior, basically this is achieved by defining the
id of component using the _for_ attribute of draggable.

```xhtml
<p:panel id="pnl" header="Draggable Panel">
    <h:outputText value="This is actually a regular panel" />
</p:panel>
<p:draggable for="pnl"/>
```
If you omit the for attribute, parent component will be selected as the draggable target.

```xhtml
<h:graphicImage id="campnou" value="/images/campnou.jpg">
    <p:draggable />
</h:graphicImage>
```
### Handle
By default any point in dragged component can be used as handle, if you need a specific handle,
you can define it with handle option. Following panel is dragged using it’s header only.

```xhtml
<p:panel id="pnl" header="Draggable Panel">
    <h:outputText value="I can only be dragged using my header" />
</p:panel>
<p:draggable for="pnl" handle="div.ui-panel-titlebar"/>
```

### Drag Axis
Dragging can be limited to either horizontally or vertically.

```xhtml
<p:panel id="pnl" header="Draggable Panel">
    <h:outputText value="I am dragged on an axis only" />
</p:panel>
<p:draggable for="pnl" axis="x or y"/>
```
### Clone
By default, actual component is used as the drag indicator, if you need to keep the component at it’s
original location, use a clone helper.

```xhtml
<p:panel id="pnl" header="Draggable Panel">
    <h:outputText value="I am cloned" />
</p:panel>
<p:draggable for="pnl" helper="clone"/>
```
### Revert
When a draggable is not dropped onto a matching droppable, revert option enables the component
to move back to it’s original position with an animation.

```xhtml
<p:panel id="pnl" header="Draggable Panel">
    <h:outputText value="I will be reverted back to my original position" />
</p:panel>
<p:draggable for="pnl" revert="true"/>
```
### Opacity
During dragging, opacity option can be used to give visual feedback, helper of following panel’s
opacity is reduced in dragging.

```xhtml
<p:panel id="pnl" header="Draggable Panel">
    <h:outputText value="My opacity is lower during dragging" />
</p:panel>
<p:draggable for="pnl" opacity="0.5"/>
```
### Grid
Defining a grid enables dragging in specific pixels. This value takes a comma separated dimensions
in x,y format.

```xhtml
<p:panel id="pnl" header="Draggable Panel">
    <h:outputText value="I am dragged in grid mode" />
</p:panel>
<p:draggable for="pnl" grid="20,40"/>
```

### Containment
A draggable can be restricted to a certain section on page, following draggable cannot go outside of
it’s parent.

```xhtml
<p:outputPanel layout="block" style="width:400px;height:200px;">
    <p:panel id="conpnl" header="Restricted">
        <h:outputText value="I am restricted to my parent's boundaries" />
    </p:panel>
</p:outputPanel>
<p:draggable for="conpnl" containment="parent" />
```

## Droppable

### Info

| Name | Value |
| --- | --- |
| Tag | droppable
| Component Class | org.primefaces.component.dnd.Droppable
| Component Type | org.primefaces.component.Droppable
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DroppableRenderer
| Renderer Class | org.primefaces.component.dnd.DroppableRenderer

### Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Variable name of the client side widget
| for | null | String | Id of the component to add droppable behavior
| disabled | false | Boolean | Disables of enables droppable behavior
| hoverStyleClass | null | String | Style class to apply when an acceptable draggable is dragged over.
| activeStyleClass | null | String | Style class to apply when an acceptable draggable is being dragged.
| onDrop | null | String | Client side callback to execute when a draggable is dropped.
| accept | null | String | Selector to define the accepted draggables.
| scope | null | String | Scope key to match draggables and droppables.
| tolerance | null | String | Specifies the intersection mode to accept a draggable.
| datasource | null | String | Id of a UIData component to connect with.
| greedy | false | Boolean | Avoids parent droppable elements receiving the drop event. Default value is false.

### Getting Started with Droppable
Usage of droppable is very similar to draggable, droppable behavior can be added to any component
specified with the for attribute.

```xhtml
<p:outputPanel id="slot" styleClass="slot" />
<p:droppable for="slot" />
```
slot styleClass represents a small rectangle.

```css
<style type="text/css">
    .slot {
        background:#FF9900;
        width:64px;
        height:96px;
        display:block;
    }
</style>
```
If _for_ attribute is omitted, parent component becomes droppable.

```xhtml
<p:outputPanel id="slot" styleClass="slot">
    <p:droppable />
</p:outputPanel>
```
### Ajax Behavior Events
_drop_ is the only and default ajax behavior event provided by droppable that is processed when a
valid draggable is dropped. In case you define a listener it’ll be executed by passing an
_org.primefaces.event.DragDrop_ event instance parameter holding information about the dragged
and dropped components.

Following example shows how to enable draggable images to be dropped on droppables.

```xhtml
<p:graphicImage id="messi" value="barca/messi_thumb.jpg" />
<p:draggable for="messi"/>
<p:outputPanel id="zone" styleClass="slot" />
<p:droppable for="zone">
    <p:ajax listener="#{ddController.onDrop}" />
</p:droppable>
```
```java
public void onDrop(DragDropEvent ddEvent) {
    String draggedId = ddEvent.getDragId(); //Client id of dragged component
    String droppedId = ddEvent.getDropId(); //Client id of dropped component
    Object data = ddEvent.getData(); //Model object of a datasource
}
```

### onDrop
onDrop is a client side callback that is invoked when a draggable is dropped, it gets two parameters
event and ui object holding information about the drag drop event.

```xhtml
<p:outputPanel id="zone" styleClass="slot" />
<p:droppable for="zone" onDrop="handleDrop"/>
```
```js
function handleDrop(event, ui) {
    var draggable = ui.draggable, //draggable element, a jQuery object
    helper = ui.helper, //helper element of draggable, a jQuery object
    position = ui.position, //position of draggable helper
    offset = ui.offset; //absolute position of draggable helper
}
```
### DataSource
Droppable has special care for data elements that extend from UIData(e.g. datatable, datagrid), in
order to connect a droppable to accept data from a data component define datasource option as the
id of the data component. Example below show how to drag data from datagrid and drop onto a
droppable to implement a dragdrop based selection. Dropped cars are displayed with a datatable.

```java
public class TableBean {
    private List<Car> availableCars;
    private List<Car> droppedCars;

    public TableBean() {
        availableCars = //populate data
    }
    //getters and setters
    public void onCarDrop(DragDropEvent event) {
        Car car = ((Car) ddEvent.getData());
        droppedCars.add(car);
        availableCars.remove(car);
    }
}
```

```xhtml
<h:form id="carForm">
    <p:fieldset legend="AvailableCars">
        <p:dataGrid id="availableCars" var="car" value="#{tableBean.availableCars}" columns="3">
            <p:column>
                <p:panel id="pnl" header="#{car.model}" style="text-align:center">
                    <p:graphicImage value="/images/cars/#{car.manufacturer}.jpg" />
                </p:panel>
                <p:draggable for="pnl" revert="true" handle=".ui-panel-titlebar" stack=".ui-panel"/>
            </p:column>
        </p:dataGrid>
    </p:fieldset>
    <p:fieldset id="selectedCars" legend="Selected Cars" style="margin-top:20px">
        <p:outputPanel id="dropArea">
            <h:outputText value="!!!Drop here!!!" rendered="#{empty tableBean.droppedCars}" style="font-size:24px;" />
            <p:dataTable var="car" value="#{tableBean.droppedCars}" rendered="#{not empty tableBean.droppedCars}">
                <p:column headerText="Model">
                    <h:outputText value="#{car.model}" />
                </p:column>
                <p:column headerText="Year">
                    <h:outputText value="#{car.year}" />
                </p:column>
                <p:column headerText="Manufacturer">
                    <h:outputText value="#{car.manufacturer}" />
                </p:column>
                <p:column headerText="Color">
                    <h:outputText value="#{car.color}" />
                </p:column>
            </p:dataTable>
        </p:outputPanel>
    </p:fieldset>
    <p:droppable for="selectedCars" tolerance="touch" activeStyleClass="ui-state-highlight" datasource="availableCars" onDrop="handleDrop"/>
        <p:ajax listener="#{tableBean.onCarDrop}" update="dropArea availableCars" />
    </p:droppable>
</h:form>
```

```js
<script type="text/javascript">
    function handleDrop(event, ui) {
        ui.draggable.fadeOut(‘fast’); //fade out the dropped item
    }
</script>
```

### Tolerance
There are four different tolerance modes that define the way of accepting a draggable.


| Mode | Description |
| --- | --- |
| fit | draggable should overlap the droppable entirely
| intersect | draggable should overlap the droppable at least 50%
| pointer | pointer of mouse should overlap the droppable
| touch | droppable should overlap the droppable at any amount

### Acceptance
You can limit which draggables can be dropped onto droppables using scope attribute which a
draggable also has. Following example has two images, only first image can be accepted by
droppable.

```xhtml
<p:graphicImage id="messi" value="barca/messi_thumb.jpg" />
<p:draggable for="messi" scope="forward"/>
    <p:graphicImage id="xavi" value="barca/xavi_thumb.jpg" />
<p:draggable for="xavi" scope="midfield"/>
<p:outputPanel id="forwardsonly" styleClass="slot" scope="forward" />
<p:droppable for="forwardsonly" />
```
## Skinning
_hoverStyleClass_ and _activeStyleClass_ attributes are used to change the style of the droppable when
interacting with a draggable.