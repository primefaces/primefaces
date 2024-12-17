# Error Handling

In general, a Servlet-Container supports error pages that are defined in the _web.xml_ or _web-fragment.xml_ files, as shown in the following example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app
        xmlns="https://jakarta.ee/xml/ns/jakartaee"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
        version="6.0">
    <!-- global error-page -->
    <error-page>
        <exception-type>java.lang.Throwable</exception-type>
        <location>/error/error.xhtml</location>
    </error-page>
   <!-- specific error-page for Faces ViewExpired -->
    <error-page>
        <exception-type>jakarta.faces.application.ViewExpiredException</exception-type>
        <location>/error/viewExpired.xhtml</location>
    </error-page>
</web-app>
```

However, since Faces operates within a sub-lifecycle of Servlets, it is not fully integrated into the standard servlet error handling mechanisms.

## Render Response Exceptions
To support exception handling in the `RENDER_RESPONSE` phase, it's required to set the
`jakarta.faces.FACELETS_BUFFER_SIZE` parameter. Otherwise you will probably see a
ServletException with "Response already committed" message.
This is a limitation of every ExceptionHandler.


## Faces ExceptionHandler implementation

Per default, the Faces implementations provides different ExceptionHandler implementations for different cases.
On both cases the exceptions are logged.

1) Non-AJAX  
   The occurred exception in the Faces lifecycle is just re-thrown and therefore the Servlet-Container will forward to the configured error-page from the web/web-fragment.xml.  
   When the ProjectStage is set to `Development`, the Faces internal error-page will be shown.  


2) AJAX
   The occurred exceptions will be written in the AJAX response via:
   ```xml
   <partial-response>
     <error>
       <error-name>...</error-name>
       <error-message>...</error-message>
     </error>
   </partial-response>
   ```
   With that information the `f:ajax onerror` handler and the `faces.ajax.addOnError` handlers are called.  
   Depending on the Faces implementation, ProjectStage and if no global listener was added via `faces.ajax.addOnError`, the error is displayed as console.log or JS alert box.


### Integration with PrimeFaces
PrimeFaces handles client-side error management of both `f:ajax` and `p:ajax`.
The `error` from the response is passed to the `p:ajaxExceptionHandler`. If no specific `p:ajaxExceptionHandler` or global `p:ajaxExceptionHandler` is defined, PrimeFaces attempts to redirect to the configured `error-page` specified in the `web/web-fragment.xml`.
If neither a `p:ajaxExceptionHandler` nor an `error-page` is found, the error is simply logged to the console.


## PrimeFaces ExceptionHandler implementation

PrimeFaces provides another ExceptionHandler implementation with the following advantages and disadvantages:

Advantages:
- It adds to ability to use exception informations, even in a AJAX request or after the redirect via EL (`pfExceptionHandler`)
- Adds the ability to skip logging of specific exceptions via the `primefaces.EXCEPTION_TYPES_TO_IGNORE_IN_LOGGING` config

Disadvantages:
- It redirects (instead of forward) to the error-page in NON-AJAX mode, therefore the error informations are not accessible from the Servlet-Container (e.g. `#{requestScope['jakarta.servlet.error.exception_type']}`)

### Configuration

`ExceptionHandler` and an `ELResolver` configured is required via `faces-config.xml`:

```xml
<application>
    <el-resolver>org.primefaces.application.exceptionhandler.PrimeExceptionHandlerELResolver</el-resolver>
</application>
<factory>
    <exception-handler-factory>org.primefaces.application.exceptionhandler.PrimeExceptionHandlerFactory</exception-handler-factory>
</factory>
```

### Exception Information

In the error page, information about the exception is provided via the `pfExceptionHandler` EL keyword.
Here is the list of exposed properties:

- **exception**: Throwable instance.
- **type**: Type of the exception.
- **message**: Exception message:
- **stackTrace**: An array of java.lang.StackTraceElement instances.
- **formattedStackTrace**: Stack trace as presentable string.
- **timestamp**: Timestamp as date.
- **formattedTimestamp**: Timestamp as presentable string.

In error page, exception metadata is accessed using EL;

```xhtml
<h:outputText value="Message:#{pfExceptionHandler.message}" />
<h:outputText value="#{pfExceptionHandler.formattedStackTrace}" escape="false" />
```

### Exception Logging
Per default all exceptions are logged in all `ProjectStages` -  we just skip logging `ViewExpiredExceptions `in `Production`.
You can also configure ignores via:

```xml
<context-param>
    <param-name>primefaces.EXCEPTION_TYPES_TO_IGNORE_IN_LOGGING</param-name>
    <param-value>org.mycompany.MyException, org.anothercompany.AnotherException</param-value>
</context-param>
```
