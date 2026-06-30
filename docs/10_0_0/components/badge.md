# Badge

Badge is a small status indicator for another element.

## Info

| Name | Value |
| --- | --- |
| Tag | badge
| Component Class | org.primefaces.component.badge.Badge
| Component Type | org.primefaces.component.Badge
| Component Family | org.primefaces.component
| Renderer Type | org.primefaces.component.BadgeRenderer
| Renderer Class | org.primefaces.component.badge.BadgeRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| value | null | String | Value to display inside the badge.
| severity | null | String | Severity type of the badge.
| size | null | String | Size of the badge, valid options are "large" and "xlarge".
| style | null | String | Style of the badge.
| styleClass | null | String | StyleClass of the badge.

## Getting Started
Badge can either be used as a standalone component, or it may contain components.

```xhtml
<p:badge value="2"></p:badge>
<p:badge value="2">
    <i class="pi pi-bell"/>
</p:badge>
```

## Severities
Different color options are available as severity levels.

```xhtml
<p:badge value="2" severity="success"></p:badge>
```

## Sizes
Badge sizes are adjusted with the ```size``` property that accepts "large" and "xlarge" as the possible alternatives 
to the default size.
```xhtml
<p:badge value="2"></p:badge>
<p:badge value="4" size="large" severity="warning"></p:badge>
<p:badge value="6" size="xlarge" severity="success"></p:badge>
```

## Skinning of Badge
Badge resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
|.ui-badge | Badge element
|.ui-overlay-badge | Wrapper of a badge and its target.
|.ui-badge-dot | Badge element with no value.
|.ui-badge-success | Badge element with success severity.
|.ui-badge-info | Badge element with info severity.
|.ui-badge-warning | Badge element with warning severity.
|.ui-badge-danger | Badge element with danger severity.
|.ui-badge-lg | Large badge element
|.ui-badge-xl | Extra large badge element
