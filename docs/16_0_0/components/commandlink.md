# CommandLink

CommandLink extends standard Jakarta Faces commandLink with Ajax capabilities.

## Getting Started with CommandLink
CommandLink is used just like the standard h:commandLink, difference is form is submitted with
ajax by default.


```java
public class BookBean {
    public void saveBook() {
    //Save book
    }
}
```
```xhtml
<p:commandLink action="#{bookBean.saveBook}">
    <h:outputText value="Save" />
</p:commandLink>
```

## Client Side API
Widget: `PrimeFaces.widget.CommandLink`

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| disable() | - | void | Disables the link.
| enable() | - | void | Enables the link.

## Skinning
CommandLink renders an HTML anchor element that `style` and `styleClass` attributes apply.
Following is the list of structural style classes;

| Class | Applies |
| --- | --- |
| .ui-state-loading | Anchor element; when `disableOnAjax` is set and an Ajax request triggered by the link is in progress.
