# Password

Password component is an extended version of standard inputSecret component with theme
integration and strength indicator.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Password.html)

## Getting Started with Password
Password is an input component and used just like a standard input text. When _feedback_ option is
enabled a password strength indicator is displayed.

```xhtml
<p:password value="#{bean.password}" feedback="true|false" />
```

```java
public class Bean {
    private String password;

    public String getPassword() { 
        return password; 
    }
    public void setPassword(String password) { 
        this.password = password;
    }
}
```
## I18N
Although all labels are in English by default, you can provide custom labels as well. Following
password gives feedback in Turkish.

```xhtml
<p:password value="#{bean.password}" promptLabel="Lütfen şifre giriniz" weakLabel="Zayıf" goodLabel="Orta seviye" strongLabel="Güçlü" feedback= "true"/>
```
## Inline Strength Indicator
By default strength indicator is shown in an overlay, if you prefer an inline indicator just enable
inline mode.

```xhtml
<p:password value="#{mybean.password}" inline="true" feedback= "true"/>
```
## Confirmation
Password confirmation is a common case and password provides an easy way to implement. The
other password component’s id should be used to define the _match_ option.

```xhtml
<p:password id="pwd1" value="#{passwordBean.password6}" feedback="false" match="pwd2" label="Password 1" required="true"/>
<p:password id="pwd2" value="#{passwordBean.password6}" feedback="false" label="Password 2" required="true"/>
```

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `valueChange`  
**Available Events:** `blur, change, click, contextmenu, copy, cut, dblclick, drag, dragend, dragenter, dragleave, dragover, dragstart, drop, focus, input, invalid, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, paste, reset, scroll, search, select, valueChange, wheel`  

```xhtml
<p:ajax event="valueChange" listener="#{bean.handlevalueChange}" update="msgs" />
```

## Client Side API
Widget: _PrimeFaces.widget.Password_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Shows password feedback panel.
hide() | - | void | Hides password feedback panel.
toggleMask() | - | void | Toggle masking and unmasking the password.
disable() | - | void | Disables the input field
enable() | - | void | Enables the input field

## Skinning
Structural selectors for password are;

| Class | Applies | 
| --- | --- | 
.ui-password | Input element.
.ui-password-panel | Overlay panel of strength indicator.
.ui-password-meter | Strength meter.
.ui-password-info | Strength label.

As skinning style classes are global, see the main theming section for more information.

