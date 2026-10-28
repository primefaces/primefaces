# Growl

Growl is based on the Mac’s growl notification widget and used to display FacesMessages in an
overlay.

## Getting Started with Growl
Growl usage is similar to standard h:messages component. Simply place growl anywhere on your
page, since messages are displayed as an overlay, the location of growl in Jakarta Faces page does not matter.

```xhtml
<p:growl />
```
## Lifetime of messages
By default each message will be displayed for 6000 ms and then hidden. A message can be made
sticky meaning it’ll never be hidden automatically.

```xhtml
<p:growl sticky="true" />
```
If growl is not working in sticky mode, it’s also possible to tune the duration of displaying
messages. Following growl will display the messages for 5 seconds and then fade-out.

```xhtml
<p:growl life="5000" />
```

## Positioning
Growl is positioned at top right corner by default, position can be controlled with a CSS selector
called _p-growl_. With the below setting growl will be located at top left corner.


```css
.p-growl {
    left:20px;
}
```
## Targetable Messages
There may be times where you need to target one or more messages to a specific message
component, for example suppose you have growl and messages on same page and you need to
display some messages on growl and some on messages. Use for attribute to associate messages
with specific components.

```xhtml
<p:messages for="somekey" />
<p:growl for="anotherkey" />
```
```java
FacesContext context = FacesContext.getCurrentInstance();
context.addMessage("somekey", facesMessage1);
context.addMessage("somekey", facesMessage2);
context.addMessage("anotherkey", facesMessage3);
```
In sample above, messages will display first and second message and growl will only display the
3rd message.

## Severity Levels
Using severity attribute, you can define which severities can be displayed by the component. For
instance, you can configure growl to only display infos and warnings.

```xhtml
<p:growl severity="info,warn" />
```
## Escaping
Growl escapes HTML content in messages, in case you need to display HTML via growl set escape
option to false.

```xhtml
<p:growl escape="false" />
```
## Skinning
Following is the list of structural style classes;

| Class | Applies |
| --- | --- |
.p-growl | Main container element of growl
.p-growl-item-container | Container of messages
.p-growl-item | Container of a message
.p-growl-message | Text message container
.p-growl-title | Summary of the message
.p-growl-message p | Detail of the message
.p-growl-image | Severity icon
.p-growl-image-info | Info severity icon
.p-growl-image-warn | Warning severity icon
.p-growl-image-error | Error severity icon
.p-growl-image-fatal | Fatal severity icon
.p-growl-{severity}-icon | Severity icon (token `--p-toast-{severity}-icon`)
.p-growl-close-icon | Close icon (token `--p-toast-close-icon`)

As skinning style classes are global, see the main theming section for more information.
