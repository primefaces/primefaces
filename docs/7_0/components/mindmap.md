# Mindmap

Mindmap is an interactive tool to visualize mindmap data featuring lazy loading, callbacks,
animations and more.

## Info

| Name | Value |
| --- | --- |
| Tag | mindmap
| Component Class | org.primefaces.component.mindmap.Mindmap
| Component Type | org.primefaces.component.Mindmap
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.MindmapRenderer
| Renderer Class | org.primefaces.component.mindmap.MindmapRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | MindmapNode | MenuModel instance to build menu dynamically.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
effectSpeed | 300 | Integer | Speed of the animations in milliseconds.
widgetVar | null | String | Name of the client side widget.

## Getting started with Mindmap
Mindmap requires an instance of _org.primefaces.model.mindmap.MindmapNode_ as the root. Due to
it’s lazy nature, a select ajax behavior must be provided to load children of selected node on the fly
with ajax.

```java
public class MindmapBean {
    private MindmapNode root;

    public MindmapBean() {
        root = new DefaultMindmapNode("google.com", "Google", "FFCC00", false);
        MindmapNode ips = new DefaultMindmapNode("IPs", "IP Nos", "6e9ebf", true);
        MindmapNode ns = new DefaultMindmapNode("NS(s)", "Names", "6e9ebf", true);
        MindmapNode mw = new DefaultMindmapNode("Mw", "Malicious ", "6e9ebf", true);
        root.addNode(ips);
        root.addNode(ns);
        root.addNode(malware);
    }
    public MindmapNode getRoot() {
        return root;
    }
    public void onNodeSelect(SelectEvent event) {
        MindmapNode node = (MindmapNode) event.getObject();
        //load children of select node and add via node.addNode(childNode);
    }
}
```
```xhtml
<p:mindmap value="#{mindmapBean.root}" style="width:100%;height:600px">
    <p:ajax event="select" listener="#{mindmapBean.onNodeSelect}" />
</p:mindmap>
```

## DoubleClick Behavior
Selecting a node with single click is used to load subnodes, double click behavior is also provided
for further customization. Following sample, displays the details of the subnode in a dialog.

```xhtml
<p:mindmap value="#{mindmapBean.root}" style="width:100%;height:600px;">
    <p:ajax event="select" listener="#{mindmapBean.onNodeSelect}" />
    <p:ajax event="dblselect" listener="#{mindmapBean.onNodeDblselect}" update="output" oncomplete="PF('details').show()"/>
</p:mindmap>
<p:dialog widgetVar="details" header="Node Details" resizable="false" modal="true" showEffect="fade" hideEffect="fade">
    <h:outputText id="output" value="#{mindmapBean.selectedNode.data}" />
</p:dialog>
```
```java
public void onNodeDblselect(SelectEvent event) {
    this.selectedNode = (MindmapNode) event.getObject();
}
```
## MindmapNode API
_org.primefaces.model.mindmap.MindmapNode_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
label | null | String | Label of the node.
data | null | Object | Optional data associated with the node.
fill | null | String | Color code of the node.
selectable | 1 | Boolean | Flag to define if node is clickable.
parent | null | MindmapNode | Parent node instance.

**Tips**

- IE 7 and IE 8 are not supported due to technical limitations, IE 9 is supported.

