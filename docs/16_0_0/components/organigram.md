# Organigram

Organigram is a data component to display an organizational hierarchy.

## Getting Started with Organigram
Organigram requires an instance of OrganigramNode interface as its value to define the root, a
default implementation _DefaultOrganigramNode_ is provided. Each node has a type where each
node at the backend needs to match the p:organigramNode helper component.

```java
public class OrganigramView implements Serializable {
    private OrganigramNode rootNode; 
    private OrganigramNode selection;
    private boolean zoom; 
    private String style = "width: 800px";
    private int leafNodeConnectorHeight; 
    private boolean autoScrollToSelection;
    private String employeeName;

    @PostConstruct
    public void init() {
        selection = new DefaultOrganigramNode(null, "Ridvan Agar", null);
        rootNode = new DefaultOrganigramNode("root", "CommerceBay GmbH", null); 
        rootNode.setCollapsible(false);
        rootNode.setDroppable(true);

        OrganigramNode softwareDevelopment = addDivision(rootNode, "Software Development", "Ridvan Agar");
        OrganigramNode teamJavaEE = addDivision(softwareDevelopment, "Team JavaEE"); 
        addDivision(teamJavaEE, "Jakarta Faces", "Thomas Andraschko");
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

## Organigram Model API
Refer to JavaDocs for more information about Organigram Model API.

