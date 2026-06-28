# SelectManyMenu

SelectManyMenu is an extended version of the standard SelectManyMenu.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectManyMenu.html)

## Getting started with SelectManyMenu
SelectManyMenu usage is same as the standard one.

## Custom Content
Custom content can be displayed for each item using column components.


```xhtml
<p:selectManyMenu value="#{bean.selectedPlayers}" converter="player" var="p">
    <f:selectItems value="#{bean.players}" var="player" itemLabel="#{player.name}" itemValue="#{player}" />
    <p:column>
        <p:graphicImage value="/images/barca/#{p.photo}" width="40"/>
    </p:column>
    <p:column>
        #{p.name} - #{p.number}
    </p:column>
</p:selectManyMenu>
```
## Filtering
Filtering is enabled by setting filter attribute to true. There are four filter modes; `startsWith`,
`contains`, `endsWith` and `custom`. In custom mode, `filterFunction` must be defined as the name of the
javascript function that takes the item value and filter as parameters to return a boolean to accept or
reject a value. To add a filter to previous example;

```xhtml
<p:selectManyMenu value="#{menuBean.selectedPlayer}" converter="player" var="p" filter="true" filterMatchMode="contains">
    ...
</p:selectManyMenu>
```
## Checkbox
SelectManyMenu has built-in support for checkbox based multiple selection, when enabled by
_showCheckbox_ option, checkboxes are displayed next to each column.


## Client Side API
Widget: _PrimeFaces.widget.SelectManyMenu_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
selectAll() | - | void | Selects all the options.
unselectAll() | - | void | Unselects all the options.
select(item) | item | void | Select the item.
unselect(item) | item | void | Unselect the item.
focus(item) | item | void | Focus the item.

## Skinning
SelectManyMenu resides in a container that _style_ and _styleClass_ attributes apply. As skinning style
classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectmanymenu | Main container element.
.ui-selectlistbox-item | Each item in list.
