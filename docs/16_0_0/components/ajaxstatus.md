# AjaxStatus

AjaxStatus is a global notifier for ajax requests.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.AjaxStatus-1.html)

## Getting Started with AjaxStatus
AjaxStatus uses facets to represent the request status. Most common used facets are _start_ and
_complete_. The _start_ facet will be visible once ajax request begins and stay visible until it’s completed.
Once the ajax response is received and page is updated, _start_ facet gets hidden and _complete_ facet
shows up. **NOTE:** If a _complete_ facet is defined, the user will never see the _error_ or _success_ facets.

```xhtml
<p:ajaxStatus>
    <f:facet name="start">
        <p:graphicImage value="ajaxloading.gif" />
    </f:facet>
    <f:facet name="complete">
        <h:outputText value="Done!" />
    </f:facet>
</p:ajaxStatus>
```
## Events
Here is the full list of available event names;

- **default** : Initially visible when page is loaded.
- **start** : Before ajax request begins.
- **success** : When ajax response is received without error.
- **error** : When ajax response is received with an http error.
- **complete** : When everything finishes.

```xhtml
<p:ajaxStatus>
    <f:facet name="error">
        <h:outputText value="Error" />
    </f:facet>
    <f:facet name="success">
        <h:outputText value="Success" />
    </f:facet>
    <f:facet name="default">
        <h:outputText value="Idle" />
    </f:facet>
    <f:facet name="start">
        <h:outputText value="Sending" />
    </f:facet>
    <f:facet name="complete">
        <h:outputText value="Done" />
    </f:facet>
</p:ajaxStatus>
```
## Custom Events
Facets are the declarative way to use, if you’d like to implement advanced cases with scripting you
can take advantage of on* callbacks which are the event handler counterparts of the facets.

```xhtml
<p:ajaxStatus onstart="alert('Start')" oncomplete="alert('End')"/>
```

A comman usage of programmatic approach is to implement a custom status dialog;

```xhtml
<p:ajaxStatus onstart="PF('status').show()" oncomplete="PF('status').hide()"/>
<p:dialog widgetVar="status" modal="true" closable="false">
    Please Wait
</p:dialog>
```
## Client Side API
Widget: _PrimeFaces.widget.AjaxStatus_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- |
| trigger(event) | event: Name of event. | void | Triggers given event. |

## Skinning
AjaxStatus is equipped with _style_ and _styleClass_. Styling directly applies to a container element
which contains the facets.

```xhtml
<p:ajaxStatus style="width:32px;height:32px" ... />
```
## Tips

- Avoid updating ajaxStatus itself to prevent duplicate facet/callback bindings.
- Provide a fixed width/height to an inline ajaxStatus to prevent page layout from changing.
- Components like commandButton has an attribute ( _global)_ to control triggering of AjaxStatus.
- AjaxStatus also supports core Jakarta Faces ajax requests of f:ajax as well.
- If a _complete_ facet is defined, the user will never see the _error_ or _success_ facets.