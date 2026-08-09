# Error Handling

PrimeFaces provides error handling on two levels: **client-side** (handling AJAX errors in the browser) and **server-side** (catching exceptions during the Faces lifecycle and routing to error pages). Server-side handling covers both AJAX and non-AJAX requests.

Both levels make use of Servlet error pages, which are defined in `web.xml` or `web-fragment.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app
        xmlns="https://jakarta.ee/xml/ns/jakartaee"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
        version="6.0">
    <error-page>
        <exception-type>java.lang.Throwable</exception-type>
        <location>/error/error.xhtml</location>
    </error-page>
    <error-page>
        <exception-type>jakarta.faces.application.ViewExpiredException</exception-type>
        <location>/error/viewExpired.xhtml</location>
    </error-page>
</web-app>
```

## Client-Side Error Handling

Client-side error handling exclusively deals with AJAX requests. It handles two categories of errors:

1. **Server-reported errors** — When an exception occurs during an AJAX request, the Faces implementation writes the error into the AJAX response.
2. **Connection errors** — When the AJAX response cannot be received at all (e.g. network failures, timeouts, or server unavailability).

For server-reported errors, the response contains:

```xml
<partial-response>
  <error>
    <error-name>...</error-name>
    <error-message>...</error-message>
  </error>
</partial-response>
```

In both cases, PrimeFaces processes the error through the following chain:

1. **Callbacks & Events** — `onerror` callbacks on `p:ajax` are triggered, as well as global listeners like `p:ajaxStatus` and the jQuery `pfAjaxError` event.
2. **`p:ajaxExceptionHandler`** — If a specific or global `p:ajaxExceptionHandler` is defined, it is invoked with the error details.
3. **Error Page Redirect** — If no `p:ajaxExceptionHandler` is found, PrimeFaces attempts to redirect to the matching `error-page` configured in `web.xml` / `web-fragment.xml`.
4. **Console Log** — If neither a handler nor an error page is configured, the error is logged to the browser console.

### Jakarta Faces Integration

PrimeFaces hooks into the standard `f:ajax` error handling as well, so errors from both `f:ajax` and `p:ajax` are processed through the same chain described above. This means the standard `faces.ajax.addOnError` listeners are also called.

### Custom Error Handling

Custom error handling can be implemented by defining a `p:ajaxExceptionHandler`. This can also be used e.g. to refresh the current view instead of a redirect.

`<p:ajaxExceptionHandler type="jakarta.faces.application.ViewExpiredException" onexception="location.reload();" />`

## Server-Side Error Handling

### Default Faces ExceptionHandler

The default Faces `ExceptionHandler` behaves differently depending on the request type. In both cases exceptions are logged.

**Non-AJAX:** The exception is re-thrown so the Servlet-Container forwards to the configured error page. In `Development` ProjectStage, the Faces internal error page is shown instead.

**AJAX:** The exception is written into the AJAX response (see Client-Side above), where client-side handlers take over. The default Faces ExceptionHandler does **not** redirect to an error page for AJAX requests.

### PrimeFaces ExceptionHandler

PrimeFaces provides its own `ExceptionHandler` implementation that extends the default behavior.

**Non-AJAX:** The ExceptionHandler **redirects** to the configured error page. Note: because this is a redirect (not a forward), standard Servlet error attributes like `#{requestScope['jakarta.servlet.error.exception_type']}` are not available. Instead, exception information is accessible via the `pfExceptionHandler` EL keyword.

**AJAX:** Unlike the default Faces ExceptionHandler, PrimeFaces also redirects to the configured error page for AJAX requests. The exception information is equally accessible via `pfExceptionHandler`.

Additional capabilities:
- Exception information is accessible via EL (`pfExceptionHandler`) in all cases — AJAX, non-AJAX, and after redirect.
- Ability to skip logging of specific exceptions via `primefaces.EXCEPTION_TYPES_TO_IGNORE_IN_LOGGING` (Mojarra only — MyFaces has this built in).

#### Configuration

Register the `ExceptionHandler` and `ELResolver` in `faces-config.xml`:

```xml
<application>
    <el-resolver>org.primefaces.application.exceptionhandler.PrimeExceptionHandlerELResolver</el-resolver>
</application>
<factory>
    <exception-handler-factory>org.primefaces.application.exceptionhandler.PrimeExceptionHandlerFactory</exception-handler-factory>
</factory>
```

#### Exception Information via EL

The `pfExceptionHandler` EL keyword exposes the following properties on the error page:

| Property | Description |
|---|---|
| `exception` | The `Throwable` instance |
| `type` | Exception type |
| `message` | Exception message |
| `stackTrace` | Array of `StackTraceElement` instances |
| `formattedStackTrace` | Stack trace as presentable string |
| `timestamp` | Timestamp as `Date` |
| `formattedTimestamp` | Timestamp as presentable string |

Example usage:

```xhtml
<h:outputText value="Message: #{pfExceptionHandler.message}" />
<h:outputText value="#{pfExceptionHandler.formattedStackTrace}" escape="false" />
```

#### Exception Logging

By default all exceptions are logged in all `ProjectStages`, except `ViewExpiredException` in `Production`. Additional exceptions can be excluded:

```xml
<context-param>
    <param-name>primefaces.EXCEPTION_TYPES_TO_IGNORE_IN_LOGGING</param-name>
    <param-value>org.mycompany.MyException, org.anothercompany.AnotherException</param-value>
</context-param>
```

### Render Response Exceptions

To support exception handling in the `RENDER_RESPONSE` phase, the `jakarta.faces.FACELETS_BUFFER_SIZE` parameter must be set. Otherwise a `ServletException` with "Response already committed" will occur. This is a limitation of every `ExceptionHandler`.
