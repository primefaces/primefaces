# Stack

Stack is a navigation component that mimics the stacks feature in Mac OS X.

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