# Splitter

Splitter component is used to categorize content.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/primefaces.widget.splitter.html)

## Info

| Name | Value |
| --- | --- |
| Tag | splitter
| Component Class | org.primefaces.component.splitter.Splitter
| Component Type | org.primefaces.component.Splitter
| Component Family | org.primefaces.component
| Renderer Type | org.primefaces.component.SplitterRenderer
| Renderer Class | org.primefaces.component.splitter.SplitterRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| layout | horizontal | String | Orientation of the panels, valid values are "horizontal" and "vertical".
| gutterSize | 4 | Integer | Size of the divider in pixels.
| stateKey | null | string | Storage identifier of a stateful Splitter.
| stateStorage | session | string | Defines where a stateful splitter keeps its state, valid values are "session" for sessionStorage and "local" for localStorage.
| style | null | String | Style of the splitter.
| styleClass | null | String | StyleClass of the splitter.

## Getting Started
Splitter requires two SplitterPanel components to wrap.

```xhtml
<p:splitter style="height: 300px">
    <p:splitterPanel>
        Panel 1
    </p:splitterPanel>
    <p:splitterPanel>
        Panel 2
    </p:splitterPanel>
</p:splitter>
```

## Multiple Panels
Any number of panels can be nested inside a Splitter.

```xhtml
<p:splitter style="height: 300px">
    <p:splitterPanel>
        Panel 1
    </p:splitterPanel>
    <p:splitterPanel>
        Panel 2
    </p:splitterPanel>
    <p:splitterPanel>
        Panel 3
    </p:splitterPanel>
    <p:splitterPanel>
        Panel 4
    </p:splitterPanel>
</p:splitter>
```

## Layout
Default orientation is configured with the ```layout``` property and default is the "horizontal" whereas other alternative is the "vertical".

```xhtml
<p:splitter style="height: 300px" layout="vertical">
    <p:splitterPanel>
        Panel 1
    </p:splitterPanel>
    <p:splitterPanel>
        Panel 2
    </p:splitterPanel>
</p:splitter>
```

## Initial Sizes
When no size is defined, panels are split 50/50, use the ```size``` property to give relative widths e.g. 20/80.

```xhtml
<p:splitter style="height: 300px">
    <p:splitterPanel :size="20" >
        Panel 1
    </p:splitterPanel>
    <p:splitterPanel  :size="80">
        Panel 2
    </p:splitterPanel>
</p:splitter>
```

## Minimum Size
Minimum size defines the lowest boundary for the size of a panel.

```xhtml
<p:splitter style="height: 300px">
    <p:splitterPanel :size="20" :minSize="10">
        Panel 1
    </p:splitterPanel>
    <p:splitterPanel  :size="80" :minSize="20">
        Panel 2
    </p:splitterPanel>
</p:splitter>
```

## Nested Panels
Splitters can be combined to create advanced layouts.

```xhtml
<p:splitter style="height: 300px">
    <p:splitterPanel styleClass="p-d-flex p-ai-center p-jc-center" :size="20" :minSize="10">
        Panel 1
    </p:splitterPanel>
    <p:splitterPanel :size="80">
        <p:splitter layout="vertical">
            <p:splitterPanel styleClass="p-d-flex p-ai-center p-jc-center" :size="15">
                Panel 2
            </p:splitterPanel>
            <p:splitterPanel :size="85">
                <p:splitter>
                    <p:splitterPanel styleClass="p-d-flex p-ai-center p-jc-center" :size="20">
                        Panel 3
                    </p:splitterPanel>
                    <p:splitterPanel styleClass="p-d-flex p-ai-center p-jc-center" :size="80">
                        Panel 4
                    </p:splitterPanel>
                </p:splitter>
            </p:splitterPanel>
        </p:splitter>
    </p:splitterPanel>
</p:splitter>
```

## Stateful
Splitters can be configured as stateful so that when the user visits the page again, the adjusts sizes can be restored. 
Define a ```stateKey``` to enable this feature. Default location of the state is session storage and other option is the 
local storage which can be configured using the ```stateStorage``` property.

```xhtml
<p:splitter stateKey="mykey" stateStorage="local">
    <p:splitterPanel>
        Panel 1
    </p:splitterPanel>
    <p:splitterPanel>
        Panel 2
    </p:splitterPanel>
</p:splitter>
```

## Skinning of Splitter
Splitter resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
| .ui-splitter | Container element.
| .ui-splitter | Container element during resize.
| .ui-splitter-horizontal | Container element with horizontal layout.
| .ui-splitter-vertical | Container element with vertical layout.
| .ui-splitter-panel | Splitter panel element.
| .ui-splitter-gutter | Gutter element to use when resizing the panels.
| .ui-splitter-gutter-handle | Handle element of the gutter.
