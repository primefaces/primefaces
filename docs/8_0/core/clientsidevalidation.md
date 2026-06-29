# Client Side Validation

PrimeFaces Client Side Validation (CSV) Framework is the most complete and advanced CSV
solution for JavaServer Faces and Java EE. CSV support for JSF is not an easy task, it is not simple
as integrating a 3rd party javascript plugin as JSF has its own lifecycle, concepts like conversion
and then validation, partial processing, facesmessages and many more. Real CSV for JSF should be
compatible with server side implementation, should do what JSF does, so that users do not
experience difference behaviors on client side and server side.

- Compatible with Server Side Implementation.
- Conversion and Validation happens at client side.
- Partial Process&Update support for Ajax.
- I18n support along with component specific messages.
- Client side Renderers for message components.
- Easy to write custom client converters and validators.
- Global or Component based enable/disable.
- Advanced Bean Validation Integration.
- Little footprint using HTML5.

## Configuration

CVS is disabled by default and a global parameter is required to turn it on.

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

## Ajax vs Non-Ajax

CSV works differently depending on the request type of the trigger component to be compatible
with cases where CVS is not enabled.


**Non-Ajax**
In non-ajax case, all visible and editable input components in the form are validated and message
components must be placed inside the form.

**Ajax**
CSV supports partial processing and updates on client side as well, if process attribute is enabled,
the components that would be processed at server side gets validated at client side. Similary if
update attribute is defined, only message components inside the updated parts gets rendered. Whole
process happens at client side.

## Events

CSV provides a behavior called p:clientValidator to do instant validation in case you do not want to
wait for the users to fill in the form and hit commandButton/commandLink. Using clientBehavior
and custom events, CSV for a particular component can run with events such as change (default),
blur, keyup.

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
## Messages

Validation errors are displayed as the same way in server side validation, texts are retrieved from a
client side bundle and message components are required for the displays.

**I18N**
Default language is English for the CSV messages and for other languages or to customize the
default messages, PrimeFaces Locales bundle needs to be present at the page if you'd like to provide
translations. For more info on PrimeFaces Locales, visit
[https://github.com/primefaces/primefaces/wiki/Locales.](https://github.com/primefaces/primefaces/wiki/Locales.)


**Rendering**
PrimeFaces message components have client side renderers for CSV support, these are p:message,
p:messages and p:growl. Component options like showSummary, showDetail, globalOnly, mode are
all implemented by client side renderer for compatibility.

**MyFaces vs Mojarra**
Bean validation messages between implementations have a slight difference regarding labels,
mojarra do not the label of the field but myfaces does. For example;

```js
Mojarra:
javax.faces.validator.BeanValidator.MESSAGE={0}
MyFaces:
javax.faces.validator.BeanValidator.MESSAGE={1}: {0}
```
Default CSV messages follow the convention of Mojarra however if you prefer to display the label
along with the message, override can be done by adding {1} to the message;

```js
Default:
PrimeFaces.locales['en_US'].messages['javax.faces.validator.BeanValidator.MESSAGE'] =
'{0}';
With Label:
PrimeFaces.locales['en_US'].messages['javax.faces.validator.BeanValidator.MESSAGE'] =
'{1}: {0}';
```
## Bean Validation

CSV has built-in integration with Bean Validation by validating the constraints defined with
annotations at client side.

```xhtml
<h:form>
    <p:growl />
    <h:panelGrid>
        <h:outputLabel for="name" value="Name:" />
        <p:inputText id="name" value="#{bean.name}" label="Name"/>
        <p:message for="name" />
        <h:outputLabel for="age" value="Age: (@Min(10) @Max(20))" />
        <p:inputText id="age" value="#{bean.age}" label="Age"/>
        <p:message for="age" />
    </h:panelGrid>
    <p:commandButton value="Save" validateClient="false" ajax="false" />
</h:form>
```

```java
public class Bean {
    @Size(min=2,max=5)
    private String name;
    @Min(10) @Max(20)
    private Integer age;
}
```
All of the standard constraints are supported.

## Extending CSV

Using CSV APIs, it is easy to write your own custom converters and validators.

**Email Validator with JSF**

Your custom validator must implement ClientValidator interface to provide the client validator id
and the optional metadata.


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
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if(value == null) {
            return;
        }
        if(!pattern.matcher(value.toString()).matches()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", value + " is not a valid email;"));
        }
    }
    public Map<String, Object> getMetadata() {
        return null;
    }
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
In some cases your validator might need metadata, for example LengthValidator requires min and
max constraints to validate against. Server side validator can pass these by overriding the
_getMetadata()_ method by providing a map of name,value pairs. At client side, these are accessed via
_element.data(key)_.

```java
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
Similarly a client side converter can be written by implementing ClientConverter API and
overriding _convert: function(element, submittedValue) {}_ method to return a javascript object.

**Email Validator with Bean Validation**
Bean Validation is also supported for extensions, here is an example of a @Email validator.

```java
//imports
import org.primefaces.validate.bean.ClientConstraint;

@Target({METHOD,FIELD,ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy=EmailConstraintValidator.class)
@ClientConstraint(resolvedBy=EmailClientValidationConstraint.class)
@Documented
public @interface Email {
    String message() default "{org.primefaces.examples.primefaces}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

@Constraint is the regular validator from Bean Validation API and @ClientConsraint is from CSV
API to resolve metadata.

```java
public class EmailConstraintValidator implements ConstraintValidator<Email, String>{
    private Pattern pattern;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    public void initialize(Email a) {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }
    public boolean isValid(String value, ConstraintValidatorContext cvc) {
        if(value == null)
            return true;
        else
            return pattern.matcher(value.toString()).matches();
    }
}
```
```java
public class EmailClientValidationConstraint implements ClientValidationConstraint {
    public static final String MESSAGE_METADATA = "data-p-email-msg";
    public Map<String, Object> getMetadata(ConstraintDescriptor constraintDescriptor) {
        Map<String,Object> metadata = new HashMap<String, Object>();
        Map attrs = constraintDescriptor.getAttributes();
        Object message = attrs.get("message");
        if(message != null) {
            metadata.put(MESSAGE_METADATA, message);
        }
        return metadata;
    }
    public String getValidatorId() {
        return Email.class.getSimpleName();
    }
}
```

Final part is implementing the client side validator;

```js
PrimeFaces.validator['Email'] = {
    pattern: /\S+@\S+/,
    MESSAGE_ID: 'org.primefaces.examples.validate.email.message',
    validate: function(element, value) {
        var vc = PrimeFaces.util.ValidationContext;
        if(!this.pattern.test(value)) {
            var msgStr = element.data('p-email-msg'),
            msg = msgStr? {summary:msgStr, detail: msgStr} :
            vc.getMessage(this.MESSAGE_ID);
            throw msg;
        }
    }
};
```
Usage is same as using standard constraints;

```xhtml
<h:form>
    <p:messages />
    <p:inputText id="email" value="#{bean.value}" />
    <p:message for="email" />
    <p:commandButton value="Save" validateClient="true" ajax="false"/>
</h:form>
```
```java
public class Bean {
    @Email
    private String value;
    //getter-setter
}
```
**3 rd Party Annotations**
When using 3rd party constraints like Hibernate Validator specific annotations, use
BeanValidationMetadataMapper to define a ClientValidationConstraint for them.

```java
BeanValidationMetadataMapper.registerConstraintMapping(Class<? extends Annotation> constraint, ClientValidationConstraint clientValidationConstraint);
```
```java
BeanValidationMetadataMapper.removeConstraintMapping(Class<? extends Annotation> constraint);
```