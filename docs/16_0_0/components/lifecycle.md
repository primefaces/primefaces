# Lifecycle

Lifecycle is a utility component which displays the execution time of each Jakarta Faces phase. It also
synchronizes automatically after each AJAX request.

## Getting started with Lifecycle
A phase listener needs to be configured to the use component.

```xml
<lifecycle>
    <phase-listener>
        org.primefaces.component.lifecycle.LifecyclePhaseListener
    </phase-listener>
</lifecycle>
```
Then usage is simple as adding the component to the page.

```xhtml
<p:lifecycle />
```
