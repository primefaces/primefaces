# LinkButton

LinkButton a simple link, which is styled as a button and integrated with Jakarta Faces navigation model.

## Getting Started with LinkButton
Usage is the same as standard h:link, an outcome is necessary to navigate using GET requests.

```xhtml
<p:linkButton outcome="target" value="Navigate"/>
```
To navigate without outcome based approach, use href attribute.

```xhtml
<p:linkButton href="http://www.primefaces.org" value="Navigate"/>
```

## Client Side API
Widget: _PrimeFaces.widget.LinkButton_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| disable() | - | void | Disables button
| enable() | - | void | Enables button
