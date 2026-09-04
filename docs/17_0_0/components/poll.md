# Poll

Poll is an ajax component that has the ability to send periodical ajax requests.

## Getting started with Poll
Poll below invokes increment method on CounterBean every 2 seconds and _txt_count_ is updated
with the new value of the count variable. Note that poll must be nested inside a form.

```xhtml
<h:outputText id="txt_count" value="#{counterBean.count}" />
<p:poll listener="#{counterBean.increment}" update="txt_count" />
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

## Tuning timing
By default the periodic interval is 2 seconds, this is changed with the interval attribute. Following
poll works every 5 seconds.

```xhtml
<h:outputText id="txt_count" value="#{counterBean.count}" />
<p:poll listener="#{counterBean.increment}" update="txt_count" interval="5" />
```
## Start and Stop
Poll can be started and stopped using client side api;

```xhtml
<h:form>
    <h:outputText id="txt_count" value="#{counterBean.count}" />
    <p:poll interval="5" listener="#{counterBean.increment}" update="txt_count" widgetVar="myPoll" autoStart="false" />
    <a href="#" onclick="PF('myPoll').start();">Start</a>
    <a href="#" onclick="PF('myPoll').stop();">Stop</a>
</h:form>
```
Or bind a boolean variable to the _stop_ attribute and set it to false at any arbitrary time.

