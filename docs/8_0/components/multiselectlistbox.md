# MultiSelectListbox

MultiSelectListbox is used to select an item from a collection of listboxes that are in parent-child
relationship.

## Info

| Name | Value |
| --- | --- |
| Tag | multiSelectListbox
| Component Class | org.primefaces.component.multiselectlistbox.MultiSelectListbox
| Component Type | org.primefaces.component.MultiSelectListbox
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.MultiSelectListboxRenderer
| Renderer Class | org.primefaces.component.multiselectlistbox.MultiSelectListboxRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
widgetVar | null | String | Name of the client side widget.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
disabled | false | Boolean | If true, disables the component.
effect | null | String | Effect to use when showing a group of items.
showHeaders | false | Boolean | Displays label of a group at header section of the children items.
header | null | String | Label of the root group items.

## Getting started with MultiSelectListbox
MultiSelectListbox needs a collection of SelectItemGroups.

```java
public class MultiSelectListboxBean {
    private List<SelectItem> categories;
    private String selection;

    @PostConstruct
    public void init() {
        categories = new ArrayList<SelectItem>();
        SelectItemGroup group1 = new SelectItemGroup("Group 1");
        SelectItemGroup group2 = new SelectItemGroup("Group 2");
        SelectItemGroup group3 = new SelectItemGroup("Group 3");
        SelectItemGroup group4 = new SelectItemGroup("Group 4");
        SelectItemGroup group11 = new SelectItemGroup("Group 1.1");
        SelectItemGroup group12 = new SelectItemGroup("Group 1.2");
        SelectItemGroup group21 = new SelectItemGroup("Group 2.1");
        SelectItem option31 = new SelectItem("Option 3.1", "Option 3.1");
        SelectItem option32 = new SelectItem("Option 3.2", "Option 3.2");
        SelectItem option33 = new SelectItem("Option 3.3", "Option 3.3");
        SelectItem option34 = new SelectItem("Option 3.4", "Option 3.4");
        SelectItem option41 = new SelectItem("Option 4.1", "Option 4.1");
        SelectItem option111 = new SelectItem("Option 1.1.1");
        SelectItem option112 = new SelectItem("Option 1.1.2");
        group11.setSelectItems(new SelectItem[]{option111, option112});
        SelectItem option121 = new SelectItem("Option 1.2.1", "Option 1.2.1");
        SelectItem option122 = new SelectItem("Option 1.2.2", "Option 1.2.2");
        SelectItem option123 = new SelectItem("Option 1.2.3", "Option 1.2.3");
        group12.setSelectItems(new SelectItem[]{option121, option122, option123});
        SelectItem option211 = new SelectItem("Option 2.1.1", "Option 2.1.1");
        group21.setSelectItems(new SelectItem[]{option211});
        group1.setSelectItems(new SelectItem[]{group11, group12});
        group2.setSelectItems(new SelectItem[]{group21});
        group3.setSelectItems(new SelectItem[]{option31, option32, option33, option34});
        group4.setSelectItems(new SelectItem[]{option41});
        categories.add(group1);
        categories.add(group2);
        categories.add(group3);
        categories.add(group4);
    }
    //getters-setters of categories and selection
}
```

```xhtml
<p:multiSelectListbox value="#{multiSelectListboxBean.selection}">
    <f:selectItems value="#{multiSelectListboxBean.categories}" />
</p:multiSelectListbox>
```
**Note** that SelectItemGroups are not selectable, only values of SelectItems can be passed to the bean.

## Effects
An animation is executed during toggling of a group, following options are available for _effect_
attribute; blind, bounce, clip, drop, explode, fold, highlight, puff, pulsate, scale, shake, size, slide
(suggested).

## Client Side API
Widget: _PrimeFaces.widget.MultiSelectListbox_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
enable() | - | void | Enables the component.
disable() | - | void | Disables the component.
showItemGroup(item) | li element as jQuery object | void | Shows subcategories of a given item.

## Skinning
MultiSelectListbox resides in a main container which _style_ and _styleClass_ attributes apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-multiselectlistbox | Main container element.
.ui-multiselectlistbox-list | List container.
.ui-multiselectlistbox-item | Each item in a list.
