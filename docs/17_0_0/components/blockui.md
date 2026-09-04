# BlockUI

BlockUI is used to block interactivity of Jakarta Faces components with optional ajax integration.

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
| isBlocking()   | none | boolean | Is the widget currently blocking

## Skinning
Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-blockui | Container element.
| .ui-blockui-content | Container for custom content.

As skinning style classes are global, see the main theming section for more information.

## Tips

- BlockUI does not support absolute or fixed positioned components. e.g. dialog.
