# AJAX Processing Requirements

AJAX requires a form to submit requests. When looking for a form to use, components will search in this order:

1. If an explicit form ID is defined in the component configuration, it will use that form
2. Otherwise, it will look for the closest parent form of the component
3. If no parent form exists, it will use the first form found in the document

If no form is available, AJAX will not function correctly and each AJAX request will throw an HTTP 405 Method Not Allowed. Therefore, ensure your components that use AJAX are placed within a form element if possible.

Alternatively, you can place an empty form at the start of your page to provide a fallback form for submission.

```xml
<h:body>
   <h:form id="emptyForm"></h:form>
   <p:accordionPanel>
   ...

</h:body>
```

# Partial Processing

In Partial Page Rendering, only specified components are rendered, similarly in Partial Processing
only defined components are processed. Processing means executing `Apply Request Values`,
`Process Validations`, `Update Model` and `Invoke Application` Jakarta Faces lifecycle phases only on defined
components.

This feature is a simple but powerful enough to do group validations, avoiding validating unwanted
components, eliminating need of using immediate and many more use cases. Various components
such as `commandButton`, `commandLink` are equipped with `process` attribute, in examples we’ll be
using `commandButton`.

## Partial Validation

A common use case of partial process is doing partial validations, suppose you have a simple
contact form with two dropdown components for selecting city and suburb, also there’s an `inputText`
which is required. When city is selected, related suburbs of the selected city is populated in suburb
dropdown.

```xhtml
<h:form>
    <h:selectOneMenu id="cities" value="#{bean.city}">
        <f:selectItems value="#{bean.cityChoices}" />
        <p:ajax listener="#{bean.populateSuburbs}" update="suburbs" process="@all"/>
    </h:selectOneMenu>
    <h:selectOneMenu id="suburbs" value="#{bean.suburb}">
        <f:selectItems value="#{bean.suburbChoices}" />
    </h:selectOneMenu>
    <h:inputText value="#{bean.email}" required="true"/>
</h:form>
```
When the city dropdown is changed an AJAX request is sent to execute _populateSuburbs_ method
which populates suburbChoices and finally update the suburbs dropdown. Problem is
_populateSuburbs_ method will not be executed as lifecycle will stop after `Process Validations` phase
to jump render response as email input is not provided. Reason is `p:ajax` has `@all` as the value
stating to process every component on page but there is no need to process the inputText.

The solution is to define what to process in `p:ajax`. As we’re just making a city change request, only
processing that should happen is cities dropdown.


```xhtml
<h:form>
    <h:selectOneMenu id="cities" value="#{bean.city}">
        <f:selectItems value="#{bean.cityChoices}" />
        <p:ajax action="#{bean.populateSuburbs}" event="change" update="suburbs" process="@this"/>
    </h:selectOneMenu>
    <h:selectOneMenu id="suburbs" value="#{bean.suburb}">
        <f:selectItems value="#{bean.suburbChoices}" />
    </h:selectOneMenu>
    <h:inputText value="#{bean.email}" required="true"/>
</h:form>
```
That is it, now _populateSuburbs_ method will be called and suburbs list will be populated. Note that
default value for process option is `@this` already for `p:ajax` as stated in `AjaxBehavior`
documentation, it is explicitly defined here to give a better understanding of how partial processing
works.

## Using Ids

Partial Process uses the same technique applied in partial updates to specify component identifiers
to process.