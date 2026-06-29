# EL Functions

PrimeFaces provides built-in EL extensions that are helpers to common use cases.

## Common Functions

| Function | Description |
| --- | --- |
resolveClientId(String expression, UIComponent source) | Returns the clientId of the resolved component for the given expression.
resolveClientIds(String expression, UIComponent source) | Returns the clientIds of the resolved components for the given expression.
resolveWidgetVar(String expression, UIComponent source) | Returns the widgetVar of the resolved component for the given expression.
closestWidgetVar(UIComponent) | Returns the widgetVar of the closest parent widget of the given component.
resolveComponent(String expression, UIComponent source) | Returns the resolved UIComponent for the given expression.
resolveFirstComponentWithId(String id, UIComponent source) | Returns the first UIComponent with the same id (not clientId!) as the requested id. This method ignores any algorithm or NamingContainers.
escapeJavaScriptVarName(String str) | Escapes a string for a JS var name (e.g. ':' in a clientId). This is useful if you need to use a clientId in the `name` of a `p:remoteCommand`.
language() | Gets the current ISO 639-1 Language Code from current Locale so 'pt_BR' becomes 'pt'.

### resolveClientId

```xhtml
//Example to search from root:
var clientId = #{p:resolveClientId('form:tabView:myDataTable', view)};
//Example to search from current component:
<p:commandButton onclick="#{p:resolveClientId('@parent', component)}">
```

### resolveWidgetVar

NOTE: this example passes "cc" as component to start the search. To start from root, can you simply pass "view".

```xhtml
<cc:implementation>
    <p:dialog id="dlg" widgetVar="dlg">
        //contents
    </p:dialog>
    <p:commandButton type="button" value="Show" onclick="PF('#{p:resolveWidgetVar(‘dlg’, cc)}').show()" />
</cc:implementation>
```

### language

```xhtml
<html lang="#{p:language()}" xml:lang="#{p:language()}">
```

## Page Authorization

Authorization function use HttpServletRequest API for the backend information.

| Function | Description |
| --- | --- |
ifGranted(String role) | Returns true if user has the given role, else false.
ifAllGranted(String roles) | Returns true if user has all of the given roles, else false.
ifAnyGranted(String roles) | Returns true if user has any of the given roles, else false.
ifNoneGranted(String roles) | Returns true if user has none of the given roles, else false. e.g `p:ifNoneGranted('ROLE_ADMIN,ROLE_OPERATOR')`
remoteUser() | Returns the name of the logged in user.
userPrincipal() | Returns the principal instance of the logged in user.

```xhtml
<p:commandButton rendered="#{p:ifGranted('ROLE_ADMIN')}" />
<h:inputText disabled="#{p:ifGranted('ROLE_GUEST')}" />
<p:inputMask rendered="#{p:ifAllGranted('ROLE_EDITOR, ROLE_READER')}" />
```
