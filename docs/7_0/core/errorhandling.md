# Error Handling

PrimeFaces provides a built-in exception handler to take care of exceptions in ajax and non-ajax
requests easily.

## Configuration

ExceptionHandler and an ElResolver configured is required in faces configuration file.

```xml
<application>
    <el-resolver>
        org.primefaces.application.exceptionhandler.PrimeExceptionHandlerELResolver
    </el-resolver>
</application>
<factory>
    <exception-handler-factory>
    org.primefaces.application.exceptionhandler.PrimeExceptionHandlerFactory
    </exception-handler-factory>
</factory>
```

## Error Pages

ExceptionHandler is integrated with error-page mechanism of Servlet API. At application startup,
PrimeFaces parses the error pages and uses this information to find the appropriate page to redirect
to based on the exception type. Here is an example web.xml configuration with a generic page for
exceptions and a special page for ViewExpiredException type.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.5"
    xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
    http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" >
    <!-- Other application configuration -->
    <error-page>
        <exception-type>java.lang.Throwable</exception-type>
        <location>/ui/error/error.jsf</location>
    </error-page>
    <error-page>
        <exception-type>javax.faces.application.ViewExpiredException</exception-type>
        <location>/ui/error/viewExpired.jsf</location>
    </error-page>
</web-app>
```

## Exception Information

In the error page, information about the exception is provided via the pfExceptionHandler EL
keyword. Here is the list of exposed properties.

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

## AjaxExceptionHandler Component

A specialized exception handler component provides a way to execute callbacks on client side,
update other components on the same page. This is quite useful in case you don't want to create a
separate error page. Following example shows the exception in a dialog on the same page.

```xhtml
<p:ajaxExceptionHandler type="javax.faces.application.ViewExpiredException" update="exceptionDialog" onexception="PF('exceptionDialog').show();" />
<p:dialog id="exceptionDialog" header="Exception: #{pfExceptionHandler.type} occured!" widgetVar="exceptionDialog" height="500px">
    Message: #{pfExceptionHandler.message} <br/>
    StackTrace: <h:outputText value="#{pfExceptionHandler.formattedStackTrace}" escape="false" />
    <p:button onclick="document.location.href = document.location.href;" value="Reload!"/>
</p:dialog>
```
Ideal location for p:ajaxExceptionHandler component is the facelets template so that it gets
included in every page. Refer to component documentation of p:ajaxExceptionHandler for the
available attributes.

## Render Response Exceptions
To support exception handling in the _RENDER_RESPONSE_ phase, it's required to set the
_javax.faces.FACELETS_BUFFER_SIZE_ parameter. Otherwise you will probably see a
ServletException with "Response already committed" message.