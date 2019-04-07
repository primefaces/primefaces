# Steps

Steps components is an indicator for the steps in a workflow. Layout of steps component is
optimized for responsive design.

## Info

| Name | Value |
| --- | --- |
| Tag | steps
| Component Class | org.primefaces.component.steps.Steps
| Component Type | org.primefaces.component.Steps
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.StepsRenderer
| Renderer Class | org.primefaces.component.steps.StepsRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
model | null | MenuModel | MenuModel instance to build menu dynamically.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
activeIndex | 0 | Integer | Index of the active tab.
widgetVar | null | String | Name of the client side widget.
readonly | true | Boolean | Defines whether items would be clickable or not.

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
.ui-steps | Main container element.
.ui-steps-item | Step element.
.ui-steps-name | Name of the step.
.ui-steps-item | Number of the step.

As skinning style classes are global, see the main theming section for more information. Here is an
example of a styled steps having "custom" as its styleClass;

```css
.ui-steps.custom {
    margin-bottom: 30px;
}
.ui-steps.custom .ui-steps-item .ui-menuitem-link {
    height: 10px;
    padding: 0 1em;
}
.ui-steps.custom .ui-steps-item .ui-steps-number {
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
