# Client Side Validation - Bean Validation

CSV has built-in integration with Bean Validation, by validating the constraints defined with annotations at client side.

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


## Implement a client side BeanValidation constraint

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

