# SpeedDial

When pressed, a floating action button can display multiple primary actions that can be performed on a page.

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
