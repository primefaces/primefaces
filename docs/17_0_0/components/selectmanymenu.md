# SelectManyMenu

SelectManyMenu is an extended version of the standard SelectManyMenu.

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

## Toggle All and Selection Count
_showToggleAll_ displays a checkbox in the header, next to the filter if there is one, to select or unselect all items.
With an active filter it only applies to the items that match the filter. _showSelectionCount_ displays the number of
selected items in the footer, using the `selectionMessage` and `emptySelectionMessage` labels of the locale.

```xhtml
<p:selectManyMenu value="#{bean.selectedOptions}" filter="true" showCheckbox="true" showToggleAll="true" showSelectionCount="true">
    ...
</p:selectManyMenu>
```


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
.p-selectmanymenu | Main container element.
.p-selectlistbox-item | Each item in list.
.p-selectmanymenu-header | Header with the toggle all checkbox.
.p-selectmanymenu-footer | Footer with the number of selected items.

Rows: _styleClass_ accepts `p-rows-striped` (striped rows), with _p:column_ children also `p-rows-gridlines`
(cell borders), `p-rows-sm` and `p-rows-lg` (cell padding). See the [theming](/core/theming.md) variants.
