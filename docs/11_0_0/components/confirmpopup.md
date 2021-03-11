# ConfirmPopup

ConfirmPopup displays a confirmation overlay displayed relatively to its target.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_primefaces.primefaces.widget.confirmpopup-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | confirmPopup
| Component Class | org.primefaces.component.confirmpopup.ConfirmPopup
| Component Type | org.primefaces.component.ConfirmPopup
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ConfirmPopupRenderer
| Renderer Class | org.primefaces.component.confirmpopup.ConfirmPopupRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget.
| message | null | String | Text to be displayed in body.
| icon | null | String | Icon to display next to the message.
| style | null | String | Inline style of the popup container.
| styleClass | null | String | Style class of the popup container
| appendTo | null | String | Appends the popup to the element defined by the given search expression.
| global | false | Boolean | When enabled, confirmPopup becomes a shared for other components that require confirmation.
| dismissable | true | Boolean | Enables to hide the popup when outside is clicked.

## Getting started with ConfirmPopup
ConfirmPopup has two modes; global and non-global. Non-Global mode is almost same as the
dialog component or overlaypanel component used with a simple client side api, _show(selector)_ and _hide()_.

```xhtml
<h:form>
    <p:commandButton type="button" onclick="PF('cp').show(this)" />
    <p:confirmPopup message="Are you sure about destroying the world?" header="Initiating destroy process" severity="alert" widgetVar="cp">
        <p:commandButton value="Yes Sure" action="#{buttonBean.destroyWorld}" update="messages" oncomplete="PF('cp').hide()"/>
        <p:commandButton value="Not Yet" onclick="PF('cp').hide();" type="button" />
    </p:confirmPopup>
</h:form>
```
## Message
Message can be defined in two ways, either via message option or message facet. Message facet is
useful if you need to place custom content instead of simple text.

```xhtml
<p:confirmPopup widgetVar="cp" header=”Confirm”>
    <f:facet name="message">
        <h:outputText value="Are you sure?" />
    </f:facet>
    //content
</p:confirmPopup>
```

## Global
Creating a confirmPopup for a specific action is a repetitive task, to solve this global confirmPopup
which is a singleton has been introduced. Trigger components need to have p:confirm _type="popup"_ behavior to
use the confirm popup. Component that trigger the actual command in popup must have _ui-confirm-
popup-yes_ style class, similarly component to cancel the command must have _ui-confirm-popup-no_.
At the moment p:confirm is supported by p:commandButton, p:commandLink and p:menuitem.

```xhtml
<p:growl id="messages" />
<p:commandButton value="Save" action="#{bean.save}" update="messages">
    <p:confirm header="Confirmation" message="Sure?" icon="ui-icon-alert"/>
</p:commandButton>
<p:confirmPopup global="true">
    <p:commandButton value="Yes" type="button" styleClass="ui-confirm-popup-yes" icon="ui-icon-check"/>
    <p:commandButton value="No" type="button" styleClass="ui-confirm-popup-no" icon="ui-icon-close"/>
</p:confirmPopup>
```
## Client Side API
Widget: _PrimeFaces.widget.ConfirmPopup_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| show(selector) | selector or DOM element | void | Displays popup.
| hide() | - | void | Closes popup.

## Skinning
ConfirmPopup resides in a main container element which _style_ and _styleClass_ options apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-confirm-popup | Container element.
| .ui-confirm-content | Content element.
| .ui-confirm-popup-icon | Message icon.
| .ui-confirm-popup-message | Message text.
| .ui-confirm-popup-footer | Footer element for buttons.
