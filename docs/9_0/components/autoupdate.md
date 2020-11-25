# AutoUpdate

AutoUpdate is a tag handler to mark a component to be updated at every ajax request.

## Info

| Name | Value |
| --- | --- |
| Tag | autoUpdate
| Handler Class | org.primefaces.component.autoupdate.AutoUpdateTagHandler

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| disabled | false | Boolean | Whether the autoUpdate functionality is enabled.
| on | null | String | Defines an observer event, which will trigger the auto update.

## Getting Started with AutoUpdate
AutoUpdate is used by nesting inside a parent component.

```xhtml
<p:panel>
    <p:autoUpdate />
</p:panel>
```

## Event/Observer Pattern
If you would like to subscribe only to certain events when updating components you can use the observer pattern.
Use the `on` attribute to determine what events you would like to **observe** to. Then you can publish
that **event** from many different components using `update="@obs(event)"`.

```xhtml
<p:commandButton value="Update global" />
<p:commandButton value="Update event1" update="@obs(event1)" />
<p:commandButton value="Update event2" update="@obs(event2)" />


Global:
<h:outputText id="displayGlobal" value="#{observerView.text}">
    <p:autoUpdate />
</h:outputText>


 Event1:
<h:outputText id="displayEvent1" value="#{observerView.text}">
    <p:autoUpdate on="event1" />
</h:outputText>


Event2:
<h:outputText id="displayEvent2" value="#{observerView.text}">
    <p:autoUpdate on="event2" />
</h:outputText>
```
