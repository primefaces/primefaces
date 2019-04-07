# Organigram

Organigram is a data component to display an organizational hierarchy.

## Info

| Name | Value |
| --- | --- |
| Tag | organigram
| Component Class | org.primefaces.component.organigram.Organigram
| Component Type | org.primefaces.component.Organigram
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.OrganigramRenderer
| Renderer Class | org.primefaces.component.organigram.OrganigramRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Organigram Node | Model instance.
var | null | String | Name of the request-scoped variable that'll be used to refer each treenode data during rendering.
selection | null | Organigram Node | OrganigramNode to reference the selection.
LeafNodeConnectorHeight | 10 | Integer | The height of the connector line for leaf nodes.
zoom | false | Boolean | Defines if zoom controls are rendered.
autoScrollToSelection | false | Boolean | Auto scroll to the selected node on rendering if enabled.
style | null | String | Style of the main container element of organigram.
styleClass | null | String | Style class of the main container element of organigram.
widgetVar | null | String | Name of the client side widget.

## Getting Started with Organigram
Organigram requires an instance of OrganigramNode interface as its value to define the root, a
default implementation _DefaultOrganigramNode_ is provided. Each node has a type where each
node at the backend needs to match the p:organigramNode helper component.

```java
public class OrganigramView implements Serializable {
    private OrganigramNode rootNode; 
    private OrganigramNode selection;
    private boolean zoom = false; 
    private String style = "width: 800px";
    private int leafNodeConnectorHeight = 0; 
    private boolean autoScrollToSelection = false;
    private String employeeName;

    @PostConstruct
    public void init() {
        selection = new DefaultOrganigramNode(null, "Ridvan Agar", null);
        rootNode = new DefaultOrganigramNode("root", "CommerceBay GmbH", null); 
        rootNode.setCollapsible(false);
        rootNode.setDroppable(true);

        OrganigramNode softwareDevelopment = addDivision(rootNode, "Software Development", "Ridvan Agar");
        OrganigramNode teamJavaEE = addDivision(softwareDevelopment, "Team JavaEE"); 
        addDivision(teamJavaEE, "JSF", "Thomas Andraschko");
        addDivision(teamJavaEE, "Backend", "Marie Louise");
        OrganigramNode teamMobile = addDivision(softwareDevelopment, "Team Mobile"); 
        addDivision(teamMobile, "Android", "Andy Ruby");
        addDivision(teamMobile, "iOS", "Stevan Jobs");
        addDivision(rootNode, "Managed Services", "Thorsten Schultze", "Sandra Becker");
        OrganigramNode marketing = addDivision(rootNode, "Marketing"); 
        addDivision(marketing, "Social Media", "Ali Mente", "Lisa Boehm");
        addDivision(marketing, "Press", "Michael Gmeiner", "Hans Peter");
        addDivision(rootNode, "Management", "Hassan El Manfalouty"); 
    }
    protected OrganigramNode addDivision(OrganigramNode parent, String name, String... employees) {
        OrganigramNode divisionNode = new DefaultOrganigramNode("division", name, parent); 
        divisionNode.setDroppable(true);
        divisionNode.setDraggable(true); 
        divisionNode.setSelectable(true);
        
        if (employees != null) { 
            for (String employee : employees) {
                OrganigramNode employeeNode = new DefaultOrganigramNode("employee", employee, divisionNode);
                employeeNode.setSelectable(true); 
                employeeNode.setDraggable(true);
            } 
        }
        return divisionNode; 
    }
}
```

```xhtml
<p:organigram id="organigram" widgetVar="organigram" value="#{organigramView.rootNode}" var="node" leafNodeConnectorHeight="#{organigramView.leafNodeConnectorHeight}"
    autoScrollToSelection="#{organigramView.autoScrollToSelection}" zoom="#{organigramView.zoom}" selection="#{organigramView.selection}" 
    style="#{organigramView.style}">
    <p:organigramNode>
        <h:outputText value="#{node.data}" />
    </p:organigramNode>
    <p:organigramNode type="root" style="border-radius: 10px;">
        <h:outputText value="#{node.data}" />
    </p:organigramNode>
    <p:organigramNode type="division" styleClass="division" icon="ui-icon-suitcase" iconPos="left">
        <h:outputText value="#{node.data}" />
    </p:organigramNode>
    <p:organigramNode type="employee" styleClass="employee" icon="ui-icon-person">
        <h:outputText value="#{node.data}" />
    </p:organigramNode>
</p:organigram>
```
## Ajax Behavior Events
Organigram provides the following custom ajax behavior events.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
dragdrop | org.primefaces.event.organigram.OrganigramNodeDragDropEvent | When a node is reordered with drag-drop.
select | org.primefaces.event.organigram.OrganigramNodeSelectEvent | When a node is selected.
contextmenu | org.primefaces.event.organigram.OrganigramNodeSelectEvent | When a node is selected with right click.
collapse | org.primefaces.event.organigram.OrganigramNodeCollapseEvent | When a node is collapsed.
expand | org.primefaces.event.organigram.OrganigramNodeExpandEvent | When a node is expanded.

## Organigram Model API
Refer to JavaDocs for more information about Organigram Model API.

