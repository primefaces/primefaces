# Search Expression Framework

PrimeFaces enhances the Faces Search Expressions with own keywords, which are part of the Faces specs since 2.3.

## Keywords

Keywords are the easier way to reference components, they resolve to ids so that if an id changes,
the reference does not need to change. Core JSF provides a couple of keywords and PrimeFaces
provides more along with composite expression support.


### Faces
| Keyword | Description |
| --- | --- |
@this | Current component.
@all | Whole view.
@form | Closest ancestor form of current component.
@none | No component.
@namingcontainer | Closest ancestor naming container of current component.
@parent | Parent of the current component.
@composite | Closest composite component ancestor.
@child(n) | nth child.
@previous | Previous sibling.
@next | Next sibling.
@root | UIViewRoot instance of the view, can be used to start searching from the root instead the current component.
@id(id) | Used to search components by their id ignoring the component tree structure and naming containers.

### PrimeFaces additional keywords
| Keyword | Description |
| --- | --- |
@row(n) | nth row.
@obs(event) | targeting a event. See [Event/Observer Pattern](/components/autoupdate.md)
@widgetVar(name) | Component with given widgetVar.
@(selector) | PFS / PrimeFaces Selectors.

Consider the following case where ids are used for referencing;

```xhtml
<h:form id="form1">
    <p:commandButton id="btn" update="form1" process="btn" />
    <h:outputText value="#{bean.value}"/>
</h:form>
```
Using keywords, same can be written as;


```xhtml
<h:form id="form1">
    <p:commandButton id="btn" update="@form" process="@this" />
    <h:outputText value="#{bean.value}"/>
</h:form>
```

### Composite Expressions
Multiple keywords can be combined in a single expression using colon;

- @form:@parent
- @composite:mybuttonid
- @this:@parent:@parent
- @form:@child(2)

### Usage Scenarios
SEF is not just at partial process and update, they are also available whenever a component is
referencing another.

```xhtml
<h:form>
    <p:commandButton id="dynaButton" value="Show" type="button" />
    <p:menu overlay="true" trigger="@parent:dynaButton">
        //items
    </p:menu>
</h:form>
```

## PrimeFaces Selectors (PFS)

PFS integrates jQuery Selector API with JSF component referencing model so that referencing can
be done using jQuery Selector API instead of core id based JSF model. Best way to explain the
power of PFS is examples;

Update all forms

```xhtml
update="@(form)"
```
Update first form

```xhtml
update="@(form:first)"
```
Update all components that has styleClass named mystyle

```xhtml
update="@(.mystyle)"
```
Update and process all inputs

```xhtml
update="@(:input)" process="@(:input)"
```

Update all datatables

```xhtml
update="@(.ui-datatable)"
```
Process input components inside any panel and update all panels

```xhtml
process="@(.ui-panel :input)" update="@(.ui-panel)"
```
Process input components but not select components

```xhtml
process="@(:input:not(select))"
```
Update input components that are disabled

```xhtml
update="@(:input:disabled)"
```
PFS can be used with other referencing approaches as well;

```xhtml
update="compId :form:compId @(:input) @parent:@child(2)"
```
```xhtml
<h:form>
    <p:commandButton id="dynaButton" value="Show" type="button" styleClass="btn"/>
    <p:menu overlay="true" trigger="@(.btn)">
        //items
    </p:menu>
</h:form>
```
PFS provides an alternative, flexible, grouping based approach to reference components to partially
process and update.
There is less CPU server load compared to regular referencing, because JSF component tree is not traversed on server side
(to find a component and figure out the client id) as PFS is implemented on client side by looking at DOM tree.

Another advantage is avoiding naming container limitations.
Just remember the times you’ve faced with "cannot find component" exception,
since the component you are looking for is in a different naming container like a form or a
datatable. PFS can help you out in tricky situations by following jQuery’s “write less do more” style.

For PFS to function properly and not to miss any component, it is required to have explicitly
defined ids on the matched set as core JSF components usually do not render auto ids. So even
though manually defined ids won't be referenced directly, they are still required for PFS to be
collected and send in the request.

For full reference of jQuery selector api, see;

http://api.jquery.com/category/selectors/

Search expression has some limitations. In the issue links below, these situations are tried to be clearly stated.

https://github.com/primefaces/primefaces/issues/8905 <br />
https://github.com/primefaces/primefaces/issues/8990