# Sticky

Sticky component positions other components as fixed so that these components stay in window
viewport during scrolling.

## Getting started with Sticky
Sticky requires a target to keep in viewport on scroll. Here is a sticky toolbar;

```xhtml
<p:toolbar id="tb">
    <p:toolbarGroup align="left">
        <p:commandButton type="button" value="New" icon="ui-icon-document" />
        <p:commandButton type="button" value="Open" icon="ui-icon-folder-open"/>
        <p:divider />
        <p:commandButton type="button" title="Save" icon="ui-icon-disk"/>
        <p:commandButton type="button" title="Delete" icon="ui-icon-trash"/>
        <p:commandButton type="button" title="Print" icon="ui-icon-print"/>
    </p:toolbarGroup>
</p:toolbar>
<p:sticky target="tb" />
```
## Skinning
There are no visual styles of sticky however, _ui-sticky_ class is applied to the target when the position
is fixed. When target is restored to its original location this is removed.

