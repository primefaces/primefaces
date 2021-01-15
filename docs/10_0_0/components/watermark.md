# Watermark

Watermark applies a HTML5 placeholder attribute on an target input field.

> :warning: **This component should only be used in environments, where no passthrough attributes are available yet (pre JSF 2.2)!**

[See this widget in the JavaScript API Docs.](../jsdocs/classes/primefaces.widget.watermark.html)

## Info

| Name | Value |
| --- | --- |
| Tag | watermark
| Component Class | org.primefaces.component.watermark.Watermark
| Component Type | org.primefaces.component.Watermark
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.WatermarkRenderer
| Renderer Class | org.primefaces.component.watermark.WatermarkRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Text of watermark.
for | null | String | Component to attach the watermark


## Getting started with Watermark
Watermark requires a input component as target.

```xhtml
<h:inputText id="txt" value="#{bean.searchKeyword}" />
<p:watermark for="txt" value="Search with a keyword" />
```

Preferred way since JSF2.2+, which results in better performance and less memory used:
```xhtml
<h:inputText id="txt" value="#{bean.searchKeyword}" pt:placeholder="Search with a keyword" />
```

