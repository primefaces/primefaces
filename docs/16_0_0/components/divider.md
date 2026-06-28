# Divider

Divider is used to separate contents.

## Getting Started
Divider has two orientations defined with the ```layout``` property, default is "horizontal" and the alternative is 
"vertical".

```xhtml
<div>Content 1</div>
<p:divider />
<div>Content 2</div>
```

## Border Style
Style of the border is configured with the ```type``` property and supports 3 values; default is "solid" and other 
possibilities are "dashed" and "dotted".

```xhtml
<div>Content 1</div>
<p:divider type="dashed"/>
<div>Content 2</div>
```

## Vertical Divider
Vertical divider is enabled by setting the ```layout``` property as "vertical".

```xhtml
<div class="p-d-flex">
    <div>Content 1</div>
    <p:divider layout="vertical" />
    <div>Content 2</div>
    <p:divider layout="vertical" />
    <div>Content 3</div>
</div>
```

## Content
Any content placed inside is rendered within the boundaries of the divider. In addition, location of the content 
is configured with the ```align``` property. In horizontal layout, alignment options are "left", "center" and "right" 
whereas vertical mode supports "top", "center" and "bottom".

```xhtml
<div>Content 1</div>

<p:divider align="left">
    <div class="p-d-inline-flex p-ai-center">
        <i class="pi pi-user p-mr-2"></i>
        <b>Icon</b>
    </div>
</p:divider>

<div>Content 2</div>

<p:divider align="center">
    <span class="p-tag">Badge</span>
</p:divider>

<div>Content 3</div>

<p:divider align="right">
    <p:button value="Button" icon="pi pi-search"></p:button>
</p:divider>

<div>Content 4</div>
```

## Skinning of Divider
Divider resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
|.ui-divider | Container element.
|.ui-divider-horizontal | Container element in horizontal layout.
|.ui-divider-vertical | Container element in vertical layout.
|.ui-divider-solid | Container element with solid border.
|.ui-divider-dashed | Container element with dashed border.
|.ui-divider-dotted | Container element with dotted border.
|.ui-divider-left | Container element with content aligned to left.
|.ui-divider-right | Container element with content aligned to right.
|.ui-divider-center | Container element with content aligned to center.
|.ui-divider-bottom | Container element with content aligned to bottom.
|.ui-divider-top | Container element with content aligned to top.
