# Confirm

Confirm is a behavior element used to integrate with global confirm dialog/popup.

## Getting started with Confirm
See global confirm dialog/popup topic in next section for details.

## The confirm message

The confirm message can be passed towards the confirm dialog/popup in two ways.
Either via the `message` attribute as demonstrated in the following example:
```xhtml
<p:commandButton action="...">
    <p:confirm message="The message content" .../>
</p:commandButton>
```
or via the `confirmMessage` facet on the parent component of the `p:confirm` behavior as shown below:
```xhtml
<p:commandButton action="...">
    <p:confirm .../>
    <f:facet name="confirmMessage">
        The message content
    </f:facet>
</p:commandButton>
```
To preserve the rendered HTML message content one can set the `escape` attribute to `false`. When disabling escaping make sure that the content of the message comes from a trusted source to prevent introduction of an XSS attack vector. If one cannot ensure this, it is advised to use a custom dialog instead.
