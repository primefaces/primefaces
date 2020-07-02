# ConfirmDialog

ConfirmDialog is a replacement to the legacy javascript confirmation box. Skinning, customization
and avoiding popup blockers are notable advantages over classic javascript confirmation.

## Info

| Name | Value |
| --- | --- |
| Tag | confirmDialog
| Component Class | org.primefaces.component.confirmdialog.ConfirmDialog
| Component Type | org.primefaces.component.ConfirmDialog
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ConfirmDialogRenderer
| Renderer Class | org.primefaces.component.confirmdialog.ConfirmDialogRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget.
| message | null | String | Text to be displayed in body.
| header | null | String | Text for the header.
| severity | null | String | Message severity for the displayed icon.
| width | auto | Integer | Width of the dialog in pixels
| height | auto | Integer | Width of the dialog in pixels
| style | null | String | Inline style of the dialog container.
| styleClass | null | String | Style class of the dialog container
| closable | true | Boolean | Defines if close icon should be displayed or not
| appendTo | null | String | Appends the dialog to the element defined by the given search expression.
| visible | false | Boolean | Whether to display confirm dialog on load.
| showEffect | null | String | Effect to use on showing dialog.
| hideEffect | null | String | Effect to use on hiding dialog.
| closeOnEscape | false | Boolean | Defines if dialog should hide on escape key.
| dir | ltr | String | Defines text direction, valid values are ltr and rtl.
| global | false | Boolean | When enabled, confirmDialog becomes a shared for other components that require confirmation.
| responsive | false | Boolean | In responsive mode, dialog adjusts itself based on screen width.

## Getting started with ConfirmDialog
ConfirmDialog has two modes; global and non-global. Non-Global mode is almost same as the
dialog component used with a simple client side api, _show()_ and _hide()_.

```xhtml
<h:form>
    <p:commandButton type="button" onclick="PF('cd').show()" />
    <p:confirmDialog message="Are you sure about destroying the world?" header="Initiating destroy process" severity="alert" widgetVar="cd">
        <p:commandButton value="Yes Sure" action="#{buttonBean.destroyWorld}" update="messages" oncomplete="PF('cd').hide()"/>
        <p:commandButton value="Not Yet" onclick="PF('cd').hide();" type="button" />
    </p:confirmDialog>
</h:form>
```
## Message and Severity
Message can be defined in two ways, either via message option or message facet. Message facet is
useful if you need to place custom content instead of simple text. Note that header can also be
defined using the _header_ attribute or the _header_ facet. Severity defines the icon to display next to
the message, default severity is _alert_ and the other option is _info_.

```xhtml
<p:confirmDialog widgetVar="cd" header=”Confirm”>
    <f:facet name="message">
        <h:outputText value="Are you sure?" />
    </f:facet>
    //content
</p:confirmDialog>
```

## Global
Creating a confirmDialog for a specific action is a repetitive task, to solve this global confirmDialog
which is a singleton has been introduced. Trigger components need to have p:confirm behavior to
use the confirm dialog. Component that trigger the actual command in dialog must have _ui-confirm-
dialog-yes_ style class, similarly component to cancel the command must have _ui-confirm-dialog-no_.
At the moment p:confirm is supported by p:commandButton, p:commandLink and p:menuitem.

```xhtml
<p:growl id="messages" />
<p:commandButton value="Save" action="#{bean.save}" update="messages">
    <p:confirm header="Confirmation" message="Sure?" icon="ui-icon-alert"/>
</p:commandButton>
<p:confirmDialog global="true">
    <p:commandButton value="Yes" type="button" styleClass="ui-confirmdialog-yes" icon="ui-icon-check"/>
    <p:commandButton value="No" type="button" styleClass="ui-confirmdialog-no" icon="ui-icon-close"/>
</p:confirmDialog>
```
## Client Side API
Widget: _PrimeFaces.widget.ConfirmDialog_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| show() | - | void | Displays dialog.
| hide() | - | void | Closes dialog.

## Skinning
ConfirmDialog resides in a main container element which _style_ and _styleClass_ options apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-dialog | Container element of dialog
| .ui-dialog-titlebar | Title bar
| .ui-dialog-title | Header text
| .ui-dialog-titlebar-close | Close icon
| .ui-dialog-content | Dialog body
| .ui-dialog-buttonpane | Footer button panel
