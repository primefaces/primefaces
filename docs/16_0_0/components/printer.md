# Printer

Printer allows sending a specific Jakarta Faces component to the printer, not the whole page.

## Getting started with the Printer
Printer is attached to any command component like a button or a link. Examples below
demonstrates how to print a simple output text or a particular image on page;

```xhtml
<h:commandButton id="btn" value="Print">
    <p:printer target="output" />
</h:commandButton>
<h:outputText id="output" value="PrimeFaces Rocks!" />
<h:outputLink id="lnk" value="#">
    <p:printer target="image" />
    <h:outputText value="Print Image" />
</h:outputLink>
<p:graphicImage id="image" value="/images/nature1.jpg" />
```
