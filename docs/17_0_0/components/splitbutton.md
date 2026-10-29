# SplitButton

SplitButton displays a command by default and additional ones in an overlay.

## Getting started with SplitButton
SplitButton usage is similar to a regular commandButton. Additional commands are placed inside
the component and displayed in an overlay. In example below, clicking the save button invokes save
method of the bean and updates messages. Nested options defined as menuitems do ajax, non-ajax
requests as well as regular navigation to an external url.

```xhtml
<p:splitButton value="Save" action="#{buttonBean.save}" update="messages" icon="pi pi-save">
    <p:menuitem value="Update" action="#{buttonBean.update}" update="messages" icon="pi pi-refresh"/>
    <p:menuitem value="Delete" action="#{buttonBean.delete}" ajax="false" icon="pi pi-times"/>
    <p:divider />
    <p:menuitem value="Homepage" url="http://www.primefaces.org" icon="pi pi-external-link"/>
</p:splitButton>
```
## Client Side API
Widget: `PrimeFaces.widget.SplitButton`

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| show() | - | void | Displays overlay.
| hide() | - | void | Hides overlay.
| disable() | - | void | Disables the button.
| enable() | - | void | Enables the button.

## Skinning
SplitButton renders a container element which `style` and `styleClass` applies. Following is the list of
structural style classes;

| Class | Applies |
| --- | --- |
| .p-splitbutton | Container element.
| .p-button | Button element
| .p-splitbutton-menubutton | Dropdown button
| .p-button-text-only | Button element when icon is not used
| .p-button-text | Label of button
| .p-menu | Container element of menu
| .p-menu-list | List container
| .p-menuitem | Each menu item
| .p-menuitem-link | Anchor element in a link item
| .p-menuitem-text | Text element in an item

As skinning style classes are global, see the main theming section for more information.

