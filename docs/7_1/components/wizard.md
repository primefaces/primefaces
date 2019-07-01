# Wizard

Wizard provides an ajax enhanced UI to implement a workflow easily in a single page. Wizard
consists of several child tab components where each tab represents a step in the process.

## Info

| Name | Value |
| --- | --- |
| Tag | wizard
| Component Class | org.primefaces.component.wizard.Wizard
| Component Type | org.primefaces.component.Wizard
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.WizardRenderer
| Renderer Class | org.primefaces.component.wizard.WizardRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
step | 0 | String | Id of the current step in flow
style | null | String | Style of the main wizard container element.
styleClass | null | String | Style class of the main wizard container element.
flowListener | null | MethodExpr | Server side listener to invoke when wizard attempts to go forward or back.
showNavBar | true | Boolean | Specifies visibility of default navigator arrows.
showStepStatus | true | Boolean | Specifies visibility of default step title bar.
onback | null | String | Javascript event handler to be invoked when flow goes back.
onnext | null | String | Javascript event handler to be invoked when flow goes forward.
nextLabel | null | String | Label of next navigation button.
backLabel | null | String | Label of back navigation button.
widgetVar | null | String | Name of the client side widget
updateModelOnPrev | false | Boolean | If yes, the model will be updated when the "Back" button is clicked. Default is false.

## Ajax Behavior Events
Wizard provides custom ajax behavior events to configure the built-in AJAX requests.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
next | javax.faces.event.AjaxBehaviorEvent | When "next" is triggered
back | javax.faces.event.AjaxBehaviorEvent | When "back" is triggered

## Getting Started with Wizard
Each step in the flow is represented with a tab. As an example following wizard is used to create a
new user in a total of 4 steps where last step is for confirmation of the information provided in first
3 steps. To begin with create your backing bean, it’s important that the bean lives across multiple
requests so avoid a request scope bean. Optimal scope for wizard is viewScope.

```java
public class UserWizard {
    private User user = new User();

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void save(ActionEvent actionEvent) {
        //Persist user
        FacesMessage msg = new FacesMessage("Successful", "Welcome :" + user.getFirstname());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
```
_User_ is a simple pojo with properties such as firstname, lastname, email and etc. Following wizard
requires 3 steps to get the user data; Personal Details, Address Details and Contact Details. Note
that last tab contains read-only data for confirmation and the submit button.

```xhtml
<h:form>
    <p:wizard>
        <p:tab id="personal">
            <p:panel header="Personal Details">
                <h:messages errorClass="error"/>
                <h:panelGrid columns="2">
                    <h:outputText value="Firstname: *" />
                    <h:inputText value="#{userWizard.user.firstname}" required="true"/>
                    <h:outputText value="Lastname: *" />
                    <h:inputText value="#{userWizard.user.lastname}" required="true"/>
                    <h:outputText value="Age: " />
                    <h:inputText value="#{userWizard.user.age}" />
                </h:panelGrid>
            </p:panel>
        </p:tab>
        <p:tab id="address">
            <p:panel header="Adress Details">
                <h:messages errorClass="error"/>
                <h:panelGrid columns="2" columnClasses="label, value">
                    <h:outputText value="Street: " />
                    <h:inputText value="#{userWizard.user.street}" />
                    <h:outputText value="Postal Code: " />
                    <h:inputText value="#{userWizard.user.postalCode}" />
                    <h:outputText value="City: " />
                    <h:inputText value="#{userWizard.user.city}" />
                </h:panelGrid>
            </p:panel>
        </p:tab>
        <p:tab id="contact">
            <p:panel header="Contact Information">
                <h:messages errorClass="error"/>
                <h:panelGrid columns="2">
                    <h:outputText value="Email: *" />
                    <h:inputText value="#{userWizard.user.email}" required="true"/>
                    <h:outputText value="Phone: " />
                    <h:inputText value="#{userWizard.user.phone}"/>
                    <h:outputText value="Additional Info: " />
                    <h:inputText value="#{userWizard.user.info}"/>
                </h:panelGrid>
            </p:panel>
        </p:tab>
        <p:tab id="confirm">
            <p:panel header="Confirmation">
                <h:panelGrid id="confirmation" columns="6">
                    <h:outputText value="Firstname: " />
                    <h:outputText value="#{userWizard.user.firstname}"/>
                    <h:outputText value="Lastname: " />
                    <h:outputText value="#{userWizard.user.lastname}"/>
                    <h:outputText value="Age: " />
                    <h:outputText value="#{userWizard.user.age}" />
                    <h:outputText value="Street: " />
                    <h:outputText value="#{userWizard.user.street}" />
                    <h:outputText value="Postal Code: " />
                    <h:outputText value="#{userWizard.user.postalCode}"/>
                    <h:outputText value="City: " />
                    <h:outputText value="#{userWizard.user.city}"/>
                    <h:outputText value="Email: " />
                    <h:outputText value="#{userWizard.user.email}" />
                    <h:outputText value="Phone " />
                    <h:outputText value="#{userWizard.user.phone}"/>
                    <h:outputText value="Info: " />
                    <h:outputText value="#{userWizard.user.info}"/>
                    <h:outputText />
                    <h:outputText />
                </h:panelGrid>
                <p:commandButton value="Submit" action="#{userWizard.save}" />
            </p:panel>
        </p:tab>
    </p:wizard>
</h:form>
```
## AJAX and Partial Validations
Switching between steps is based on ajax, meaning each step is loaded dynamically with ajax.
Partial validation is also built-in, by this way when you click next, only the current step is validated,
if the current step is valid, next tab’s contents are loaded with ajax. Validations are not executed
when flow goes back.

## Navigations
Wizard provides two icons to interact with; next and prev. Please see the skinning wizard section to
know more about how to change the look and feel of a wizard.


## Custom UI
By default wizard displays right and left arrows to navigate between steps, if you need to come up
with your own UI, set _showNavBar_ to false and use the provided the client side api.

```xhtml
<p:wizard showNavBar="false" widgetVar="wiz">
...
</p:wizard>
<h:outputLink value="#" onclick="PF('wiz').next();">Next</h:outputLink>
<h:outputLink value="#" onclick="PF('wiz').back();">Back</h:outputLink>
```
## FlowListener
If you’d like get notified on server side when wizard attempts to go back or forward, define a
flowListener.

```xhtml
<p:wizard flowListener="#{userWizard.handleFlow}">
    ...
</p:wizard>
```
```java
public String handleFlow(FlowEvent event) {
    String currentStepId = event.getCurrentStep();
    String stepToGo = event.getNextStep();
    if(skip)
        return "confirm";
    else
        return event.getNextStep();
}
```
Steps here are simply the ids of tab, by using a flowListener you can decide which step to display
next so wizard does not need to be linear always. If you need to update other component(s) on page
within a flow, use _PrimeFaces.current().ajax().update(String clientId)_ API.

## Client Side Callbacks
Wizard is equipped with onback and onnext attributes, in case you need to execute custom
javascript after wizard goes back or forth. You just need to provide the names of javascript functions
as the values of these attributes.

```xhtml
<p:wizard onnext="alert(‘Next’)" onback="alert(‘Back’)">
    ...
</p:wizard>
```
## Client Side API
Widget: _PrimeFaces.widget.Wizard_


| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
next() | - | void | Proceeds to next step.
back() | - | void | Goes back in flow.
getStepIndex() | - | Number | Returns the index of current step.
showNextNav() | - | void | Shows next button.
hideNextNav() | - | void | Hides next button.
showBackNav() | - | void | Shows back button.
hideBackNav() | - | void | Hides back button.

## Skinning
Wizard resides in a container element that _style_ and _styleClass_ attributes apply. Following is the list
of structural css classes.

| Class | Applies |
| --- | --- |
.ui-wizard | Main container element.
.ui-wizard-content | Container element of content.
.ui-wizard-step-titles | Container of step titles.
.ui-wizard-step-title | Each step title.
.ui-wizard-navbar | Container of navigation controls.
.ui-wizard-nav-back | Back navigation control.
.ui-wizard-nav-next | Forward navigation control.

As skinning style classes are global, see the main theming section for more information.

