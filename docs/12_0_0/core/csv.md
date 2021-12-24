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

## Extending CSV

Using CSV APIs, it is easy to write your own custom converters and validators.

Your custom validator must implement ClientValidator interface to provide the id of the client validator and the optional metadata.


```java
package org.primefaces.examples.validate;
import java.util.Map;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.primefaces.validate.ClientValidator;

@FacesValidator("custom.emailValidator")
public class EmailValidator implements Validator, ClientValidator {
    private Pattern pattern;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    public EmailValidator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if(value == null) {
            return;
        }
        if(!pattern.matcher(value.toString()).matches()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", value + " is not a valid email;"));
        }
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "custom.emailValidator";
    }
}
```

Validator is plugged-in using the standard way.

```xhtml
<h:form>
    <p:messages />
    <p:inputText id="email" value="#{bean.value}">
        <f:validator validatorId="custom.emailValidator" />
    </p:inputText>
    <p:message for="email" />
    <p:commandButton value="Save" validateClient="true" ajax="false"/>
</h:form>
```

Last step is implementing the validator at client side and configuring it.

```js
PrimeFaces.validator['custom.emailValidator'] = {
    pattern: /\S+@\S+/,
    validate: function(element, value) {
        //use element.data() to access validation metadata, in this case there is none.
        if(!this.pattern.test(value)) {
            throw {
                summary: 'Validation Error',
                detail: value + ' is not a valid email.'
            }
        }
    }
};
```

In some cases your validator might need metadata, for example _LengthValidator_ requires _min_ and  _max_ constraints to validate against.  
Server side validator can pass these by overriding the _getMetadata()_ method by providing a _Map_ of name/value pairs.  
At client side, these are accessed via _element.data(key)_.

```java
@Override
public Map<String, Object> getMetadata() {
    Map<String,Object> data = new HashMap<String,Object>();
    data.put("data-prime", 10);
    return data;
}
```

```js
validate: function(element, value) {
    var prime = element.data("prime"); //10
    //validate
}
```

## Converter

Like on server side, PrimeFaces CSV requires to convert e.g. the string of input field to a number or Object.  
Your _Converter_ can simply implement _ClientConverter_ and provide a implementation on the client side.  
Here is the example of the built on _BooleanConverter_:

```java
public class BooleanConverter extends javax.faces.convert.BooleanConverter implements ClientConverter {

    @Override
    public String getConverterId() {
        return BooleanConverter.CONVERTER_ID;
    }
}
```

```js
PrimeFaces.converter['javax.faces.Boolean'] = {

    regex: /^[-+]?\d+$/,

    MESSAGE_ID: 'javax.faces.converter.BooleanConverter.BOOLEAN',

    convert: function(element, submittedValue) {
        if(submittedValue === null) {
            return null;
        }

        if(PrimeFaces.trim(submittedValue).length === 0) {
            return null;
        }

        var vc = PrimeFaces.validation.ValidationContext;

        try {
            return ((submittedValue === 'true' || submittedValue === 'on' || submittedValue === 'yes') ? true : false);
        }
        catch(exception) {
            throw vc.getMessage(this.MESSAGE_ID, submittedValue, vc.getLabel(element));
        }
    }
};
```

## Multi-Field / Complex validation

CSV has a built in mechanism to validate container elements by using the _data-p-val_ data attribute.  
Just make sure that the container element is processed.

```js
PrimeFaces.validator['MyComplexValidator'] = {

    validate: function (element) {
        var nameRequired = element.find('#form\\:nameRequired');
        var name = element.find('#form\\:name');

        if (nameRequired.find('.ui-chkbox-icon').hasClass('ui-icon-check') && name.val().length === 0) {
            PrimeFaces.validator.Highlighter.types['default'].highlight(name);

            throw {
                summary: 'Validation Error',
                detail: 'A name is required!'
            };
        } else {
            PrimeFaces.validator.Highlighter.types['default'].unhighlight(name);
        }
    }
};
```

```html
<h:form id="form">
    <p:panel header="Validate" pt:data-p-val="MyComplexValidator">
        <p:messages showDetail="true">
            <p:autoUpdate/>
        </p:messages>

        <h:panelGrid id="grid" columns="3" cellpadding="5">
            <p:outputLabel for="@next" value="Name required"/>
            <p:selectBooleanCheckbox id="nameRequired" value="#{complexValidationView.nameRequired}"/>
            <p:message for="@previous"/>

            <p:outputLabel for="@next" value="Name"/>
            <p:inputText id="name" value="#{complexValidationView.name}" label="Name"/>
            <p:message for="@previous"/>

            <p:outputLabel for="@next" value="Accept Terms And Conditions"/>
            <p:selectBooleanCheckbox id="terms" value="#{complexValidationView.acceptTermnsAndCondition}"/>
            <p:message for="@previous"/>
        </h:panelGrid>

        <p:commandButton value="Non-Ajax" ajax="false" icon="pi pi-check" validateClient="true" />
        <p:commandButton value="Ajax" id="btnAjax" update="grid" icon="pi pi-check" validateClient="true" />
    </p:panel>
</h:form>
```