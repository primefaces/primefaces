
# Client Side Validation

PrimeFaces Client Side Validation (CSV) Framework is the most complete and advanced CSV
solution for JavaServer Faces and Java EE. CSV support for JSF is not an easy task, it is not simple
as integrating a 3rd party javascript plugin as JSF has its own lifecycle, concepts like conversion
and then validation, partial processing, facesmessages and many more. Real CSV for JSF should be
compatible with server side implementation, should do what JSF does, so that users do not
experience difference behaviors on client side and server side.

- Compatible with Server Side Implementation.
- Conversion and Validation happens at client side.
- Partial Process & Update support for AJAX.
- I18n support along with component specific messages.
- Client side Renderers for message components.
- Easy to write custom client converters and validators.
- Global or Component based enable/disable.
- Advanced Bean Validation Integration.
- Little footprint using HTML5.

## Configuration

CSV is disabled by default and a global parameter is required to turn it on.

```xml
<context-param>
    <param-name>primefaces.CLIENT_SIDE_VALIDATION</param-name>
    <param-value>true</param-value>
</context-param>
```
At page level, enable _validateClient_ attribute of commandButton and commandLink components.

```xhtml
<h:form>
    <p:messages />
    <p:inputText required="true" />
    <p:inputTextarea required="true" />
    <p:commandButton value="Save" validateClient="true" ajax="false"/>
</h:form>
```
That is all for the basics, clicking the button validates the form at client side and displays the errors
using messages component.

CSV works for PrimeFaces components only, standard h: * components are not supported.

## AJAX vs Non-AJAX

CSV works differently depending on the request type of the trigger component, to be compatible with cases where CSV is not enabled.

#### Non-AJAX
All visible and editable input components in the form are validated and message components must be placed inside the form.

#### AJAX
CSV supports partial processing and updates on client side.  
If process attribute is set, the components that would be processed at server side gets validated at client side.  
Similary if update attribute is defined, only message components inside the updated parts gets rendered.  
The whole process happens at client side.

## Events

CSV provides a behavior, _p:clientValidator_, to do instant validation, in case you do not want to
wait for the users to fill in the form and hit commandButton/commandLink.  
Using _p:clientValidator_ and custom events, CSV for a particular component can run with events such as change (default), blur or keyup.

```xhtml
<h:form>
    <p:panel header="Validate">
        <h:panelGrid columns="4" cellpadding="5">
            <h:outputLabel for="text" value="Text: (Change)" />
            <p:inputText id="text" value="#{validationBean.text}" required="true">
                <f:validateLength minimum="2" maximum="5" />
                <p:clientValidator />
            </p:inputText>
            <p:message for="text" display="icon" />
            <h:outputText value="#{validationBean.text}" />
            <h:outputLabel for="integer" value="Integer: (Keyup)" />
            <p:inputText id="integer" value="#{validationBean.integer}">
                <p:clientValidator event="keyup"/>
            </p:inputText>
            <p:message for="integer" display="icon" />
            <h:outputText value="#{validationBean.integer}" />
        </h:panelGrid>
        <p:commandButton value="Save" ajax="false" icon="ui-icon-check" validateClient="true"/>
    </p:panel>
</h:form>
```
