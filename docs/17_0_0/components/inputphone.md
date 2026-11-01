# InputPhone

InputPhone is an input for international phone numbers with a country selector. Formatting and validation are based on
[libphonenumber-js](https://gitlab.com/catamphetamine/libphonenumber-js) (the metadata of Google's libphonenumber).

## Getting Started with InputPhone
The value is a String with the number in the E.164 format, e.g. `+4930123456`.

```xhtml
<p:inputPhone value="#{bean.phone}"/>
```

The user selects the country in a panel with a filter (name, code or calling code), or enters the number with its
calling code (e.g. `+44 20 ...`): the country follows the number. The number is formatted while typing
(`formatAsYouType`) and completely when the input loses the focus.

## Countries
For an empty value the country of the view locale is selected (US if the locale has none), `initialCountry` sets
another one. A value selects the country of the number, a value without calling code (e.g. stored before as
`030 123456`) is a number of the initial country. The lists of countries are ISO 3166-1 alpha-2 codes, a comma
separated String or a Collection. `onlyCountries` and `excludeCountries` also restrict the numbers: a number of another
country is invalid.

```xhtml
<p:inputPhone value="#{bean.phone}" initialCountry="CH" onlyCountries="DE,AT,CH" preferredCountries="CH"/>
<p:inputPhone value="#{bean.phone}" excludeCountries="#{bean.blockedCountries}"/>
```

`initialCountry="auto"` looks up the country of the user with `geoIpLookup`, the body of a
`function(success, failure)` that calls `success` with the country code (until then the country of the view locale is
shown; a number entered or a country selected meanwhile is kept):

```xhtml
<p:inputPhone value="#{bean.phone}" initialCountry="auto"
              geoIpLookup="fetch('https://ipapi.co/json').then(r => r.json()).then(d => success(d.country_code)).catch(failure);"/>
```

The names of the countries are the names of the browser in the language of the page, `localizedCountries` (a
`Map<String, String>` of code to name) replaces them. `allowDropdown="false"` shows the country without letting the
user change it (a number with another calling code still selects its country).

## Format
| Attribute | Default | Description |
| --- | --- | --- |
| nationalMode | true | Numbers of the selected country in the national format (`030 123456`), otherwise all numbers in the international format (`+49 30 123456`). |
| separateDialCode | true | The calling code is shown next to the code of the country in the selector. |
| autoHideDialCode | true | In the international format: the calling code is filled in when the empty input gets the focus and removed again if nothing else is entered. |
| formatAsYouType | true | Formats the number while typing (at the end of the number) and completely when the input loses the focus, otherwise it is shown as entered. |
| formatOnDisplay | true | Formats the value when it is displayed, otherwise it is shown as it is. |
| autoPlaceholder | polite | An example mobile number of the selected country as placeholder: `polite` if `placeholder` is not set, `aggressive` always, `off` never. |
| fixDropdownWidth | true | The panel has the width of the component, otherwise it grows with the longest name. |

A valid number is always submitted in the E.164 format.

## Validation
A number that is valid for its country (and allowed by `onlyCountries` / `excludeCountries`) is submitted in the E.164
format, any other input as entered. The server
rejects a value that is not empty and not an E.164 number. If Google's libphonenumber is on the classpath, the server
also checks that the number is valid for its country and that the country is allowed by `onlyCountries` /
`excludeCountries` (`primefaces.inputphone.INVALID`, or `validatorMessage`); without it the server checks the format
only:

```xml
<dependency>
    <groupId>com.googlecode.libphonenumber</groupId>
    <artifactId>libphonenumber</artifactId>
    <version>9.0.38</version>
</dependency>
```

With [client side validation](/core/csv.md) the number is validated in the browser with the same rules (the full
metadata of libphonenumber-js), `required` and the validators of the component are supported.

```xhtml
<p:inputPhone value="#{bean.phone}" required="true"/>
<p:commandButton value="Save" validateClient="true"/>
```

## Events
In addition to the DOM events of the input (`change`, `blur`, `keyup`, ...):

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| countrySelect | org.primefaces.event.SelectEvent&lt;String&gt; | When the user selects a country, `getObject()` is the ISO code. The number is not submitted (and not validated) with this event. |

```xhtml
<p:inputPhone value="#{bean.phone}">
    <p:ajax event="countrySelect" listener="#{bean.onCountrySelect}"/>
</p:inputPhone>
```

```java
public void onCountrySelect(SelectEvent<String> event) {
    String country = event.getObject(); // e.g. "DE"
}
```

## Styling
The component is one input field: it has the border, background and focus ring of `p:inputText` (`--p-inputtext-*`
tokens), the country selector and the input are parts of it. The focus ring is shown while the selector or the input
has the focus and while the panel is open. `inputStyleClass="p-inputtext-sm"` / `p-inputtext-lg` changes the size,
`p-input-filled` (on the component or a parent) the filled variant. The panel is styled like the panel of
`p:selectOneMenu` (`--p-select-*` tokens).

| Token | Description |
| --- | --- |
| `--p-inputphone-country-background` | Background of the country selector (`-hover-background` on hover, keyboard focus and while the panel is open). |
| `--p-inputphone-country-color` | Text color of the country selector. |
| `--p-inputphone-country-padding-x` | Horizontal padding of the country selector. |
| `--p-inputphone-country-gap` | Space between code, calling code and icon. |
| `--p-inputphone-separator-color` | Line between the country selector and the input. |
| `--p-inputphone-dial-code-color` | Color of the calling codes. |
| `--p-inputphone-dropdown-color` / `--p-inputphone-dropdown-icon` | Color and icon of the dropdown icon. |
| `--p-inputphone-filter-icon` | Icon of the filter. |
| `--p-inputphone-filter-gap` | Space below the filter, before the list. |
| `--p-inputphone-list-max-height` | Maximum height of the list of countries. |

## Keyboard Support

| Key | Function |
| --- | --- |
| `arrow down` / `arrow up` (country selector) | Opens the panel. |
| `arrow down` / `arrow up` (filter) | Moves the highlight. |
| `enter` | Selects the highlighted country. |
| `escape` | Closes the panel. |
| `tab` (filter) | Closes the panel and moves to the number. |

## Client Side API
Widget: _PrimeFaces.widget.InputPhone_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| getValue() / getNumber() | - | string | Returns the number in the E.164 format (as entered if it is not valid). |
| setValue(value) / setNumber(value) | value: string | void | Sets the number (E.164 or national format of the selected country). |
| getCountry() | - | string | Returns the ISO code of the selected country. |
| setCountry(code) | code: string | void | Selects a country like the user. |
| isValid() | - | boolean | Whether the number is valid for its country and the country is allowed (`onlyCountries` / `excludeCountries`). |
| isPossible() | - | boolean | Whether the number has a possible length for its country. |
| getValidationError() | - | string | Why the length is not possible (`TOO_SHORT`, `TOO_LONG`, ...), `undefined` if it is. |
| show() / hide() | - | void | Shows / hides the panel. |
| focus() | - | void | Focuses the number. |
| enable() | - | void | Enables the component. |
| disable() | - | void | Disables the component. |

## Migration from PrimeFaces Extensions
`pe:inputPhone` (intl-tel-input) becomes `p:inputPhone`, most attributes keep their name: `initialCountry` (also
`auto` with `geoIpLookup`), `onlyCountries`, `excludeCountries`, `preferredCountries`, `localizedCountries`,
`nationalMode`, `allowDropdown`, `separateDialCode`, `autoHideDialCode`, `autoPlaceholder`, `formatOnDisplay`,
`formatAsYouType`, `fixDropdownWidth`, `inputStyle`, `inputStyleClass` and the `countrySelect` event (with the ISO code
as `SelectEvent<String>`). Differences:
- The value is the number in the E.164 format, the widget variable is `PrimeFaces.widget.InputPhone`.
- There are no flags, the selector shows the country code (and the calling code).
- `separateDialCode` defaults to `true`.
- `geoIpLookup` is the body of a `function(success, failure)` instead of a function.
- `placeholderNumberType` is not supported, the placeholder is an example mobile number.
