# Grid CSS

Grid CSS is a lightweight responsive layout utility optimized for mobile devices, tablets and
desktops. Up to 12 columns are supported based on fluid layout.

## Getting Started with Grid
Grid CSS is based on a 12 column layout. Columns are prefixed with ui-g-* and they should be a
descendant of a container having ui-g class. A simple 3 column layout can be defined as;

```xhtml
<div class="ui-g">
    <div class="ui-g-4">Col1</div>
    <div class="ui-g-4">Col2</div>
    <div class="ui-g-4">Col3</div>
</div>
```

## Multi Line
When the number of columns exceed 12, columns wrap to a new line.

```xhtml
<div class="ui-g">
    <div class="ui-g-6">Col1</div>
    <div class="ui-g-6">Col2</div>
    <div class="ui-g-6">Col3</div>
    <div class="ui-g-6">Col4</div>
</div>
```
Same can also be achieved by having two ui-g containers to semantically define a row.

```xhtml
<div class="ui-g">
    <div class="ui-g-6">Col1</div>
    <div class="ui-g-6">Col2</div>
</div>
<div class="ui-g">
    <div class="ui-g-6">Col3</div>
    <div class="ui-g-6">Col4</div>
</div>
```
## Nested
Columns can be nested to create more complex layouts.

```xhtml
<div class="ui-g">
    <div class="ui-g-8 ui-g-nopad">
        <div class="ui-g-6">6</div>
        <div class="ui-g-6">6</div>
        <div class="ui-g-12">12</div>
    </div>
    <div class="ui-g-4">4</div>
</div>
```
Direct children of ui-g has the same height automatically, in example above if the inside columns is
likely to have different height with different content.

```xhtml
<div class="ui-g">
    <div class="ui-g-8 ui-g-nopad">
        <div class="ui-g-6">6 <br /><br /><br /> Content </div>
        <div class="ui-g-6">6</div>
        <div class="ui-g-12">12</div>
    </div>
    <div class="ui-g-4">4</div>
</div>
```

Solution is wrapping the internal divs inside a ui-g as well.

```xhtml
<div class="ui-g">
    <div class="ui-g-8 ui-g-nopad">
        <div class="ui-g">
            <div class="ui-g-6">6 <br /><br /><br /> Content </div>
            <div class="ui-g-6">6</div>
            <div class="ui-g-12">12</div>
        </div>
    </div>
    <div class="ui-g-4">4</div>
</div>
```
## Responsive

Responsive layout is achieved by applying additional classes to the columns whereas ui-g-* define
the default behavior. Four screen sizes are supported with different breakpoints.
#### Prefix Devices Media Query Example
- _ui-sm-*_ Phones max-width: 40em (640px) ui-sm-6, ui-sm-4
- _ui-md-*_ Tablets min-width: 40.063em (641px) ui-md-2, ui-sm-8
- _ui-lg-*_ Desktops min-width: 64.063em (1025px) ui-lg-6, ui-sm-12
- _ui-xl-*_ Big screen monitors min-width: 90.063em (1441px) ui-xl-2, ui-sm-10

Most of the time, ui-md-* styles are used with default ui-g-* classes, to customize small or large
screens apply ui-sm, ui-lg and ui-xl can be utilized.

In example below, large screens display 4 columns, medium screens display 2 columns in 2 rows
and default behavior gets only displayed in a mobile phone where each column is rendered in a
separate row.

```xhtml
<div class="ui-g">
    <div class="ui-g-12 ui-md-6 ui-lg-3">ui-g-12 ui-md-6 ui-lg-3</div>
    <div class="ui-g-12 ui-md-6 ui-lg-3">ui-g-12 ui-md-6 ui-lg-3</div>
    <div class="ui-g-12 ui-md-6 ui-lg-3">ui-g-12 ui-md-6 ui-lg-3</div>
    <div class="ui-g-12 ui-md-6 ui-lg-3">ui-g-12 ui-md-6 ui-lg-3</div>
</div>
```
In this second example below, 3 columns are displayed in large screens and in medium screens first
two columns are placed side by side where the last column displayed below them. In a mobile
phone, they all get displayed in a separate row.

```xhtml
<div class="ui-g">
    <div class="ui-g-12 ui-md-6 ui-lg-4">ui-g-12 ui-md-6 ui-lg-4</div>
    <div class="ui-g-12 ui-md-6 ui-lg-4">ui-g-12 ui-md-6 ui-lg-4</div>
    <div class="ui-g-12 ui-lg-4">ui-g-12 ui-lg-4</div>
</div>
```
**Note**: A column has a default padding by default, to remove it you may apply ui-g-nopad style class.
