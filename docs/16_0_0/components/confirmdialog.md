# ConfirmDialog

ConfirmDialog is a replacement to the legacy javascript confirmation box. Skinning, customization
and avoiding popup blockers are notable advantages over classic javascript confirmation.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.ConfirmDialog-1.html)

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
<p:confirmDialog widgetVar="cd" header="Confirm">
    <f:facet name="message">
        <h:outputText value="Are you sure?" />
    </f:facet>
    //content
</p:confirmDialog>
```

## Global
Creating a confirmDialog for a specific action is a repetitive task. To solve this global confirmDialog,
which is a singleton, has been introduced. Trigger components need to have p:confirm behavior to
use the confirm dialog. The component that triggers the actual command in the dialog must have the _ui-confirmdialog-yes_
style class. Similarly, the component to cancel the command must have the _ui-confirm-dialog-no_ style class.
At the moment p:confirm is supported by p:commandButton, p:commandLink and p:menuitem.

```xhtml
<p:growl id="messages" />
<p:commandButton value="Save" action="#{bean.save}" update="messages">
    <p:confirm header="Confirmation" message="Sure?" icon="pi pi-exclamation-triangle"/>
</p:commandButton>
<p:confirmDialog global="true">
    <p:commandButton value="Yes" type="button" styleClass="ui-confirmdialog-yes" icon="pi pi-check"/>
    <p:commandButton value="No" type="button" styleClass="ui-confirmdialog-no" icon="pi pi-times"/>
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
