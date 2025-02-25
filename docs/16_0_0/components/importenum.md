# ImportEnum

In older EL versions (< 3.0), it's not possible to use enum constants or any other static
fields/methods in an EL expression. As it is not really a good practice to create beans with
getter/setter for each constants class, we provide a utils tag which allows to import enum values in
a page.

The enum values can be accessed via the name of the class (default setting) or via a custom name
(var attribute). It is also possible to get all enum values of the class with the "ALL_VALUES" key.

## Info

| Name | Value |
| --- | --- |
| Tag | importEnums
| Handler | org.primefaces.component.importenum.ImportEnumTagHandler

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
type | null | String | Name of the class containing the constants.
var | null | String | Variable name to expose to EL.
~~allSuffix~~ | null | String | **Deprecated**: Use the special key `ALL_VALUES` to access all values, which is and was always available. Suffix name to retrieve all values.

## Getting Started with ImportEnum
Class whose enums would be imported is defined with type property and the var property specifies
the variable name to use via EL.

```xhtml
<p:importEnum type="jakarta.faces.application.ProjectStage" var="FacesProjectStages" />
Development: \#{FacesProjectStages.Development}
```
```xhtml
ALL
<ui:repeat var="current" value="#{FacesProjectStages.ALL_VALUES}">
    <h:outputText value="#{current}" />
</ui:repeat>
```

