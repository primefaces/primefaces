# Responsive Design

There are three ingridients to make a responsive page with PrimeFaces.

## Page Layout
Page layout typically consists of the menus, header, footer and the content section. A responsive
page layout should optimize these sections according to the screen size. You may create your own
layout with CSS, pick one from a responsive css framework or choose PrimeFaces Premium
Layouts such as Sentinel, Spark, Modena, Rio and more.

## Grid Framework
Grid framework is used to define container where you place the content and the components. A
typical grid framework usually consists of columns with varying widths and since they are also
responsive, containers adjust themselves according to the screen size. There are 3rd party grid
frameworks you can use whereas PrimeFaces also provides Grid CSS as a solution.

## Components
Components also must be flexible enough to use within a responsive layout, if a component has
fixed width, it will not work well with a responsive page layout and grid framework as it does not
adjust its dimensions based on its container. There are two important points in PrimeFaces
components related to responsive design.

First is the fluid mode support for components where component gets 100% width meaning when
used within a grid, it will take the width of the grid. Fluid usually effects the form components. To
enable fluid mode, add _ui-fluid_ to a container element.

```xhtml
<div class="ui-fluid">
    <p:panelGrid columns="2" layout="grid">
        <p:outputLabel for="input" value="Input"/>
        <p:inputText id="input"/>
    </p:panelGrid>
</div>
```
Second is the built-in responsive modes for complex components such Dialog, Charts, Carousel and
PickList. These types of components get a responsive attribute, when enabled they hook-in to
screen size change to optimize their content.

```xhtml
<p:dialog responsive="true"...
```
For a detailed example of a responsive page that uses all of the parts above, visit;

http://www.primefaces.org/showcase/ui/misc/responsive.xhtml

Source code is available at GitHub.
