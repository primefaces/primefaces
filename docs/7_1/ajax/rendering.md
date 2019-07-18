# Partial Rendering

In addition to components like autoComplete, datatable, slider with built-in ajax capabilities,
PrimeFaces also provides a generic PPR (Partial Page Rendering) mechanism to update JSF
components with ajax. Several components are equipped with the common PPR attributes (e.g.
update, process, onstart, oncomplete).

## Infrastructure

PrimeFaces Ajax Framework is based on standard server side APIs of JSF 2. There are no additional
artfacts like custom AjaxViewRoot, AjaxStateManager, AjaxViewHandler, Servlet Filters,
HtmlParsers, PhaseListeners and so on. PrimeFaces aims to keep it clean, fast and lightweight.

On client side rather than using client side API implementations of JSF implementations like
Mojarra and MyFaces, PrimeFaces scripts are based on the most popular javascript library; jQuery
which is far more tested, stable regarding ajax, dom handling, dom tree traversing than a JSF
implementations scripts.

## Using IDs

**Getting Started**

When using PPR you need to specify which component(s) to update with ajax. If the component
that triggers PPR request is at the same namingcontainer (eg. form) with the component(s) it
renders, you can use the server ids directly. In this section although we’ll be using commandButton,
same applies to every component that’s capable of PPR such as commandLink, poll,
remoteCommand and etc.

```xhtml
<h:form>
    <p:commandButton update="display" />
    <h:outputText id="display" value="#{bean.value}"/>
</h:form>
```
**PrependId**

Setting prependId setting of a form has no effect on how PPR is used.

```xhtml
<h:form prependId="false">
    <p:commandButton update="display" />
    <h:outputText id="display" value="#{bean.value}"/>
</h:form>
```

**ClientId**

It is also possible to define the client id of the component to update.

```xhtml
<h:form id="myform">
    <p:commandButton update="myform:display" />
    <h:outputText id="display" value="#{bean.value}"/>
</h:form>
```
**Different NamingContainers**

If your page has different naming containers (e.g. two forms), you also need to add the container id
to search expression so that PPR can handle requests that are triggered inside a namingcontainer
that updates another namingcontainer. Following is the suggested way using separator char as a
prefix, note that this uses same search algorithm as standard JSF 2 implementation;

```xhtml
<h:form id="form1">
    <p:commandButton update=":form2:display" />
</h:form>
<h:form id="form2">
    <h:outputText id="display" value="#{bean.value}"/>
</h:form>
```
Please read **findComponent** algorithm described in link below used by both JSF core and
PrimeFaces to fully understand how component referencing works.

http://docs.oracle.com/javaee/6/api/javax/faces/component/UIComponent.html

JSF h:form, datatable, composite components are naming containers, in addition tabView,
accordionPanel, dataTable, dataGrid, dataList, carousel, galleria, ring, sheet and subTable are
PrimeFaces component that implement NamingContainer.

**Multiple Components**

Multiple components to update can be specified with providing a list of ids separated by a comma,
whitespace or even both.

```xhtml
<h:form>
    <p:commandButton update="display1,display2" />
    <p:commandButton update="display1 display2" />
    <h:outputText id="display1" value="#{bean.value1}"/>
    <h:outputText id="display2" value="#{bean.value2}"/>
</h:form>
```

## Notifying Users

ajaxStatus is the component to notify the users about the status of **global** ajax requests. See the
ajaxStatus section to get more information about the component.

**Global vs Non-Global**

By default ajax requests are global, meaning if there is an ajaxStatus component present on page, it
is triggered.

If you want to do a "silent" request not to trigger ajaxStatus instead, set global to false. An example
with commandButton would be;

```xhtml
<p:commandButton value="Silent" global="false" />
<p:commandButton value="Notify" global="true" />
```

## Dynamic/Runtime Updates

Conditional UI update is quite common where different parts of the page need to be updated based
on a dynamic condition. In this case, it is not efficient to use declarative update and defined all
update areas since this will cause unnecessary updates.

There may be cases where you need to define which component(s) to update at runtime rather than specifying it declaratively
 _update_ method is added to handle this case. In example below, button actionListener decides which part of the page to update on-the-fly.

```xhtml
<p:commandButton value="Save" action="#{bean.save}" />
<p:panel id="panel"> ... </p:panel>
<p:dataTable id="table"> ... </p:panel>
```
```java
public void save() {
    //boolean outcome = ...
    if(outcome)
        PrimeFaces.current().ajax().update("panel");
    else
        PrimeFaces.current().ajax().update("table");
}
```
When the save button is clicked, depending on the outcome, you can either configure the datatable
or the panel to be updated with AJAX response.