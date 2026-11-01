# TriStateManyCheckbox

TriStateManyCheckbox is a group of checkboxes with three states each.

## Getting Started with TriStateManyCheckbox
The items are defined with `f:selectItem` / `f:selectItems` like `p:selectManyCheckbox`. The value is a `Map` of the
item values to their states: `"0"` (first state, unchecked), `"1"` (second state, checked) and `"2"` (third state,
crossed). An item that is not in the Map is in the first state. The submitted Map contains every item, in the order
of the items.

```xhtml
<p:triStateManyCheckbox value="#{bean.toppings}">
    <f:selectItem itemLabel="Cheese" itemValue="Cheese"/>
    <f:selectItem itemLabel="Olives" itemValue="Olives"/>
    <f:selectItem itemLabel="Onion" itemValue="Onion"/>
</p:triStateManyCheckbox>
```

```java
public class Bean {
    private Map<String, String> toppings = new LinkedHashMap<>(Map.of("Cheese", "1", "Onion", "2"));
    //getter-setter
}
```

The keys are the item values; the states in the model may also be numbers (e.g. `1`), the submitted ones are strings
unless a converter is set. Groups (`SelectItemGroup`) are not supported.

## Converter
A converter converts the states, not the keys: `getAsObject` gets `"0"`, `"1"` or `"2"`, `getAsString` must return
one of them.

```xhtml
<p:triStateManyCheckbox value="#{bean.preferences}" converter="#{bean.preferenceConverter}">
    <f:selectItems value="#{bean.toppings}"/>
</p:triStateManyCheckbox>
```

```java
public enum Preference { NEUTRAL, LIKE, DISLIKE }

private Map<String, Preference> preferences;

public Converter<Preference> getPreferenceConverter() {
    return new Converter<>() {
        public Preference getAsObject(FacesContext context, UIComponent component, String value) {
            return Preference.values()[Integer.parseInt(value)];
        }

        public String getAsString(FacesContext context, UIComponent component, Preference value) {
            return String.valueOf(value.ordinal());
        }
    };
}
```

## Layout
`layout="lineDirection"` (default) displays the items in a row, `layout="pageDirection"` one per line.

## Icons and Titles
The second and third state show the icon tokens of the theme (see [Theming](/core/theming.md?id=icons)),
`--p-checkbox-checked-icon` and `--p-checkbox-crossed-icon` like `p:triStateCheckbox`, the first state shows no icon.
`stateOneIcon`, `stateTwoIcon` and `stateThreeIcon` replace them with icons of the application (style classes, e.g.
`pi pi-heart`). `stateOneTitle`, `stateTwoTitle` and `stateThreeTitle` define the tooltip of each state.

```xhtml
<p:triStateManyCheckbox value="#{bean.toppings}" stateTwoIcon="pi pi-heart-fill" stateThreeIcon="pi pi-ban"
                        stateOneTitle="Don't care" stateTwoTitle="Love it" stateThreeTitle="Hate it">
    <f:selectItems value="#{bean.allToppings}"/>
</p:triStateManyCheckbox>
```

## Disabled and Readonly
`disabled` disables all items, `itemDisabled` of a select item one item; a disabled item keeps its state, a state
submitted for it is ignored. `readonly` shows the states but they can't be changed.

## Validation
`required` means that at least one item is not in the first state. The validators of the component (e.g.
`f:validateLongRange`) are called with the state of each item (converted, if there is a converter), bean validation
with the Map. `validatorMessage` replaces the message of a failed validator. The component is validated on the server
only, client side validation skips it.

```xhtml
<p:triStateManyCheckbox value="#{bean.ratings}" validatorMessage="State 0 is not allowed">
    <f:selectItems value="#{bean.items}"/>
    <f:validateLongRange minimum="1"/>
</p:triStateManyCheckbox>
```

## Events
| Event | Listener Parameter | Fired |
| --- | --- | --- |
| change | jakarta.faces.event.AjaxBehaviorEvent | When the state of an item was changed (default). |

```xhtml
<p:triStateManyCheckbox value="#{bean.toppings}">
    <f:selectItems value="#{bean.allToppings}"/>
    <p:ajax listener="#{bean.onChange}" update="msgs"/>
</p:triStateManyCheckbox>
```

## Migration from PrimeFaces Extensions
`pe:triStateManyCheckbox` becomes `p:triStateManyCheckbox` with the same attributes (`layout`, `stateOneIcon`,
`stateTwoIcon`, `stateThreeIcon`, `stateOneTitle`, `stateTwoTitle`, `stateThreeTitle`) and the same value (a Map of
the item values to the states, converted by the converter of the component). Differences:
- The widget is `PrimeFaces.widget.TriStateManyCheckbox` (was `PrimeFaces.widget.ExtTriStateManyCheckbox`).
- `p:ajax` supports only the event `change` (the default): `event="valueChange"` and DOM events like `click` or
  `focus` must become `change` or be removed.
- The markup changed: a `div` per item instead of a `table`, one input per item (named `clientId:index`) and the
  checkbox classes `p-chkbox`, `p-chkbox-box`, `p-chkbox-label` instead of `ui-*`. Custom CSS, JavaScript and
  Selenium tests that relied on the old markup need to be adjusted.
- The item values may be of any type (PFE expected strings).
- The default icons come from the theme, the `stateXIcon` attributes are only needed for other icons.
- `required` uses the required message (PFE used the invalid value message), `readonly` is supported.
- A disabled item keeps its state (PFE did not submit the component if an item was disabled).

## Styling
The boxes and labels are the ones of the checkbox (`.p-chkbox`, `.p-chkbox-box`, `.p-chkbox-icon`, `.p-chkbox-label`),
`styleClass="p-chkbox-sm"` / `p-chkbox-lg` makes them smaller / larger.

| Class | Applies |
| --- | --- |
| `.p-tristatemanycheckbox` | Main container element. |
| `.p-tristatemanycheckbox-vertical` | Main container with `layout="pageDirection"`. |

| Token | Description |
| --- | --- |
| `--p-tristatemanycheckbox-gap` | Space between the items in a row. |
| `--p-tristatemanycheckbox-row-gap` | Space between the lines. |

## Accessibility
The component is a `group`, each item has an input that gets the focus and is labelled by the label of the item. A
changed state is announced with its title or, without titles, the `nullLabel`, `trueLabel` and `falseLabel` of the
locale.

## Keyboard Support

| Key | Function |
| --- | --- |
| `tab` | Moves to the next item. |
| `space` / `enter` / `arrow up` / `arrow right` | Moves the item to the next state. |
| `arrow down` / `arrow left` | Moves the item to the previous state. |

## Client Side API
Widget: _PrimeFaces.widget.TriStateManyCheckbox_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| toggle(index, direction) | index: number, direction: 1 \| -1 (default 1) | void | Moves the item to the next / previous state, with events. |
| getState(index) | index: number | 0 \| 1 \| 2 | Returns the state of the item. |
| getStates() | - | (0 \| 1 \| 2)[] | Returns the state of each item. |
| setState(index, state) | index: number, state: 0 \| 1 \| 2 | void | Sets the state of the item, without events. |
| enable() | - | void | Enables the items, the ones disabled on the server (`disabled`, `itemDisabled`) stay disabled. |
| disable() | - | void | Disables the items. |
