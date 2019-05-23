# BeanValidation Transformation

Since JavaEE 6, validation metadata is already available for many components via the value
reference (EL ValueExpression) and BeanValidation (e.g. @NotNull, @Size). The JSF Implementations use this
information for server side validation and PrimeFaces enhances this feature with client side
validation framework.

PrimeFaces makes use of these metadata by transforming them to component and html attributes.
For example sometimes it’s required to manually maintain the required attribute or maxlength attribute for
input components. The required attribute also controls the behavior of p:outputLabel to show or
hide the required indicator (*) whereas the _maxlength_ attribute is used to limit the characters on
input fields. BeanValidation transformation features enables avoiding manually maintaining these
attributes anymore by implicility handling them behind the scenes.

## Configuration
To start with, transformation should be enabled.

```xml
<context-param>
    <param-name>primefaces.TRANSFORM_METADATA</param-name>
    <param-value>true</param-value>
</context-param>
```

To enable full support for `@NotNull`, `InterpretEmptyStringAsNull` has to be set to `true`. Otherwise no required indicator will be shown for that constraint.

```xml
<context-param>
    <param-name>javax.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL</param-name>
    <param-value>true</param-value>
</context-param>
```

## Usage
Define constraints at bean level.

```java
@NotNull
@Max(30)
private String firstname;
```
Component at view does not have any constraints;

```xhtml
<p:inputText value="#{bean.firstname}" />
```
Final output has html maxlength attribute generated from the @Max annotation, also the component
instance has required enabled.

```xhtml
<input type="text" maxlength="30" ... />
```
