# AjaxExceptionHandler

AjaxExceptionHandler is a utility component for the built-in ExceptionHandler.

## Getting Started with AjaxExceptionHandler
A component that offers a declarative approach to handling AJAX exceptions.  
It allows you to execute client-side callbacks or update other components on the same page.  
This is particularly useful when you prefer not to create a separate error page and wish to handle minor exceptions directly within the same view.

It serves as the equivalent of the Faces `faces.ajax.addOnError`, which is only supported by `f:ajax`.

Following example shows the exception in a dialog on the same page:

```xhtml
<p:ajaxExceptionHandler type="jakarta.faces.application.ViewExpiredException" update="exceptionDialog" onexception="PF('exceptionDialog').show();" />
<p:dialog id="exceptionDialog" header="Exception: #{pfExceptionHandler.type} occurred!" widgetVar="exceptionDialog" height="500px">
    Message: #{pfExceptionHandler.message} <br/>
    StackTrace: <h:outputText value="#{pfExceptionHandler.formattedStackTrace}" escape="false" />
    <p:button onclick="document.location.href = document.location.href;" value="Reload!"/>
</p:dialog>
```

The ideal location for `p:ajaxExceptionHandler` component is the facelets template so that it gets
included in every page.

For detailed information, please refer to the [Error Handling](core/errorhandling.md) section.
