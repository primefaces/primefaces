# Tree

Tree is used for displaying hierarchical data and creating site navigations.

## Info

| Name | Value |
| --- | --- |
| Tag | tree
| Component Class | org.primefaces.component.tree.Tree
| Component Type | org.primefaces.component.Tree
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.TreeRenderer
| Renderer Class | org.primefaces.component.tree.TreeRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
value | null | Object | A TreeNode instance as the backing model.
var | null | String | Name of the request-scoped variable that'll be used to refer each treenode data.
dynamic | false | Boolean | Specifies the ajax/client toggleMode
cache | true | Boolean | Specifies caching on dynamically loaded nodes. When set to true expanded nodes will be kept in memory.
onNodeClick | null | String | Javascript event to process when a tree node is clicked.
selection | null | Object | TreeNode array to reference the selections.
style | null | String | Style of the main container element of tree
styleClass | null | String | Style class of the main container element of tree
selectionMode | null | String | Defines the selectionMode
highlight | true | Boolean | Highlights nodes on hover when selection is enabled.
datakey | null | Object | Unique key of the data presented by nodes.
animate | false | Boolean | When enabled, displays slide effect on toggle.
orientation | vertical | String | Orientation of layout, _vertical_ or _horizontal_.
propagateSelectionUp | true | Boolean | Defines upwards selection propagation for checkbox mode.
propagateSelectionDown | true | Boolean | Defines downwards selection propagation for checkbox mode.
dir | ltr | String | Defines text direction, valid values are _ltr_ and _rtl_.
draggable | false | Boolean | Makes tree nodes draggable.
droppable | false | Boolean | Makes tree droppable.
dragdropScope | null | String | Scope key to group a set of tree components for transferring nodes using drag and drop.
dragMode | self | String | Defines parent-child relationship when a node is dragged, valid values are self (default), parent and ancestor.
dropRestrict | none | String | Defines parent-child restrictions when a node is dropped valid values are none (default) and sibling.
required | false | Boolean | Validation constraint for selection.
requiredMessage | null | String | Message for required selection validation.
skipChildren | false | Boolean | Ignores processing of children during lifecycle, improves performance if table only has output components.
showUnselectableCheckbox | false | Boolean | Defines if in checkbox selection mode, a readonly checkbox should be displayed for an unselectable node.
tabindex | 0 | Integer | Position of the element in the tabbing order.
nodeVar | null | String | Name of the request-scoped variable that'll be used to refer current treenode using EL.
filterBy | null | Object | Value expression to be used in filtering.
filterMatchMode | startsWith | String | Match mode for filtering.
disabled | false | Boolean | Disables tree.
multipleDrag | false | Boolean | When enabled, the selected multiple nodes can be dragged from a tree to another tree.
dropCopyNode | false | Boolean | When enabled, the copy of the selected nodes can be dropped from a tree to another tree using Shift key.
onDrop | null | MethodExpression | Method providing suggestions.
filterMode | lenient | String | Mode for filtering valid values are lenient and strict. Default is lenient.

## Getting started with the Tree
Tree is populated with a _org.primefaces.model.TreeNode_ instance which corresponds to the root.

```xhtml
<p:tree value="#{treeBean.root}" var="node">
    <p:treeNode>
        <h:outputText value="#{node}"/>
    </p:treeNode>
</p:tree>
```
```java
public class TreeBean {
    private TreeNode root;

    public TreeBean() {
        root = new TreeNode("Root", null);
        TreeNode node0 = new TreeNode("Node 0", root);
        TreeNode node1 = new TreeNode("Node 1", root);
        TreeNode node2 = new TreeNode("Node 2", root);
        TreeNode node00 = new TreeNode("Node 0.0", node0);
        TreeNode node01 = new TreeNode("Node 0.1", node0);
        TreeNode node10 = new TreeNode("Node 1.0", node1);
        TreeNode node11 = new TreeNode("Node 1.1", node1);
        TreeNode node000 = new TreeNode("Node 0.0.0", node00);
        TreeNode node001 = new TreeNode("Node 0.0.1", node00);
        TreeNode node010 = new TreeNode("Node 0.1.0", node01);
        TreeNode node100 = new TreeNode("Node 1.0.0", node10);
    }
    //getter of root
}
```
## TreeNode vs p:TreeNode
TreeNode API is used to create the node model and consists of _org.primefaces.model.TreeNode_
instances, on the other hand _<p:treeNode />_ represents a component of type
_org.primefaces.component.tree.UITreeNode_. You can bind a TreeNode to a particular p:treeNode
using the _type_ name. Document Tree example in upcoming section demonstrates a sample usage.

## TreeNode API
TreeNode has a simple API to use when building the backing model. For example if you call
node.setExpanded(true) on a particular node, tree will render that node as expanded.

| Property | Type | Description |
| --- | --- | --- |
type | String | type of the treeNode name, default type name is "default".
data | Object | Encapsulated data
children | List<TreeNode> | List of child nodes
parent | TreeNode | Parent node
expanded | Boolean | Flag indicating whether the node is expanded or not

## Dynamic Tree
Tree is non-dynamic by default and toggling happens on client-side. In order to enable ajax toggling
set dynamic setting to true.

```xhtml
<p:tree value="#{treeBean.root}" var="node" dynamic="true">
    <p:treeNode>
        <h:outputText value="#{node}"/>
    </p:treeNode>
</p:tree>
```
_Non-Dynamic:_ When toggling is set to client all the treenodes in model are rendered to the client
and tree is created, this mode is suitable for relatively small datasets and provides fast user
interaction. On the otherhand it’s not suitable for large data since all the data is sent to the client also
client side tree is stateless.

_Dynamic:_ Dynamic mode uses ajax to fetch the treenodes from server side on demand, compared to
the client toggling, dynamic mode has the advantage of dealing with large data because only the
child nodes of the root node is sent to the client initially and whole tree is lazily populated. When a
node is expanded, tree only loads the children of the particular expanded node and send to the client
for display.

## Multiple TreeNode Types
It’s a common requirement to display different TreeNode types with a different UI (eg icon).
Suppose you’re using tree to visualize a company with different departments and different
employees, or a document tree with various folders, files each having a different formats (music,
video). In order to solve this, you can place more than one _<p:treeNode />_ components each having
a different type and use that "type" to bind TreeNode’s in your model. Following example
demonstrates a document explorer. Here is the final output to achieve;


Document Explorer is implemented with four different _<p:treeNode />_ components and additional
CSS skinning to visualize expanded/closed folder icons.

```xhtml
<p:tree value="#{bean.root}" var="doc">
    <p:treeNode expandedIcon="ui-icon ui-icon-folder-open" collapsedIcon="ui-icon ui-icon-folder-collapsed">
        <h:outputText value="#{doc.name}"/>
    </p:treeNode>
    <p:treeNode type="document" icon="ui-icon ui-icon-document">
        <h:outputText value="#{doc.name}" />
    </p:treeNode>
    <p:treeNode type="picture" icon="ui-icon ui-icon-image">
        <h:outputText value="#{doc.name}" />
    </p:treeNode>
    <p:treeNode type="mp3" icon="ui-icon ui-icon-video">
        <h:outputText value="#{doc.name}" />
    </p:treeNode>
</p:tree>
```

```java
public class Bean {
    private TreeNode root;

    public Bean() {
        root = new DefaultTreeNode("root", null);
        TreeNode documents = new DefaultTreeNode("Documents", root);
        TreeNode pictures = new DefaultTreeNode("Pictures", root);
        TreeNode music = new DefaultTreeNode("Music", root);
        TreeNode work = new DefaultTreeNode("Work", documents);
        TreeNode primefaces = new DefaultTreeNode("PrimeFaces", documents);
        //Documents
        TreeNode expenses = new DefaultTreeNode("document", "Expenses.doc", work);
        TreeNode resume = new DefaultTreeNode("document", "Resume.doc", work);
        TreeNode refdoc = new DefaultTreeNode("document", "RefDoc.pages", primefaces);
        //Pictures
        TreeNode barca = new DefaultTreeNode("picture", "barcelona.jpg", pictures);
        TreeNode primelogo = new DefaultTreeNode("picture", "logo.jpg", pictures);
        TreeNode optimus = new DefaultTreeNode("picture", "optimus.png", pictures);
        //Music
        TreeNode turkish = new DefaultTreeNode("Turkish", music);
        TreeNode cemKaraca = new DefaultTreeNode("Cem Karaca", turkish);
        TreeNode erkinKoray = new DefaultTreeNode("Erkin Koray", turkish);
        TreeNode mogollar = new DefaultTreeNode("Mogollar", turkish);
        TreeNode nemalacak = new DefaultTreeNode("mp3", "Nem Alacak Felek Benim", cemKaraca);
        TreeNode resimdeki = new DefaultTreeNode("mp3", "Resimdeki Goz Yaslari", cemKaraca);
        TreeNode copculer = new DefaultTreeNode("mp3", "Copculer", erkinKoray);
        TreeNode oylebirgecer = new DefaultTreeNode("mp3", "Oyle Bir Gecer", erkinKoray);
        TreeNode toprakana = new DefaultTreeNode("mp3", "Toprak Ana", mogollar);
        TreeNode bisiyapmali = new DefaultTreeNode("mp3", "Bisi Yapmali", mogollar);
    }
        //getter of root
}
```
Integration between a TreeNode and a p:treeNode is the type attribute, for example music files in
document explorer are represented using TreeNodes with type "mp3", there’s also a p:treeNode
component with same type "mp3". This results in rendering all music nodes using that particular
p:treeNode representation which displays a note icon. Similarly document and pictures have their
own p:treeNode representations.

Folders on the other hand have two states whose icons are defined by _expandedIcon_ and
_collapsedIcon_ attributes.

## Filtering
Tree has built-in support for filtering that is enabled by using filterBy property.


```xhtml
<p:tree value="#{treeBean.root}" var="node" filterBy="#{node.name}">
    <p:treeNode>
        <h:outputText value="#{node}"/>
    </p:treeNode>
</p:tree>
```
startsWith is the default filter method and filterMatchMode is used to customize this. Valid values
are _startsWith, endsWidth, contains, exact, lt, lte, gt, gte, equals_ and _in_.

## Ajax Behavior Events
Tree provides various ajax behavior events.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
expand | org.primefaces.event.NodeExpandEvent | When a node is expanded.
collapse | org.primefaces.event.NodeCollapseEvent | When a node is collapsed.
select | org.primefaces.event.NodeSelectEvent | When a node is selected.
unselect | org.primefaces.event.NodeUnselectEvent | When a node is unselected.
dragdrop | javax.faces.event.TreeDragDropEvent | When a node is dropped.
contextMenu | org.primefaces.event.NodeSelectEvent | When a context menu is shown.
filter | javax.faces.event.AjaxBehaviorEvent | When the tree gets filtered.

Following tree has three listeners;

```xhtml
<p:tree value="#{treeBean.model}" dynamic="true">
    <p:ajax event="select" listener="#{treeBean.onNodeSelect}" />
    <p:ajax event="expand" listener="#{treeBean.onNodeExpand}" />
    <p:ajax event="collapse" listener="#{treeBean.onNodeCollapse}" />
    ...
</p:tree>
```
```java
public void onNodeSelect(NodeSelectEvent event) {
    String node = event.getTreeNode().getData().toString();
}
public void onNodeExpand(NodeExpandEvent event) {
    String node = event.getTreeNode().getData().toString();
}
public void onNodeCollapse(NodeCollapseEvent event) {
    String node = event.getTreeNode().getData().toString();
}
```
Event listeners are also useful when dealing with huge amount of data. The idea for implementing
such a use case would be providing only the root and child nodes to the tree, use event listeners to
get the selected node and add new nodes to that particular tree at runtime.

## Selection
Node selection is a built-in feature of tree and it supports three different modes. Selection should be
a TreeNode for single case and an array of TreeNodes for multiple and checkbox cases, tree finds
the selected nodes and assign them to your selection model.


_single_ : Only one at a time can be selected, selection should be a TreeNode reference.
_multiple_ : Multiple nodes can be selected, selection should be a TreeNode[] reference.
_checkbox_ : Multiple selection is done with checkbox UI, selection should be a TreeNode[] reference.

```xhtml
<p:tree value="#{treeBean.root}" var="node" selectionMode="checkbox" selection="#{treeBean.selectedNodes}">
    <p:treeNode>
        <h:outputText value="#{node}"/>
    </p:treeNode>
</p:tree>
```
```java
public class TreeBean {
    private TreeNode root;
    private TreeNode[] selectedNodes;

    public TreeBean() {
        root = new CheckboxTreeNode("Root", null);
        //populate nodes
    }
    //getters and setters
}
```
That’s it, now the checkbox based tree looks like below. When the form is submitted with a
command component like a button, selected nodes will be populated in selectedNodes property of
TreeBean. As checkbox selection have a special hierarchy, use _CheckboxTreeNode_ instead.

## Node Caching
When caching is turned on by default, dynamically loaded nodes will be kept in memory so re-
expanding a node will not trigger a server side request. In case it’s set to false, collapsing the node
will remove the children and expanding it later causes the children nodes to be fetched from server
again.

## Handling Node Click
If you need to execute custom javascript when a treenode is clicked, use the _onNodeClick_ attribute.
Your javascript method will be invoked with passing the html element of the _node_ and the click
_event_ as parameters. In case you have datakey defined, you can access datakey on client side by
using node.attr(‘data-datakey’) that represents the data represented by the backing tree model.

## DragDrop
Tree nodes can be reordered within a single tree and can even be transferred between multiple trees
using dragdrop. For a single tree enable draggable and droppable options.


```xhtml
<p:tree value="#{treeBean.root}" var="node" draggable="true" droppable="true">
    <p:treeNode>
        <h:outputText value="#{node}"/>
    </p:treeNode>
</p:tree>
```
For multiple trees, use a scope attribute to match them and configure dragdrop options depending
on your case, following example has 2 trees where one is the source and other is the target. Target
can also be reordered within itself.

```xhtml
<p:tree value="#{treeBean.root1}" var="node" draggable="true" droppable="false" dragdropScope="myscope">
    <p:treeNode>
        <h:outputText value="#{node}"/>
    </p:treeNode>
</p:tree>
<p:tree value="#{treeBean.root2}" var="node" draggable="true" droppable="true" dragdropScope="myscope">
    <p:treeNode>
        <h:outputText value="#{node}"/>
    </p:treeNode>
</p:tree>
```
Two additional options exist for further configuration, _dragMode_ defines the target node that would
be dropped, default value is _self_ and other values are _parent_ and _ancestor_. _dropRestrict_ on the other
hand, can restrict the drop target to be within the parent by setting it to _sibling_.

## Horizontal Tree
Default orientation of tree is vertical, setting it to horizontal displays nodes in an horizontal layout.
All features of vertical tree except dragdrop is available for horizontal tree as well.


## ContextMenu
Tree has special integration with context menu, you can even match different context menus with
different tree nodes using _nodeType_ option of context menu that matches the tree node type. Note
that selection must be enabled in tree component for context menu integration.

```xhtml
<p:contextMenu for="tree">
    <p:menuitem value="View" update="messages" action="#{bean.view}" icon="ui-icon-search" />
    <p:menuitem value="View" update="tree" action="#{bean.delete}" icon="ui-icon-close" />
</p:contextMenu>
<p:tree id="tree" value="#{bean.root}" var="node" selectionMode="single" selection="#{bean.selectedNode}">
    <p:treeNode>
        <h:outputText value="#{node}" />
    </p:treeNode>
</p:tree>
```
## Skinning
Tree resides in a container element which _style_ and _styleClass_ options apply. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-tree | Main container
.ui-tree-container | Root node container.
.ui-treenode | Tree node
.ui-treenode-content | Tree node content
.ui-treenode-icon | Tree node icon
.ui-tree-toggler | Toggle icon
.ui-treenode-label | Tree node label
.ui-treenode-parent | Nodes with children
.ui-treenode-leaf | Nodes without children

As skinning style classes are global, see the main theming section for more information.

