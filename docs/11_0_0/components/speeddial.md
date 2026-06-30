# SpeedDial

When pressed, a floating action button can display multiple primary actions that can be performed on a page.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SpeedDial-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | speedDial
| Component Class | org.primefaces.component.speeddial.SpeedDial
| Component Type | org.primefaces.component.SpeedDial
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SpeedDialRenderer
| Renderer Class | org.primefaces.component.speeddial.SpeedDialRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
widgetVar | null | String | Name of the client side widget.
disabled | false | Boolean | Whether the component is disabled.
visible | false | Boolean | Specifies the visibility of the overlay.
direction | "up" | String | Specifies the opening direction of actions. Valid values are `up`, `down`, `left`, `right`, `up-left`, `up-right`, `down-left` and `down-right`.
transitionDelay | 30 | Integer | Transition delay step for each action item.
type | "linear" | String | Specifies the opening type of actions. Valid values are `linear`, `circle`, `semi-circle` and `quarter-circle`.
radius | 0 | Integer | Radius for circle types.
mask | false | Boolean | Whether to show a mask element behind the speed dial.
hideOnClickOutside | true | Boolean | Whether the actions close when clicked outside.
style | null | String | Inline style of the element.
styleClass | null | String | Style class of the element.
buttonStyle | null | String | Inline style of the button element.
buttonStyleClass | null | String | Style class of the button element.
maskStyle | null | String | Inline style of the mask element.
maskStyleClass | null | String | Style class of the mask element.
showIcon | "pi pi-plus" | String | Show icon of the button element.
hideIcon | null | String | Hide icon of the button element.
rotateAnimation | true | Boolean | Defined to rotate the showIcon (and hideIcon).
onVisibleChange | null | String | Client side callback to execute when the visibility of element changed.
onClick | null | String | Client side callback to execute when the button element clicked.
onShow | null | String | Client side callback to execute when the actions are visible.
onHide | null | String | Client side callback to execute when the actions are hidden.
keepOpen | false | Boolean | Whether the menu should be kept open on clicking menu items.
badge | null | Object | Badge to render. Either a `String` value or `org.primefaces.model.badge.BadgeModel` instance.

## Getting Started with SpeedDial
When pressed, a floating action button can display multiple primary actions that can be performed on a page. It has a
collection of additional options defined by the menuitem. SpeedDial's position is calculated according to the
container element with the position type style.

```java
@Named
@RequestScoped
public class SpeedDialView {

    public void add() {
        addMessage("Add", "Data Added");
    }

    public void update() {
        addMessage("Update", "Data Updated");
    }

    public void delete() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Delete", "Data Deleted");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
```
```xhtml
<h:form>
    <p:growl id="messages" showDetail="true"/>

    <p:speedDial direction="down">
        <p:menuitem icon="pi pi-pencil" action="#{speedDialView.add}" update="messages"/>
        <p:menuitem icon="pi pi-refresh" action="#{speedDialView.update}" update="messages"/>
        <p:menuitem icon="pi pi-trash" action="#{speedDialView.delete}" update="messages"/>
        <p:menuitem icon="pi pi-upload" outcome="/ui/file/upload/basic"/>
        <p:menuitem icon="pi pi-external-link" url="https://www.primefaces.org"/>
    </p:speedDial>
</h:form>
```
## Dynamic Items
SpeedDial items can be created dynamically as well. PrimeFaces provides the built-in
_org.primefaces.model.DefaultMenuModel_ implementation. Using this structure, dynamic item models can be created.

```java
@Named
@RequestScoped
public class SpeedDialView {

    private MenuModel model;

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();

        DefaultMenuItem item = DefaultMenuItem.builder()
                .icon("pi pi-pencil")
                .command("#{speedDialView.add}")
                .update("messages")
                .build();
        model.getElements().add(item);

        item = DefaultMenuItem.builder()
                .icon("pi pi-refresh")
                .command("#{speedDialView.update}")
                .update("messages")
                .build();
        model.getElements().add(item);

        item = DefaultMenuItem.builder()
                .icon("pi pi-trash")
                .command("#{speedDialView.delete}")
                .update("messages")
                .build();
        model.getElements().add(item);

        item = DefaultMenuItem.builder()
                .icon("pi pi-upload")
                .outcome("/ui/file/upload/basic")
                .build();
        model.getElements().add(item);

        item = DefaultMenuItem.builder()
                .icon("pi pi-external-link")
                .url("https://www.primefaces.org")
                .build();
        model.getElements().add(item);
    }

    public MenuModel getModel() {
        return model;
    }

    public void add() {
        addMessage("Add", "Data Added");
    }

    public void update() {
        addMessage("Update", "Data Updated");
    }

    public void delete() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Delete", "Data Deleted");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
```
```xhtml
<h:form>
    <p:growl id="messages" showDetail="true"/>

    <p:speedDial direction="down" model="#{speedDialView.model}" />
</h:form>
```

## Type
SpeedDial has 4 types; `linear`, `circle`, `semi-circle` and `quarter-circle`.

## Direction
Specifies the opening direction of actions. For the **linear** and **semi-circle** types; `up`, `down`, `left` and `right`.
For the **quarter-circle** type; `up-left`, `up-right`, `down-left` and `down-right`.

## Client Side API
Widget: _PrimeFaces.widget.SpeedDial_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| show() | - | void | Displays multiple action buttons. |
| hide() | - | void | Hides multiple action buttons. |

## Skinning
SpeedDial resides in a container element that using `style` and `styleClass` applies. Following is the list of
structural style classes:

| Class | Applies |
| --- | --- |
.ui-speeddial | Container element.
.ui-speeddial-button | Button element of speeddial.
.ui-speeddial-mask | Mask element of speeddial.
.ui-speeddial-list | List of the actions.
.ui-speeddial-item | Each action item of list.
