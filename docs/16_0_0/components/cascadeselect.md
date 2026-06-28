# CascadeSelect

CascadeSelect displays a nested structure of options.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.CascadeSelect.html)

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

## Client Side API
Widget: _PrimeFaces.widget.CascadeSelect_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Shows overlay menu.
hide() | - | void | Hides overlay menu.
enable() | - | void | Enables the component.
disable() | - | void | Disables the component.

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