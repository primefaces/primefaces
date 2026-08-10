# CommandButton

CommandButton is an extended version of standard commandButton with ajax and theming.

## Getting started with CommandButton
CommandButton usage is similar to standard commandButton, by default commandButton submits
its enclosing form with ajax.

```xhtml
<p:commandButton value="Save" action="#{bookBean.saveBook}" />
```
```java
public class BookBean {
    public void saveBook() {
    //Save book
    }
}
```
## Reset Buttons
Reset buttons do not submit the form, just resets the form contents.

```xhtml
<p:commandButton type="reset" value="Reset" />
```
## Push Buttons
Push buttons are used to execute custom javascript without causing an ajax/non-ajax request. To
create a push button set type as "button".

```xhtml
<p:commandButton type="button" value="Alert" onclick="alert(‘Prime’)" />
```
## AJAX and Non-AJAX
CommandButton has built-in ajax capabilities, ajax submit is enabled by default and configured
using _ajax_ attribute. When ajax attribute is set to false, form is submitted with a regular full page
refresh.

The _update_ attribute is used to partially update other component(s) after the ajax response is
received. Update attribute takes a comma or white-space separated list of Jakarta Faces component ids to be
updated. Basically any Jakarta Faces component, not just PrimeFaces components should be updated with the
Ajax response. In the following example, form is submitted with ajax and _display_ outputText is
updated with the ajax response.

```xhtml
<h:form>
    <h:inputText value="#{bean.text}" />
    <p:commandButton value="Submit" update="display"/>
    <h:outputText value="#{bean.text}" id="display" />
</h:form>
```

**Tip**: You can use the ajaxStatus component to notify users about the ajax request.


## Icons
An icon on a button is provided using _icon_ option. iconPos is used to define the position of the
button which can be “left” or “right”.

```xhtml
<p:commandButton value="With Icon" icon="disk"/>
<p:commandButton icon="disk"/>
```
.disk is a simple css class with a background property;

```css
.disk {
    background-image: url(‘disk.png’) !important;
}
```
You can also use the pre-defined icons from ThemeRoller like _ui-icon-search_.

## Ajax loading indicator icon
A loading indicator will be shown by default when the button triggers an Ajax request. This is done based on the
`ui-state-loading` class which is toggled on the button.

If you don't want this default styling, you can suppress it using the following custom CSS rules:

```css
html .ui-state-loading.ui-button-text-only .ui-icon-loading + .ui-button-text {
    opacity: inherit;
}
html .ui-state-loading .ui-icon-loading {
    display: none;
}
html .ui-state-loading .ui-icon:not(.ui-icon-loading) {
    display: inherit;
}
```

## Client Side API
Widget: _PrimeFaces.widget.CommandButton_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| disable() | - | void | Disables button
| enable() | - | void | Enables button

## Skinning
CommandButton renders a button tag which _style_ and _styleClass_ applies. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-button | Button element
| .ui-button-text-only | Button element when icon is not used
| .ui-button-text | Label of button
| .ui-state-loading | Button element; when `disableOnAjax` is set and an Ajax request triggered by the button is in progress.

As skinning style classes are global, see the main theming section for more information.
