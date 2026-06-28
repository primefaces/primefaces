# SplitButton

SplitButton displays a command by default and additional ones in an overlay.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SplitButton-1.html)

## Getting started with SplitButton
SplitButton usage is similar to a regular commandButton. Additional commands are placed inside
the component and displayed in an overlay. In example below, clicking the save button invokes save
method of the bean and updates messages. Nested options defined as menuitems do ajax, non-ajax
requests as well as regular navigation to an external url.

```xhtml
<p:splitButton value="Save" action="#{buttonBean.save}" update="messages" icon="ui-icon-disk">
    <p:menuitem value="Update" action="#{buttonBean.update}" update="messages" icon="ui-icon-arrowrefresh-1-w"/>
    <p:menuitem value="Delete" action="#{buttonBean.delete}" ajax="false" icon="ui-icon-close"/>
    <p:divider />
    <p:menuitem value="Homepage" url="http://www.primefaces.org" icon="ui-icon-extlink"/>
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
| .ui-splitbutton | Container element.
| .ui-button | Button element
| .ui-splitbutton-menubutton | Dropdown button
| .ui-button-text-only | Button element when icon is not used
| .ui-button-text | Label of button
| .ui-menu | Container element of menu
| .ui-menu-list | List container
| .ui-menuitem | Each menu item
| .ui-menuitem-link | Anchor element in a link item
| .ui-menuitem-text | Text element in an item

As skinning style classes are global, see the main theming section for more information.

