# Badge

Badge is a small status indicator for another element.

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

* success
* info
* warning
* danger
* secondary
* help

```xhtml
<p:badge value="2" severity="success"></p:badge>
```

## Sizes
Badge sizes are adjusted with the `size` property that accepts "large" and "xlarge" as the possible alternatives
to the default size.
```xhtml
<p:badge value="2"></p:badge>
<p:badge value="4" size="large" severity="warning"></p:badge>
<p:badge value="6" size="xlarge" severity="success"></p:badge>
```

## Skinning of Badge
Badge resides in a main container element which `style` and `styleClass` options apply. As skinning
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
|.ui-badge-secondary | Badge element with secondary severity.
|.ui-badge-help | Badge element with help severity.
|.ui-badge-lg | Large badge element
|.ui-badge-xl | Extra large badge element
