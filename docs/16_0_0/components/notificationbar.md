# NotificationBar

NotificationBar displays a multipurpose fixed positioned panel for notification.

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

