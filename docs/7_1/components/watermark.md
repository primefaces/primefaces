# Watermark

Watermark displays a hint on an input field.

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
Watermark requires a target of the input component. In case you don't need to support legacy
browsers, prefer placeholder attribute of input components over watermark.

```xhtml
<h:inputText id="txt" value="#{bean.searchKeyword}" />
<p:watermark for="txt" value="Search with a keyword" />
```

## Form Submissions
Watermark is set as the text of an input field which shouldn’t be sent to the server when an
enclosing for is submitted. This would result in updating bean properties with watermark values.
Watermark component is clever enough to handle this case, by default in non-ajax form
submissions, watermarks are cleared. However ajax submissions requires a little manual effort.

Please note that this only applies to legacy browsers, as watermark uses HTML5 placeholder option
when available.

```xhtml
<h:inputText id="txt" value="#{bean.searchKeyword}" />
<p:watermark for="txt" value="Search with a keyword" />
<p:commandButton value="Submit" onclick="PrimeFaces.cleanWatermarks()" oncomplete="PrimeFaces.showWatermarks()" />
```
## Skinning
For browsers that do not support placeholder, there’s only one css style class applying watermark
which is ‘ _.ui-watermark_ ’, you can override this class to bring in your own style.

