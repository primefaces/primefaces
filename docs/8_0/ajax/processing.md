# Partial Processing

In Partial Page Rendering, only specified components are rendered, similarly in Partial Processing
only defined components are processed. Processing means executing Apply Request Values,
Process Validations, Update Model and Invoke Application JSF lifecycle phases only on defined
components.

This feature is a simple but powerful enough to do group validations, avoiding validating unwanted
components, eliminating need of using immediate and many more use cases. Various components
such as commandButton, commandLink are equipped with process attribute, in examples we’ll be
using commandButton.

## Partial Validation

A common use case of partial process is doing partial validations, suppose you have a simple
contact form with two dropdown components for selecting city and suburb, also there’s an inputText
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
When the city dropdown is changed an ajax request is sent to execute populateSuburbs method
which populates suburbChoices and finally update the suburbs dropdown. Problem is
populateSuburbs method will not be executed as lifecycle will stop after process validations phase
to jump render response as email input is not provided. Reason is p:ajax has @all as the value
stating to process every component on page but there is no need to process the inputText.

The solution is to define what to process in p:ajax. As we’re just making a city change request, only
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
That is it, now populateSuburbs method will be called and suburbs list will be populated. Note that
default value for process option is @this already for p:ajax as stated in AjaxBehavior
documentation, it is explicitly defined here to give a better understanding of how partial processing
works.

## Using Ids

Partial Process uses the same technique applied in partial updates to specify component identifiers
to process.