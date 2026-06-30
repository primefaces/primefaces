# Tab

Tab is a generic container component used by other components such as _tabView_ and _accordionPanel_.

## Info

| Name | Value |
| --- | --- |
| Tag | tab
| Component Class | org.primefaces.component.TabView.Tab
| Component Type | org.primefaces.component.Tab
| Component Family | org.primefaces.component |

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
title | null | String | Title text of the tab
titleStyle | null | String | Inline style of the tab.
titleStyleClass | null | String | Style class of the tab.
disabled | false | Boolean | Disables tab element.
closable | false | Boolean | Makes the tab closable when enabled.
titletip | null | String | Tooltip of the tab header.

## Getting started with the Tab
See the sections of components who utilize _tab_ component for more information.
As _tab_ is a shared component, not all attributes may be respected by the parent component like _tabView_.

> :warning: **As it's a shared component, the parent component like _tabView_ is responsible to render the content. Therefore a _tab_ is not updateable via AJAX.**