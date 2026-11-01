# Steps

Steps components is an indicator for the steps in a workflow. Layout of steps component is
optimized for responsive design.

## Getting started with Steps
Steps requires menuitems as children components, each menuitem is rendered as a step. Just like in
any other menu component, menuitems can be utilized to do ajax requests, non-ajax requests and
simple GET navigations.

```xhtml
<p:steps activeIndex="1">
    <p:menuitem value="Personal" />
    <p:menuitem value="Seat Selection" />
    <p:menuitem value="Payment" />
    <p:menuitem value="Confirmation" />
</p:steps>
```
## Skinning Steps
Steps resides in a main container which _style_ and _styleClass_ attributes apply. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.p-steps | Main container element.
.p-steps-item | Step element.
.p-steps-title | Title of the step.
.p-steps-number | Number of the step.

As skinning style classes are global, see the main theming section for more information. Here is an
example of a styled steps having "custom" as its styleClass;

```css
.p-steps.custom {
    margin-bottom: 30px;
}
.p-steps.custom .p-steps-item .p-menuitem-link {
    height: 10px;
    padding: 0 1em;
}
.p-steps.custom .p-steps-item .p-steps-number {
    background-color: #0081c2;
    color: #FFFFFF;
    display: inline-block;
    width: 30px;
    border-radius: 10px;
    margin-top: -10px;
    margin-bottom: 10px;
    margin-bottom: 10px;
}
```
