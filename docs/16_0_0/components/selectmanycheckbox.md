# SelectManyCheckbox

SelectManyCheckbox is an extended version of the standard SelectManyCheckbox.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectManyCheckbox.html)

## Getting started with SelectManyCheckbox
SelectManyCheckbox usage is same as the standard one.

## Layouts
There are five layouts options for the checkboxes;

- **lineDirection** : Checkboxes are displayed horizontally
- **pageDirection** : Checkboxes are displayed vertically
- **grid** : Checkboxes are displayed in grid with given columns value.
- **responsive** : This is same as grid except, checkboxes are displayed vertically after a certain
    screen breakpoint.
- **custom** : In this mode, selectManyCheckbox leaves the layout to you using p:checkbox
    components. Next section provides more detail about this mode.

## Custom Layout
Custom layout is defined using the standalone checkbox components that match the selectItems of
the selectManyCheckbox by their itemIndex option. Note that in this mode selectManyCheckbox
does not render anything visible.

Following example uses customizable Grid CSS layout.


```xhtml
<p:selectManyCheckbox id="custom" value="#{checkboxView.selectedConsoles2}" layout="custom">
    <f:selectItem itemLabel="Xbox One" itemValue="Xbox One" />
    <f:selectItem itemLabel="PS4" itemValue="PS4" />
</p:selectManyCheckbox>
<div class="ui-grid ui-grid-responsive">
    <div class="ui-grid-row">
        <div class="ui-grid-col-6">
            <p:outputLabel for="opt1" value="Xbox One" style="display:block"/>
            <p:checkbox id="opt1" for="custom" itemIndex="0" />
        </div>
        <div class="ui-grid-col-6">
            <p:outputLabel for="opt2" value="PS4" style="display:block"/>
            <p:checkbox id="opt2" for="custom" itemIndex="1" />
        </div>
    </div>
</div>
```


## Client Side API
Widget: _PrimeFaces.widget.SelectManyCheckbox_


| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |

## Skinning
SelectManyCheckbox resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectmanycheckbox | Main container element.
.ui-chkbox | Container of a checkbox.
.ui-chkbox-box | Container of checkbox icon.
.ui-chkbox-icon | Checkbox icon.
