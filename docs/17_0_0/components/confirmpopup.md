# ConfirmPopup

ConfirmPopup displays a confirmation overlay displayed relatively to its target.

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
<p:confirmPopup widgetVar="cp" header="Confirm">
    <f:facet name="message">
        <h:outputText value="Are you sure?" />
    </f:facet>
    //content
</p:confirmPopup>
```

## Global
Creating a confirmPopup for a specific action is a repetitive task. To solve this global confirmPopup,
which is a singleton, has been introduced. Trigger components need to have p:confirm _type="popup"_ behavior to
use the confirm popup. The component that triggers the actual command in the popup must have _p-confirm-
popup-yes_ style class. Similarly, the component to cancel the command must have _p-confirm-popup-no_ style class.
At the moment p:confirm is supported by p:commandButton, p:commandLink and p:menuitem.

```xhtml
<p:growl id="messages" />
<p:commandButton value="Save" action="#{bean.save}" update="messages">
    <p:confirm header="Confirmation" message="Sure?" icon="pi pi-exclamation-triangle"/>
</p:commandButton>
<p:confirmPopup global="true">
    <p:commandButton value="Yes" type="button" styleClass="p-confirm-popup-yes" icon="pi pi-check"/>
    <p:commandButton value="No" type="button" styleClass="p-confirm-popup-no" icon="pi pi-times"/>
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
| .p-confirm-popup | Container element.
| .p-confirm-popup-content | Content element.
| .p-confirm-popup-icon | Message icon.
| .p-confirm-popup-message | Message text.
| .p-confirm-popup-footer | Footer element for buttons.
