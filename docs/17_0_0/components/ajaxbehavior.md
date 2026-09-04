# Ajax (AjaxBehavior)

`p:ajax` is an extension to standard `f:ajax`.

## Getting Started with AjaxBehavior
AjaxBehavior is attached to the component to ajaxify.

```xhtml
<h:inputText value="#{bean.text}">
    <p:ajax update="out" />
</h:inputText>
<h:outputText id="out" value="#{bean.text}" />
```
In the example above, each time the input changes, an AJAX request is sent to the server. When the
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
@Named
@ViewScoped
public class CounterBean implements Serializable {

    private int count;

    public void increment() {
        count++;
    }

    public int getCount() {
        return this.count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
```

## Events
Default client side events are defined by components that support client behaviors, for input
components it is _onchange_ and for command components it is _onclick_. In order to override the DOM
event to trigger the AJAX request use _event_ option. In following example, AJAX request is triggered
when key is up on input field.

```xhtml
<h:inputText id="firstname" value="#{bean.text}">
    <p:ajax update="out" event="keyup"/>
</h:inputText>
<h:outputText id="out" value="#{bean.text}" />
```