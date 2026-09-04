# Link

Link is an extension to standard h:link component.

## Getting Started with Link
p:link usage is same as standard h:link, an outcome is necessary to navigate using GET requests.
Assume you are at source.xhtml and need to navigate target.xhtml.

```xhtml
<p:link outcome="target" value="Navigate"/>
```
To navigate without outcome based approach, use href attribute.

```xhtml
<p:link href="http://www.primefaces.org" value="Navigate"/>
```
