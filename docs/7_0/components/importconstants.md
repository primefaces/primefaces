# ImportConstants

In older EL versions (< 3.0), it's not possible to use constants or any other static fields/methods in
an EL expression. As it is not really a good practive to create beans with getter/setter for each
constants class. ImportConstant is an utility tag which allows to import constant fields in a page.
The constants can be accessed via the name of the class (default setting) or via a custom name (var
attribute).

## Info

| Name | Value |
| --- | --- |
| Tag | importConstants
| Handler | org.primefaces.component.importconstants.ImportConstantsTagHandler

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
type | null | String | Name of the class containing the constants.
var | null | String | Variable name to expose to EL.

## Getting Started with ImportConstants
Class whose constants would be imported is defined with type property and the var property
specifies the variable name to use via EL.

```java
package org.primefaces.util;

public class Constants {
    public static final String DOWNLOAD_COOKIE = "primefaces.download";
    public final static String LIBRARY = "primefaces";
    public final static String PUSH_PATH = "/primepush";
}
```
```xhtml
<p:importConstants type="org.primefaces.util.Constants" var="PFConstants" />
<h:outputText value="#{PFConstants.LIBRARY}" />
<h:outputText value="#{PFConstants.DOWNLOAD_COOKIE}" />
```
