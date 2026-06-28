# Keyboard

Keyboard is an input component that uses a virtual keyboard to provide the input. Notable features
are the customizable layouts and skinning capabilities.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Keyboard.html)

## Getting Started with Keyboard
Keyboard is used just like a simple inputText, by default when the input gets the focus a keyboard is
displayed.

```xhtml
<p:keyboard value="#{bean.value}" />
```

## Built-in Layouts
There’re a couple of built-in keyboard layouts these are ‘qwerty’, ‘qwertyBasic’ and ‘alphabetic’. For
example keyboard below has the alphabetic layout.

```xhtml
<p:keyboard value="#{bean.value}" layout="alphabetic"/>
```
## Custom Layouts
Keyboard has a very flexible layout mechanism allowing you to come up with your own layout.

```xhtml
<p:keyboard value="#{bean.value}" layout="custom" layoutTemplate="prime-back,faces-clear,rocks-close"/>
```
Another example;

```xhtml
<p:keyboard value="#{bean.value}" layout="custom" layoutTemplate="create-space-your-close,owntemplate-shift,easily-space-spacebar"/>
```

A layout template basically consists of built-in keys and your own keys. Following is the list of all
built-in keys.

- back
- clear
- close
- shift
- spacebar
- space
- halfspace

All other text in a layout is realized as separate keys so "prime" would create 5 keys as "p" "r" "i"
"m" "e". Use dash to separate each member in layout and use commas to create a new row.

## Keypad
By default keyboard displays whole keys, if you only need the numbers use the keypad mode.

```xhtml
<p:keyboard value="#{bean.value}" keypadOnly="true"/>
```
## ShowMode
There’re a couple of different ways to display the keyboard, by default keyboard is shown once
input field receives the focus. This is customized using the showMode feature which accept values
‘focus’, ‘button’, ‘both’. Keyboard below displays a button next to the input field, when the button is
clicked the keyboard is shown.

```xhtml
<p:keyboard value="#{bean.value}" showMode="button"/>
```
Button can also be customized using the _buttonImage_ and _buttonImageOnly_ attributes.

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `valueChange`  
**Available Events:** `blur, change, click, contextmenu, copy, cut, dblclick, drag, dragend, dragenter, dragleave, dragover, dragstart, drop, focus, input, invalid, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, paste, reset, scroll, search, select, valueChange, wheel`  

```xhtml
<p:ajax event="valueChange" listener="#{bean.handlevalueChange}" update="msgs" />
```