# Callback

Callback is a client behavior that executes JavaScript on an event of a component instead of an AJAX request.

## Getting Started with Callback
Like `p:ajax`, `p:callback` is attached to an event of the component, without `event` to the default event. The
JavaScript in `execute` is the body of a function with these arguments:

| Argument | Description |
| --- | --- |
| `source` | Client id of the component. |
| `event` | Name of the event, e.g. `page`. |
| `params` | Parameters of the event that the widget passes (`[{name, value}]`), `null` if there are none. |
| `ext` | Options the widget passes for the behavior, `null` if there are none (e.g. for DOM events like `click`). |

```xhtml
<p:dataTable value="#{bean.cars}" var="car" paginator="true" rows="10">
    <p:callback event="page" execute="console.log('page of ' + source);"/>
    ...
</p:dataTable>

<p:slider for="input">
    <p:callback event="slideEnd" execute="console.log('slide ended at ' + params[0].value);"/>
</p:slider>
```

## Together with p:ajax
The behaviors of an event are executed in their order. If the callback returns `false`, the following behaviors (e.g.
a `p:ajax`) and, for a DOM event like the `click` of a button, the default action are not executed.

```xhtml
<p:toggleSwitch value="#{bean.active}">
    <p:callback event="change" execute="return confirm('Send the AJAX request?');"/>
    <p:ajax listener="#{bean.onChange}" update="msgs"/>
</p:toggleSwitch>
```

`disabled="true"` disables the callback, e.g. depending on a condition of the bean.

## Migration from PrimeFaces Extensions
`pe:javascript` becomes `p:callback` with the same attributes (`event`, `execute`, `disabled`) and the same arguments
of the JavaScript. Returning `false` now stops the following behaviors of the event (PFE ignored the return value).
`f:param` children were never passed to the JavaScript by PFE and are not supported.
