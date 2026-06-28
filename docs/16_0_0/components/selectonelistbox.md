# SelectOneListbox

SelectOneListbox is an extended version of the standard selectOneListbox component.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectOneListbox.html)

## Getting started with SelectOneListbox
SelectOneListbox usage is same as the standard one.

## Custom Content
Custom content can be displayed for each item using column components.

```xhtml
<p:selectOneListbox value="#{bean.player}" converter="player" var="p">
    <f:selectItems value="#{bean.players}" var="player" itemLabel="#{player.name}" itemValue="#{player}" />
    <p:column>
        <p:graphicImage value="/images/barca/#{p.photo}" width="40"/>
    </p:column>
    <p:column>
        #{p.name} - #{p.number}
    </p:column>
</p:selectOneListbox>
```
## Filtering
Filtering is enabled by setting filter attribute to true. There are four filter modes; `startsWith` ,
`contains`, `endsWith` and `custom`. In custom mode, `filterFunction` must be defined as the name of the
javascript function that takes the item value and filter as parameters to return a boolean to accept or
reject a value. To add a filter to previous example;

```xhtml
<p:selectOneListbox value="#{menuBean.selectedPlayer}" converter="player" var="p" filter="true" filterMatchMode="contains">
    ...
</p:selectOneListbox>
```

## Skinning
SelectOneListbox resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectonelistbox | Main container element.
.ui-selectlistbox-item | Each item in list.
