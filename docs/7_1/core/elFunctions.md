# EL Functions

PrimeFaces provides built-in EL extensions that are helpers to common use cases.

## Common Functions

| Function | Description |
| --- | --- |
resolveClientId(expression, context) | Returns clientId of the component from a given expression.
resolveWidgetVar(expression, context) | Returns widget variable name of the component from a given expression.

### resolveClientId

```xhtml
//Example to search from root:
var clientId = #{p:resolveClientId('form:tabView:myDataTable', view)};
//Example to search from current component:
<p:commandButton onclick="#{p:resolveClientId('@parent', component)}">
```

### resolveWidgetVar

```xhtml
<cc:implementation>
    <p:dialog id="dlg">
        //contents
    </p:dialog>
    <p:commandButton type="button" value="Show" onclick="#{p:resolveWidgetVar(‘dlg’), cc}.show()" />
</cc:implementation>
```

## Page Authorization

Authorization function use HttpServletRequest API for the backend information.

| Function | Description |
| --- | --- |
ifGranted(String role) | Returns true if user has the given role, else false.
ifAllGranted(String roles) | Returns true if user has all of the given roles, else false.
ifAnyGranted(String roles) | Returns true if user has any of the given roles, else false.
ifNotGranted(String roles) | Returns true if user has none of the given roles, else false.
remoteUser() | Returns the name of the logged in user.
userPrincipal() | Returns the principal instance of the logged in user.

```xhtml
<p:commandButton rendered="#{p:ifGranted('ROLE_ADMIN')}" />
<h:inputText disabled="#{p:ifGranted('ROLE_GUEST')}" />
<p:inputMask rendered="#{p:ifAllGranted('ROLE_EDITOR, ROLE_READER')}" />
```
