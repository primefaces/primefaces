# CascadeSelect

CascadeSelect displays a nested structure of options.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_primefaces.primefaces.widget.cascadeselect.html)

## Info

| Name | Value |
| --- | --- |
| Tag | cascadeSelect
| Component Class | org.primefaces.component.cascadeselect.CascadeSelect
| Component Type | org.primefaces.component.CascadeSelect
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.CascadeSelectRenderer
| Renderer Class | org.primefaces.component.cascadeselect.CascadeSelectRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component.
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method expression that refers to a method validationg the input
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
var | null | String | Name of the item iterator.
widgetVar | null | String | Name of the client side widget.
disabled | false | Boolean | Disables the component.
appendTo | null | String | Appends the overlay to the element defined by search expression. Defaults to document body.
placeholder | null | String | The placeholder attribute specifies a short hint that describes the expected value of an input field.
tabindex | null | String | Tabindex of the input.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the container.

## Getting started with CascadeSelect

```java
public class MenuBean {
    private List<SelectItem> countries;
    private String selection;

    @PostConstruct
    public void init() {
        countries = new ArrayList<>();
        SelectItemGroup group1 = new SelectItemGroup("Australia");
        SelectItemGroup group2 = new SelectItemGroup("Canada");
        SelectItemGroup group3 = new SelectItemGroup("United States");

        SelectItemGroup group11 = new SelectItemGroup("New South Wales");
        SelectItemGroup group12 = new SelectItemGroup("Queensland");

        SelectItemGroup group21 = new SelectItemGroup("Quebec");
        SelectItemGroup group22 = new SelectItemGroup("Ontario");

        SelectItemGroup group31 = new SelectItemGroup("California");
        SelectItemGroup group32 = new SelectItemGroup("Florida");
        SelectItemGroup group33 = new SelectItemGroup("Texas");

        SelectItem option111 = new SelectItem("Sydney");
        SelectItem option112 = new SelectItem("Newcastle");
        SelectItem option113 = new SelectItem("Wollongong");
        group11.setSelectItems(new SelectItem[]{option111, option112, option113});

        SelectItem option121 = new SelectItem("Brisbane");
        SelectItem option122 = new SelectItem("Townsville");
        group12.setSelectItems(new SelectItem[]{option121, option122});

        SelectItem option211 = new SelectItem("Montreal");
        SelectItem option212 = new SelectItem("Quebec City");
        group21.setSelectItems(new SelectItem[]{option211, option212});

        SelectItem option221 = new SelectItem("Ottawa");
        SelectItem option222 = new SelectItem("Toronto");
        group22.setSelectItems(new SelectItem[]{option221, option222});

        SelectItem option311 = new SelectItem("Los Angeles");
        SelectItem option312 = new SelectItem("San Diego");
        SelectItem option313 = new SelectItem("San Francisco");
        group31.setSelectItems(new SelectItem[]{option311, option312, option313});

        SelectItem option321 = new SelectItem("Jacksonville");
        SelectItem option322 = new SelectItem("Miami");
        SelectItem option323 = new SelectItem("Tampa");
        SelectItem option324 = new SelectItem("Orlando");
        group32.setSelectItems(new SelectItem[]{option321, option322, option323, option324});

        SelectItem option331 = new SelectItem("Austin");
        SelectItem option332 = new SelectItem("Dallas");
        SelectItem option333 = new SelectItem("Houston");
        group33.setSelectItems(new SelectItem[]{option331, option332, option333});

        group1.setSelectItems(new SelectItem[]{group11, group12});
        group2.setSelectItems(new SelectItem[]{group21, group22});
        group3.setSelectItems(new SelectItem[]{group31, group32, group33});

        countries.add(group1);
        countries.add(group2);
        countries.add(group3);
    }
    //getters and setters
}
```
```xhtml
<p:cascadeSelect value="#{myBean.selection}" placeholder="Select a City">
    <f:selectItems value="#{myBean.countries}"/>
</p:cascadeSelect>
```

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specific the default event is called.  
In addition to the custom "itemSelect" event is also available to invoke when an item is selected from dropdown.  
  
**Default Event:** itemSelect 

```xhtml
<p:ajax event="itemSelect" listener="#{bean.handleItemSelect}" update="msgs" />
```

## Client Side API
Widget: _PrimeFaces.widget.CascadeSelect_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Shows overlay menu.
hide() | - | void | Hides overlay menu.

## Skinning
CascadeSelect resides in a container element that _style_ and _styleClass_ attributes apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;


| Class | Applies | 
| --- | --- | 
.ui-cascadeselect	| Container element.
.ui-cascadeselect-label | Element to display label of selected option.
.ui-cascadeselect-trigger | Icon element.
.ui-cascadeselect-panel | Panel element.
.ui-cascadeselect-items-wrapper | Wrapper element of items list.
.ui-cascadeselect-items | List element of items.
.ui-cascadeselect-item | An item in the list.