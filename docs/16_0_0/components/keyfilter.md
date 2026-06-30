# KeyFilter

KeyFilter is used to filter keyboard input on specified input components.

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

