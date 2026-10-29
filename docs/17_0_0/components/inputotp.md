# InputOtp

InputOtp is an input for one-time passwords and PINs with one input per character.

## Getting Started with InputOtp
The value is the concatenation of the entered characters, a String. `length` defines the number of characters
(default 4).

```xhtml
<p:inputOtp value="#{bean.code}" length="6"/>
```

Typing a character moves to the next input, a pasted code is distributed over the inputs starting at the focused one
(a complete code starts at the first input). The characters always fill the first inputs: typing into an input after
an empty one fills the empty one, deleting a character moves the following ones to the front.
The first input has `autocomplete="one-time-code"`, so mobile browsers offer a code received by SMS.

## Integer Only and Mask
`integerOnly` accepts digits only and shows the numeric keyboard on mobile devices, `mask` hides the characters like a
password. Like `p:password`, a masked value is not rendered (it would be readable in the page source), the inputs
are empty also after a validation error; `redisplay="true"` renders it.

```xhtml
<p:inputOtp value="#{bean.pin}" integerOnly="true" mask="true"/>
```

## Separator
A text between the inputs is defined with `separator`, any content (e.g. an icon) with the `separator` facet.

```xhtml
<p:inputOtp length="6" separator="-"/>

<p:inputOtp length="6">
    <f:facet name="separator">
        <i class="pi pi-minus"/>
    </f:facet>
</p:inputOtp>
```

## Placeholder
`placeholder` is either one character for all inputs or one character per input.

```xhtml
<p:inputOtp placeholder="0"/>
<p:inputOtp length="5" placeholder="ABCDE"/>
```

## Validation
A value that is not empty must have `length` characters (and only digits with `integerOnly`), otherwise the component
is invalid and its validators are not called, they only get complete codes. `required` makes an empty value invalid.
`validatorMessage` replaces the message of a wrong length, `converterMessage` the message of a character that is not a
digit (`integerOnly`).

With [client side validation](/core/csv.md) the length and `required` are validated in the browser, as well as the
validators of the component.

```xhtml
<p:inputOtp value="#{bean.code}" length="6" integerOnly="true" required="true"/>
<p:commandButton value="Verify" validateClient="true"/>
```

## Migration from PrimeFaces Extensions
`pe:inputOtp` becomes `p:inputOtp` with the same attributes (`length`, `integerOnly`, `mask`, `separator` and the
`separator` facet, `placeholder`, `ariaLabel`, `inputStyle`, `inputStyleClass`, `autocomplete`). The widget variable is
`PrimeFaces.widget.InputOtp`, `p:ajax` uses the events `change` (the focus leaves the component) and `complete` instead
of the DOM events of each input.

## Events
| Event | Listener Parameter | Fired |
| --- | --- | --- |
| change | jakarta.faces.event.AjaxBehaviorEvent | When the focus leaves the component and the value was changed (default). |
| complete | jakarta.faces.event.AjaxBehaviorEvent | When all characters are entered. |

```xhtml
<p:inputOtp value="#{bean.code}" length="6" integerOnly="true">
    <p:ajax event="complete" listener="#{bean.verify}"/>
</p:inputOtp>
```

## Styling
The inputs are input fields (`.p-inputfield`) styled like `p:inputText`. `inputStyleClass="p-inputtext-sm"` /
`p-inputtext-lg` makes them smaller / larger.

| Token | Description |
| --- | --- |
| `--p-inputotp-gap` | Space between the inputs. |
| `--p-inputotp-input-width` | Width of an input (`-sm-width` / `-lg-width` for the sizes). |
| `--p-inputotp-separator-color` | Color of the separator. |

## Accessibility
The component is a `group`, a label (`p:outputLabel for`) references the first input and labels the group. Each input
gets an `aria-label` from the `otpLabel` of the locale (`{0}` is the position), `ariaLabel` replaces it.

## Keyboard Support

| Key | Function |
| --- | --- |
| `arrow left` / `arrow right` | Moves to the previous / next input. |
| `home` / `end` | Moves to the first input / the first empty input (the last input of a complete code). |
| `backspace` | Deletes the character, in an empty input deletes the previous character and moves to it. |
| `delete` | Deletes the character. |

## Client Side API
Widget: _PrimeFaces.widget.InputOtp_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| getValue() | - | string | Returns the current value. |
| setValue(value) | value: string | void | Sets the value, without events. |
| clear() | - | void | Clears all inputs, without events. |
| focus() | - | void | Focuses the first empty input. |
| enable() | - | void | Enables the component. |
| disable() | - | void | Disables the component. |
