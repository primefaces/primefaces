# Dialog Framework

Dialog Framework (DF) is used to open an external xhtml page in a dialog that is generated
dynamically on runtime. This is quite different to regular usage of dialogs with declarative p:dialog
components as DF is based on a programmatic API where dialogs are created and destroyed at
runtime.

Note that DF and the declarative approach are two different ways and both can even be
used together. Usage is quite simple, PrimeFaces.current().dialog() has _openDynamic_ and _closeDynamic_
methods;

```java
/**
* Open a view in dialog.
* @param outcome The logical outcome used to resolve a navigation case.
*/
public abstract void openDynamic(String outcome);
/**
* Open a view in dialog.
* @param outcome The logical outcome used to resolve a navigation case.
* @param options Configuration options for the dialog.
* @param params Parameters to send to the view displayed in a dialog.
*/
public abstract void openDynamic(String outcome, Map<String,Object> options, Map<String,List<String>> params);
/**
* Close a dialog.
* @param data Optional data to pass back to a dialogReturn event.
*/
public abstract void closeDynamic(Object data);
```

**Configuration**
DF requires the following configuration to be present in faces config file.

```xml
<application>
    <action-listener>
        org.primefaces.application.DialogActionListener
    </action-listener>
    <navigation-handler>
        org.primefaces.application.DialogNavigationHandler
    </navigation-handler>
    <view-handler>
        org.primefaces.application.DialogViewHandler
    </view-handler>
</application>
```
**Getting Started**
Simplest use case of DF is opening an xhtml view like _cars.xhtml_ in a dialog;


```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html"
xmlns:p="http://primefaces.org/ui">
    <h:head>
        <title>Cars</title>
    </h:head>
    <h:body>
        <p:dataTable var="car" value="#{tableBean.cars}">
            //columns
        </p:dataTable>
    </h:body>
</html>
```
On the host page, call PrimeFaces.current().dialog().openDynamic("viewname");

```xhtml
<p:commandButton value="View Cars" action="#{hostBean.view}" />
```
```java
public void view() {
    PrimeFaces.current().dialog().openDynamic("viewCars");
}
```
Once the response is received from the request caused by command button a dialog would be
generated with the contents of viewCars.xhtml. Title of the dialog is retrieved from the title element
of the viewCars, in this case, Cars.

**Dialog Configuration**
Overloaded openDialog method provides advanced configuration regarding the visuals of dialog
along with parameters to send to the dialog content.

```xhtml
<p:commandButton value="View Cars" action="#{hostBean.viewCustomized}" />
```
```java
public void view() {
    Map<String,Object> options = new HashMap<String, Object>();
    options.put("modal", true);
    options.put("draggable", false);
    options.put("resizable", false);
    options.put("contentHeight", 320);
    PrimeFaces.current().dialog().openDynamic("viewCars", options, null);
}
```

Here is the full list of configuration options;

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
widgetVar | null | String | Custom widgetVar of the dialog, if not declared it will be automatically created as "id+dlgWidget".
modal | false | Boolean | Controls modality of the dialog.
resizable | true | Boolean | When enabled, makes dialog resizable.
draggable | true | Boolean | When enabled, makes dialog draggable.
width | auto | String | Width of the dialog.
height | auto | String | Height of the dialog.
contentWidth | 640 | String | Width of the dialog content.
contentHeight | auto | String | Height of the dialog content.
closable | true | Boolean | Whether the dialog can be closed or not.
includeViewParams | false | Boolean | When enabled, includes the view parameters.
headerElement | null | String | Client id of the element to display inside header.
minimizable | false | Boolean | Makes dialog minimizable.
maximizable | false | Boolean | Makes dialog maximizable.
closeOnEscape | false | Boolean | Whether the dialog can be closed with escape key.
minWidth | 150 | Integer | Minimum width of a resizable dialog.
minHeight | 0 | Integer | Minimum height of a resizable dialog.
appendTo | null | String | Appends the dialog to the element defined by the given search expression.
dynamic | false | Boolean | Enables lazy loading of the content with ajax.
showEffect | null | String | Effect to use when showing the dialog
hideEffect | null | String | Effect to use when hiding the dialog
position | null | String | Defines where the dialog should be displayed
fitViewport | false | Boolean | Dialog size might exceeed viewport if content is bigger than viewport in terms of height. fitViewport option automatically adjusts height to fit dialog within the viewport.
responsive | false | Boolean | In responsive mode, dialog adjusts itself based on screen width.
focus | null | String | Defines which component to apply focus by search expression.
onShow | null | String | Client side callback to execute when dialog is displayed.
onHide | null | String | Client side callback to execute when dialog is hidden.

**Data Communication**
Page displayed in the dialog can pass data back to the parent page. The trigger component needs to
have _dialogReturn_ ajax behavior event to hook-in when data is returned from dialog.

```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html"
    xmlns:p="http://primefaces.org/ui">
    <h:head>
        <title>Cars</title>
    </h:head>
    <h:body>
        <p:dataTable var="car" value="#{tableBean.cars}">
            //columns
            <p:column headerText="Select">
                <p:commandButton icon="ui-icon-search" action="#{tableBean.selectCarFromDialog(car)}" />
            </p:column>
        </p:dataTable>
    </h:body>
</html>
```

```java
public void selectCarFromDialog(Car car) {
    PrimeFaces.current().dialog().closeDynamic(car);
}
```
At host page, the button that triggered the dialog should have _dialogReturn_ event.

```xhtml
<p:commandButton value="View Cars" action="#{hostBean.viewCars}">
    <p:ajax event="dialogReturn" listener="#{hostBean.handleReturn}" />
</p:commandButton>
```
```java
public void view() {
    PrimeFaces.current().dialog().openDynamic("viewCars");
}
public void handleReturn(SelectEvent event) {
    Car car = (Car) event.getObject();
}
```
**Remarks on Dialog Framework**

- Calls to DialogFramework API within a non-ajax are ignored.
- Content Width and Height can take percentage value like 100%.

**Dialog Messages**
Displaying FacesMessages in a Dialog is a common case where a faces message needs to be added
to the context first, dialog content containing a message component needs to be updated and finally
dialog gets shown with client side api. DF has a simple utility to bypass this process by providing a
shortcut;

```java
/**
* Displays a message in a dialog.
* @param message FacesMessage to be displayed.
*/
public abstract void showMessageInDialog(FacesMessage message);
```
Using this shortcut it is just one line to implement the same functionality;

```xhtml
<p:commandButton value="Show" action="#{bean.save}" />
```
```java
public void save() {
    //business logic
    PrimeFaces.current().dialog().showMessageInDialog(new
    FacesMessage(FacesMessage.SEVERITY_INFO, "What we do in life", "Echoes in eternity."););
}
```
