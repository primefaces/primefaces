# Tag

Tag component is used to categorize content.

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
