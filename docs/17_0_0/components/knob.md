# Knob

Knob is an input component to insert numeric values in a range.

## Getting Started with Knob
Knob is used as an input component with a value.

```xhtml
<p:knob value="#{bean.propertyName}" />
```
## Boundaries and Step
Boundaries and step can be customized using min, max and step properties.

```xhtml
<p:knob value="#{bean.propertyName}" min="100" max="1000" step="50"/>
```

## Label
Label at the center is visible by default and can be hidden by setting showLabel to false. In addition
it can be customized using labelTemplate option.

```xhtml
<p:knob value="#{bean.propertyName}" labelTemplate="{value}%"/>
```
## Styling
The knob is an SVG styled with the `--p-knob-*` tokens of the theme: the value arc uses the primary color, the range
arc the border color. The tokens can be overridden for a single knob with `style` or a style class, the
`foregroundColor` and `backgroundColor` attributes are shortcuts for the two arc colors (a CSS color or a
`java.awt.Color`).

```xhtml
<p:knob value="25" foregroundColor="red" backgroundColor="#e0e0e0"/>
<p:knob value="45" style="--p-knob-value-background: var(--p-orange-500); --p-knob-text-color: var(--p-orange-500)"/>
```

| Token | Description |
| --- | --- |
| `--p-knob-value-background` | Color of the value arc (`foregroundColor`). |
| `--p-knob-range-background` | Color of the range arc (`backgroundColor`). |
| `--p-knob-text-color` | Color of the label. |
| `--p-knob-text-font-weight` | Font weight of the label. |
| `--p-knob-focus-ring-*` | Focus ring of the knob (width, style, color, offset, shadow). |

The size is set with `width` / `height` in pixels or as CSS length (e.g. `50%`); if only one is set, the knob is a
square. The default size is 200px.
## Client Side API
Widget: _PrimeFaces.widget.Knob_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
setValue(value) | value | void | Updates the value with given value
getValue() | - | number | Returns the current knob value.
increment() | - | void | Increments current value by step factor.
decrement() | - | void | Decrements current value by step factor.
disable() | - | void | Disables the knob
enable() | - | void | Enables the knob

## Keyboard Support

| Key | Function |
| --- | --- |
| `arrow up` / `arrow right` | Increments the value by the step. |
| `arrow down` / `arrow left` | Decrements the value by the step. |
| `page up` / `page down` | Increments / decrements the value by a tenth of the range. |
| `home` / `end` | Sets the minimum / maximum value. |

