# Spinner

Spinner is an input component to provide a numerical input via increment and decrement buttons.

## Getting Started with Spinner
Spinner is an input component and used just like a standard input text.

```java
public class SpinnerBean {
    private int number;
    //getter and setter
}
```
```xhtml
<p:spinner value="#{spinnerBean.number}" />
```

## Step Factor
Other than integers, spinner also support decimals so the fractional part can be controller with
spinner as well. For decimals use the stepFactor attribute to specify stepping amount. Following
example uses a stepFactor 0.25.

```xhtml
<p:spinner value="#{spinnerBean.number}" stepFactor="0.25"/>
```
```java
public class SpinnerBean {
    private double number;
    //getter and setter
}
```
Output of this spinner would be;

After an increment happens a couple of times.

## Prefix and Suffix
Prefix and Suffix options enable placing fixed strings on input field. Note that you would need to
use a converter to avoid conversion errors since prefix/suffix will also be posted.

```xhtml
<p:spinner value="#{spinnerBean.number}" prefix="$" />
```
## Boundaries
In order to restrict the boundary values, use _min_ and _max_ options.

```xhtml
<p:spinner value="#{spinnerBean.number}" min="0" max="100"/>
```
## Ajax Spinner
Spinner can be ajaxified using client behaviors like f:ajax or p:ajax. In example below, an ajax
request is done to update the outputtext with new value whenever a spinner button is clicked.

```xhtml
<p:spinner value="#{spinnerBean.number}">
    <p:ajax update="display" />
</p:spinner>
<h:outputText id="display" value="#{spinnerBean.number}" />
```

## Icons
The button icons are icon tokens of the theme (see [Theming](/core/theming.md?id=icons)): `--p-inputnumber-increment-icon`
and `--p-inputnumber-decrement-icon` for the default `stacked` layout, `--p-icon-plus` and `--p-icon-minus` for the
`horizontal`, `horizontal-after` and `vertical` layouts. Set them in `:root, [data-p-scope]` for all spinners or on a style class for
some:

```css
.custom-icons-spinner {
    --p-inputnumber-increment-icon: var(--p-icon-plus);
    --p-inputnumber-decrement-icon: var(--p-icon-minus);
}
```

They are rendered as `<span class="p-icon p-up-icon p-spinner-increment-icon p-c">` and
`<span class="p-icon p-down-icon p-spinner-decrement-icon p-c">` (`p-plus-icon` / `p-minus-icon` for the other
layouts); the former `upIcon` / `downIcon` attributes are gone.

## Client Side API
Widget: _PrimeFaces.widget.Spinner_


| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| spin(dir) | integer | void | Spin up or down based on dir value 1 or -1 |
| getValue() | void | void | Gets the current value of the spinner |
| setValue(val) | number | void | Sets the current value of the spinner |
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |

## Skinning
Spinner resides in a container element that using `style` and `styleClass` applies. Following is the list of
structural style classes:

| Class | Applies |
| --- | --- |
.p-spinner | Main container element of spinner
.p-spinner-input | Input field
.p-spinner-button | Spinner buttons
.p-spinner-up | Increment button
.p-spinner-down | Decrement button

### Text alignment
The PrimeFlex 3.0.0 classes `text-left`, `text-center` and `text-right` are supported to align the text of the Spinner input field.

As skinning style classes are global, see the main theming section for more information.
