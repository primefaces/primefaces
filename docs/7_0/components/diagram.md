# Diagram

Diagram is generic component to create visual elements and connect them on a web page. SVG is
used on modern browsers and VML on IE 8 and below. Component is highly flexible in terms of
api, events and theming.

## Info

| Name | Value |
| --- | --- |
| Tag | diagram
| Component Class | org.primefaces.component.diagram.Diagram
| Component Type | org.primefaces.component.Diagram
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DiagramRenderer
| Renderer Class | org.primefaces.component.diagram.DiagramRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget
| value | null | String | Model of the diagram.
| converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id.
| style | null | String | Inline style of the diagram.
| styleClass | null | String | Style class of the diagram.

## Getting started with the Diagram
Diagram requires a backend model to display.

```xhtml
<p:diagram value="#{diagramBasicView.model}" style="height:400px" />
```
There are various concepts in diagram model;

- **Element**: Main type to be connected.
- **EndPoint**: Ports of elements to be used in connection.
- **Connector**: Connector to join elements.
- **Overlay**: Decorators over connectors and endpoints.

```java
public class BasicView {
    private DefaultDiagramModel model;

    @PostConstruct
    public void init() {
        model = new DefaultDiagramModel();
        model.setMaxConnections(-1);
        Element elementA = new Element("A", "20em", "6em");
        elementA.addEndPoint(new DotEndPoint(EndPointAnchor.BOTTOM));
        Element elementB = new Element("B", "10em", "18em");
        elementB.addEndPoint(new DotEndPoint(EndPointAnchor.TOP));
        Element elementC = new Element("C", "40em", "18em");
        elementC.addEndPoint(new DotEndPoint(EndPointAnchor.TOP));
        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);
        model.connect(new Connection(elementA.getEndPoints().get(0),
        elementB.getEndPoints().get(0)));
        model.connect(new Connection(elementA.getEndPoints().get(0),
        elementC.getEndPoints().get(0)));
    }
    public DiagramModel getModel() {
        return model;
    }
}
```
In diagram above, there are 3 elements each having endpoints of dot type. After adding them to the
model, 2 connections are made, first one being A to B and second one from A to C.


## Elements
Elements are the main part of diagram. Styling is done with css and positioning can be done using
model. An element should have at least width and height defined to be displayed on page. They can
be styled globally using .ui-diagram-element style class or individually using the styleClass
property on DiagramElement class.

## EndPoints
EndPoints are the ports available on an element that can be used for connections. An endpoint has a
location defined by EndPointAnchor. Anchors can be static like "TopLeft" or dynamic like
"AutoDefault". There are 4 types of EndPoints differentiated by their shapes;

- BlankEndPoint
- DotEndPoint
- RectangleEndPoint
- ImagEndPoint

An endpoint is added to an element using addEndPoint api;

```java
element.addEndPoint(new DotEndPoint(EndPointAnchor.TOP));
```
## Connections
A connection requires two endpoints, connector and optional decorators like overlays. There are
four connector types;

- Bezier
- FlowChart
- Straight
- StateMachine

Default is bezier and it can be customized using default connector method globally in model or at
connection level.

#### Global

```java
DiagramModel model = new DefaultDiagramModel();
FlowChartConnector connector = new FlowChartConnector();
connector.setPaintStyle("{strokeStyle:'#C7B097',lineWidth:3}");
model.setDefaultConnector(connector);
```
#### Per Connection

```java
model.connect(new Connection(elementA.getEndPoints().get(0),
elementB.getEndPoints().get(0), new FlowChartConnector()));
```
## Overlays
Overlays are decorators for connectors and endpoints. Available ones are;


- ArrowOverlay
- DiamondOverlay
- LabelOverlay

Example below adds label and arrow for the connector;

```java
Connection conn = new Connection(from, to);
conn.getOverlays().add(new ArrowOverlay(20, 20, 1, 1));
conn.getOverlays().add(new LabelOverlay(label, "flow-label", 0.5));
```
## Dynamic Diagrams
A diagram can be edited after being initialized, to create new connections an element should be set
as source and to receive new connections it should be a target. Ajax event callbacks such as
"connect", "disconnect" and "connectionChange" are available.

```java
ElementA.setSource(true);
ElementB.setTarget(true);
```
## Ajax Behavior Events
Diagram provides ajax behavior event callbacks invoked by interactive diagrams.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| connect | org.primefaces.event.diagram.ConnectEvent | On new connection.
| disconnect | org.primefaces.event.diagram.DisconnectEvent | When a connection is removed.
| connectionChange | org.primefaces.event.diagram.ConnectionChange | When a connection has changed.
