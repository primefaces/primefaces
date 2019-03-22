# NotificationBar

NotificationBar displays a multipurpose fixed positioned panel for notification.

## Info

| Name | Value |
| --- | --- |
| Tag | notificationBar
| Component Class | org.primefaces.component.notificationbar.NotificationBar
| Component Type | org.primefaces.component.NotificatonBar
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.NotificationBarRenderer
| Renderer Class | org.primefaces.component.notificationbar.NotificationBarRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
style | null | String | Style of the container element
styleClass | null | String | StyleClass of the container element
position | top | String | Position of the bar, "top" or "bottom".
effect | fade | String | Name of the effect, "fade", "slide" or "none".
effectSpeed | normal | String | Speed of the effect, "slow", "normal" or "fast".
autoDisplay | false | Boolean | When true, panel is displayed on page load.
widgetVar | null | String | Name of the client side widget.

## Getting started with NotificationBar
As notificationBar is a panel component, any content can be placed inside.

```xhtml
<p:notificationBar>
    //Content
</p:notificationBar>
```

## Showing and Hiding
To show and hide the content, notificationBar provides an easy to use client side api that can be
accessed through the widgetVar. _show()_ displays the bar and _hide()_ hides it. _isVisible()_ and _toggle()_
are additional client side api methods.

```xhtml
<p:notificationBar widgetVar="nv">
    //Content
</p:notificationBar>
<h:outputLink value="#" onclick="PF('nv').show()">Show</h:outputLink>
<h:outputLink value="#" onclick="PF('nv').hide()">Hide</h:outputLink>
```
**Note** that notificationBar has a default built-in close icon to hide the content.

## Effects
Default effect to be used when displaying and hiding the bar is "fade", another possible effect is
"slide".

```xhtml
<p:notificationBar effect="slide">
    //Content
</p:notificationBar>
```
If you’d like to turn off animation, set effect name to "none". In addition duration of the animation is
controlled via effectSpeed attribute that can take "normal", "slow" or "fast" as it’s value.

## Position
Default position of bar is "top", other possibility is placing the bar at the bottom of the page. Note
that bar positioning is fixed so even page is scrolled, bar will not scroll.

```xhtml
<p:notificationBar position="bottom">
    //Content
</p:notificationBar>
```
## Skinning
style and styleClass attributes apply to the main container element. Additionally there are two pre-
defined css selectors used to customize the look and feel.

| Class | Applies | 
| --- | --- | 
.ui-notificationbar | Main container element

