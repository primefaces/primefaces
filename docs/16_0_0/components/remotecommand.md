# RemoteCommand

RemoteCommand provides a way to execute backing bean methods directly from javascript.

## Getting started with RemoteCommand
RemoteCommand is used by invoking the command from your javascript code.

```xhtml
<p:remoteCommand name="increment" action="#{counter.increment}" update="count" />
<h:outputText id="count" value="#{counter.count}" />
```
```js
<script type="text/javascript">
    function customfunction() {
        //your custom code
        increment(); //makes a remote call
    }
</script>
```

That’s it whenever you execute your custom javascript function(eg customfunction()), a remote call
will be made, actionListener is processed and output text is updated. Note that remoteCommand
must be nested inside a form.


## Passing parameters

Remote command can send dynamic parameters in the following way;

```js
increment([{name:'x', value:10}, {name:'y', value:20}]);
```

To access these parameters in a bean method:

```java
    public void execute() {
        String param1 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("x");
        String param2 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("y");
        // Do something with the parameters
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Executed", "x: " + param1 + ", y: " + param2));
    }
```

## Receiving parameters

You can receive parameters from the server in the following way.

First, in your bean method, add a callback parameter:

```java
    public void execute() {
        // Do something interesting when the remote command is called
        // ...

        // Return data to the client
        PrimeFaces.current().ajax().addCallbackParam("serverTime", System.currentTimeMillis());
    }
```

Then access the parameter on the client:

```xhtml
<p:remoteCommand name="rc" update="msgs" action="#{remoteCommandView.execute}"
    oncomplete="alert('Return value from server: ' + args.serverTime)"/>
```

## Using promises

You can add also add callbacks for when the remote succeeds or fails via the
promise returned by the remote command. Compared with adding a callback via
`oncomplete`, this lets you register as many callbacks as you want, and also
register different callbacks dynamically from the calling JavaScript code:

Using `async` JavaScript functions (check browser support!, not supported in
IE), this would look as follows:

```javascript
async function main(param1, param2) {
    const responseData = await rc([
        {name: 'x', value: param1},
        {name: 'y', value: param2},
    ]);
    
    const serverTime = responseData.jqXHR.pfArgs.serverTime;
}

main("foo", "bar");
```

Without support for async function, use `rc().then(...).catch(...)` instead.

Note to TypeScript users: You can use the
[PrimeFaces.ajax.RemoteCommand](../jsdocs/modules/src_PrimeFaces.PrimeFaces.ajax.html#remotecommand)
type to declare the available remote commands once in a declaration file in the
window scope so they can be called by your code.