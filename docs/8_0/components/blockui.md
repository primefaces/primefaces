# BlockUI

BlockUI is used to block interactivity of JSF components with optional ajax integration.

## Info

| Name | Value |
| --- | --- |
| Tag | blockUI
| Component Class | org.primefaces.component.blockui.BlockUI
| Component Type | org.primefaces.component.BlockUI
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.BlockUIRenderer
| Renderer Class | org.primefaces.component.blockui.BlockUIRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget.
| trigger | null | String | Identifier of the component(s) to bind.
| block | null | String | Search expression for block targets.
| blocked | false | Boolean | Blocks the UI by default when enabled.
| animate | true | Boolean | When disabled, displays block without animation effect.
| styleClass | null | String | Style class of the component.

## Getting Started with BlockUI
BlockUI requires _trigger_ and _block_ attributes to be defined. With the special ajax integration, ajax
requests whose source are the trigger components will block the ui onstart and unblock oncomplete.
Example below blocks the ui of the panel when saveBtn is clicked and unblock when ajax response
is received.

```xhtml
<p:panel id="pnl" header="My Panel">
    //content
    <p:commandButton id="saveBtn" value="Save" />
</p:panel>
<p:blockUI block="pnl" trigger="saveBtn" />
```
Multiple triggers are defined as a comma separated list. Multiple block targets are also possible
using a search expression.

```xhtml
<p:blockUI block="pnl" trigger="saveBtn,deleteBtn,updateBtn" />
```

## Custom Content
In order to display custom content like a loading text and animation, place the content inside the
blockUI.

```xhtml
<p:dataTable id="dataTable" var="car" value="#{tableBean.cars}" paginator="true" rows="10">
    <p:column>
        <f:facet name="header">
            <h:outputText value="Model" />
        </f:facet>
        <h:outputText value="#{car.model}" />
    </p:column>
    //more columns
</p:dataTable>
<p:blockUI block="dataTable" trigger="dataTable"> 
    LOADING<br />
    <p:graphicImage value="/images/ajax-loader.gif"/>
</p:blockUI>
```

## Client Side API
Widget: _PrimeFaces.widget.BlockUI_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| show(duration) | duration: (optional) duration of the animation | void | Blocks the UI.
| hide(duration) | duration: (optional) duration of the animation | void | Unblocks the UI

## Skinning
Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-blockui | Container element.
| .ui-blockui-content | Container for custom content.

As skinning style classes are global, see the main theming section for more information.

## Tips

- BlockUI does not support absolute or fixed positioned components. e.g. dialog.
