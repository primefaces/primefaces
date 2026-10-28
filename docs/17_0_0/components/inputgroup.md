# InputGroup

InputGroup is a CSS utility to group forms with icons and buttons.

## Getting Started with InputGroup
InputGroup contains an input element and one or more addons such as icons, text and buttons.
Following are examples of input elements with an icon and a text.

```xhtml
<div class="p-inputgroup">
    <span class="p-inputgroup-addon"><i class="fa fa-user"></i></span>
    <p:inputText placeholder="Username" />
</div>
```
```xhtml
<div class="p-inputgroup">
    <span class="p-inputgroup-addon">$</span>
    <p:inputText placeholder="Price" />
</div>
```

## Multiple Add-Ons
An inputgroup can contain multiple add-ons as well.

```xhtml
<div class="p-inputgroup">
    <span class="p-inputgroup-addon"><i class="fa fa-credit-card"></i></span>
    <span class="p-inputgroup-addon"><i class="fa fa-cc-visa"></i></span>
    <p:inputText placeholder="Price" />
    <span class="p-inputgroup-addon">$</span>
    <span class="p-inputgroup-addon">.00</span>
</div>
```
## Buttons

```xhtml
<div class="p-inputgroup">
    <p:commandButton value="Search"/>
    <p:inputText placeholder="Keyword" />
</div>
<div class="p-inputgroup">
    <p:commandButton icon="fa fa-check" styleClass="green-button"/>
    <p:inputText placeholder="Vote" />
    <p:commandButton icon="fa fa-close" styleClass="red-button"/>
</div>
```
## Checkboxes

Style class of the addon that contains a checkbox should be _p-inputgroup-addon-checkbox_.

```xhtml
<div class="p-inputgroup">
    <span class="p-inputgroup-addon-checkbox">
        <p:selectBooleanCheckbox />
    </span>
    <p:inputText placeholder="Username" />
</div>
```
