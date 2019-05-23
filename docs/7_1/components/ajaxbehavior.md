# AjaxBehavior

AjaxBehavior is an extension to standard f:ajax.

## Info

| Name | Value |
| --- | --- |
| Tag | ajax |
| Behavior Id | org.primefaces.component.AjaxBehavior |
| Behavior Class | org.primefaces.component.behavior.ajax.AjaxBehavior |

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| listener | null | Method Expr | Method to process in partial request. |
| immediate | false | Boolean | Boolean value that determines the phaseId, when true actions are processed at apply_request_values, when false at invoke_application phase. |
| async | false | Boolean | When set to true, ajax requests are not queued. |
| process | @this | String | Component(s) to process in partial request. |
| update | @none | String | Component(s) to update with ajax. |
| onstart | null | String | Client-side javascript callback to execute before ajax request is begins. |
| oncomplete | null | String | Client-side javascript callback to execute when ajax request is completed. |
| onsuccess | null | String | Client-side javascript callback to execute when ajax request succeeds. |
| onerror | null | String | Client-side javascript callback to execute when ajax request fails. |
| global | true | Boolean | Global ajax requests are listened by ajaxStatus component, setting global to false will not trigger ajaxStatus. |
| delay | null | String | If less than delay milliseconds elapses between calls to request() only the most recent one is sent and all other requests are discarded. If this option is not specified, or if the value of delay is the literal string 'none' without the quotes, no delay is used. |
| partialSubmit | false | Boolean | Enables serialization of values belonging to the partially processed components only. |
| partialSubmitFilter | null | String | Selector to use when partial submit is on, default is ":input" to select all descendant inputs of a partially processed components. |
| disabled | false | Boolean | Disables ajax behavior. |
| event | null | String | Client side event to trigger ajax request. |
| resetValues | false | Boolean | If true, local values of input components to be updated within the ajax request would be reset. |
| ignoreAutoUpdate | false | Boolean | If true, components which autoUpdate="true" will not be updated for this request. If not specified, or the value is false, no such indication is made. |
| form | null | String | Form to serialize for an ajax request. Default is the enclosing form. |
| skipChildren | true | Boolean | Containers components like, datatable, panel, tabview skip their children if the request owner is them. For example, sort, page event of a datatable. As children are skipped, input values get lost, assume a case with a datatable and inputs components in a column. Sorting the column discards the changes and data is sorted according to original value. Setting skipChildren to false, enabled input values to update the value and sorting to be happened with user values. |


## Getting Started with AjaxBehavior
AjaxBehavior is attached to the component to ajaxify.

```xhtml
<h:inputText value="#{bean.text}">
    <p:ajax update="out" />
</h:inputText>
<h:outputText id="out" value="#{bean.text}" />
```
In the example above, each time the input changes, an ajax request is sent to the server. When the
response is received output text with id "out" is updated with value of the input.

## Listener
In case you need to execute a method on a backing bean, define a listener;

```xhtml
<h:inputText id="counter">
    <p:ajax update="out" listener="#{counterBean.increment}"/>
</h:inputText>
<h:outputText id="out" value="#{counterBean.count}" />
```

```java
public class CounterBean {
    private int count; //getter setter
    
    public void increment() {
        count++;
    }
}
```

## Events
Default client side events are defined by components that support client behaviors, for input
components it is _onchange_ and for command components it is _onclick_. In order to override the dom
event to trigger the ajax request use _event_ option. In following example, ajax request is triggered
when key is up on input field.

```xhtml
<h:inputText id="firstname" value="#{bean.text}">
    <p:ajax update="out" event="keyup"/>
</h:inputText>
<h:outputText id="out" value="#{bean.text}" />
```