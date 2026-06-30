# Stack

Stack is a navigation component that mimics the stacks feature in Mac OS X.

## Info

| Name | Value |
| --- | --- |
| Tag | stack
| Component Class | org.primefaces.component.stack.Stack
| Component Type | org.primefaces.component.Stack
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.StackRenderer
| Renderer Class | org.primefaces.component.stack.StackRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
icon | null | String | An optional image to contain stacked items.
openSpeed | 300 | String | Speed of the animation when opening the stack.
closeSpeed | 300 | Integer | Speed of the animation when opening the stack.
widgetVar | null | String | Name of the client side widget.
model | null | MenuModel | MenuModel instance to create menus programmatically
expanded | false | Boolean | Whether to display stack as expanded or not.

## Getting started with Stack
Each item in the stack is represented with menuitems. Stack below has five items with different
icons and labels.

```xhtml
<p:stack icon="/images/stack/stack.png">
    <p:menuitem value="Aperture" icon="/images/stack/aperture.png" url="#"/>
    <p:menuitem value="Photoshop" icon="/images/stack/photoshop.png" url="#"/>
    //...
</p:stack>
```
Initially stack will be rendered in collapsed mode;

## Location
Stack is a fixed positioned element and location can be change via css. There’s one important css
selector for stack called _.ui-stack._ Override this style to change the location of stack.

```css
.ui-stack {
    bottom: 28px;
    right: 40px;
}
```
## Dynamic Menus
Menus can be created programmatically as well, see the dynamic menus part in menu component
section for more information and an example.

## Skinning

| Class | Applies | 
| --- | --- | 
.ui-stack | Main container element of stack
.ui-stack ul li | Each item in stack
.ui-stack ul li img | Icon of a stack item
.ui-stack ul li span | Label of a stack item