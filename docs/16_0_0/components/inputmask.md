# InputMask

InputMask forces an input to fit in a defined mask template.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.InputMask.html)

## Getting Started with InputMask
InputMask below enforces input to be in 99/99/9999 date format.

```xhtml
<p:inputMask value="#{bean.field}" mask="99/99/9999" />
```

## Mask Characters
The mask supports the following characters and their meaning based on the underlying [InputMask JS Library](https://github.com/RobinHerbots/Inputmask):
  * `a` - Represents an alpha character (A-Z,a-z)
  * `A` - Represents an UPPERCASE alpha character (A-Z)
  * `9` - Represents a numeric character (0-9)
  * `*` - Represents an alphanumeric character (A-Z,a-z,0-9)
  * `[]` - Makes the input in between `[` and `]` optional

## Mask Samples
Here are more samples based on different masks;

```xhtml
<h:outputText value="Phone: " />
<p:inputMask value="#{bean.phone}" mask="(999) 999-9999"/>
<h:outputText value="Phone with Ext: " />
<p:inputMask value="#{bean.phoneExt}" mask="(999) 999-9999 [x99999]"/>
<h:outputText value="SSN: " />
<p:inputMask value="#{bean.ssn}" mask="999-99-9999"/>
<h:outputText value="Product Key: " />
<p:inputMask value="#{bean.productKey}" mask="a*-999-a999"/>
```

## Client Side API
Widget: _PrimeFaces.widget.AccordionPanel_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| getValue() | none | void | Returns the current value of this input field including the mask like "12/31/1999".|
| getValueUnmasked() | none | void | Returns the current value of this input field without the mask like "12311999".|
| setValue(value) |value: the new value | void | Sets the value of this input field to the given value. If the value does not fit the mask, it is adjusted appropriately.|
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |

## Skinning
_style_ and _styleClass_ options apply to the displayed input element. As skinning style classes are
global, see the main theming section for more information.

