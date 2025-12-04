# Tag

Tag component is used to categorize content.

## Info

| Name | Value |
| --- | --- |
| Tag | tag
| Component Class | org.primefaces.component.tag.Tag
| Component Type | org.primefaces.component.Tag
| Component Family | org.primefaces.component
| Renderer Type | org.primefaces.component.TagRenderer
| Renderer Class | org.primefaces.component.tag.TagRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| value | null | Object | Value to display inside the tag.
| converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
| severity | null | String | Severity type of the tag.
| rounded | false | Boolean | Whether the corners of the tag are rounded.
| icon | null | String | Icon of the tag to display next to the value.
| style | null | String | Style of the tag.
| styleClass | null | String | StyleClass of the tag.

## Getting Started
Content of the tag is specified using the ```value``` property.

```xhtml
<p:tag value="New"></p:tag>
```

## Icon
An icon can also be configured to be displayed next to the value with the ```icon``` property.

```xhtml
<p:tag value="2" severity="success" icon="pi pi-check"></p:tag>
```

## Severities
Different color options are available as severity levels.

* success
* info
* warning
* danger
* secondary
* help

## Templating
Content can easily be customized with the default slot instead of using the built-in display.

```xhtml
<p:tag>
    Content
</p:tag>
```

## Skinning of Tag
Tag resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
|.ui-tag | Tag element
|.ui-tag-rounded | Rounded element
|.ui-tag-icon | Icon of the tag
|.ui-tag-value	| Value of the tag
