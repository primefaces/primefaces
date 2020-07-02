# KeyFilter

KeyFilter is used to filter keyboard input on specified input components.

## Info

| Name | Value |
| --- | --- |
| Tag | keyFilter
| Component Class | org.primefaces.component.keyfilter.KeyFilter
| Component Type | org.primefaces.component.KeyFilter
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.KeyFilter
| Renderer Class | org.primefaces.component.keyfilter.KeyFilterRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component than can be either an EL expression of a literal text
widgetVar | null | String | Name of the client side widget.
for | null | String | The target input expression, defaults to parent.
regEx | null | String | Defines the regular expression which should be used for filtering the input.
inputRegEx | null | String | Defines the regular expression which should be used to test the complete input text content.
mask | null | String | Defines the predefined mask which should be used (pint, int, pnum, num, hex, email, alpha, alphanum).
testFunction | null | String | Defines a javascript code or function which should be used for filtering.
preventPaste | true | Boolean | Boolean value to specify if the component also should prevent paste.

## Getting Started with KeyFilter
KeyFilter can either be attached to an input using for property or by being nested inside the target
input component. Filtering is applied using regex, mask or testFunction properties.

```xhtml
<h:form>
    <h:panelGrid columns="2">
        <h:outputText value="KeyFilter with regEx on a p:inputText"/>
        <p:inputText id="text1">
            <p:keyFilter regEx="/[ABC]/i"/>
        </p:inputText>
        <h:outputText value="KeyFilter with mask on a h:inputText"/>
        <h:inputText id="text2" />
        <h:outputText value="KeyFilter with testFunction on a p:autoComplete" />
        <p:autoComplete id="autoComplete1" value="#{autoCompleteView.txt1}" completeMethod="#{autoCompleteView.completeText}" />
    </h:panelGrid>
    <p:keyFilter for="text2" mask="num" />
    <p:keyFilter for="autoComplete1" testFunction="return c == 'z';" />
</h:form>
```
There are also predefined masks for common formats;

**Mask Pattern**
- pint /[\d]/
- int /[\d\-]/
- pnum /[\d\.]/
- money /[\d\.\s,]
- num /[\d\-\.]/
- hex /[0-9a-f]/i
- email /[a-z0-9_\.\-@]/i
- alpha /[a-z_]/i
- alphanum /[a-z0-9_]/i

