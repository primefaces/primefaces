# Client-Side Validation - Messages

Validation errors are displayed the same way as in server-side validation: message text is taken from a client-side bundle, and message components are required to show them.

## Rendering

PrimeFaces message components **p:message**, **p:messages**, and **p:growl** have client-side renderers for CSV. Options such as _showSummary_, _showDetail_, _globalOnly_, and _mode_ are supported by the client-side renderer for compatibility.

## I18N

The default language for CSV messages is English. For other languages or to customize messages, the PrimeFaces Locales bundle must be present on the page. For more information, see [PrimeFaces Locales](https://github.com/primefaces/primefaces/wiki/Locales).

## MyFaces vs Mojarra

Bean Validation message format differs slightly between Faces implementations: Mojarra does not include the field label in the message; MyFaces does.

Mojarra format (message only):
```properties
jakarta.faces.validator.BeanValidator.MESSAGE={0}
```

MyFaces format (label and message):
```properties
jakarta.faces.validator.BeanValidator.MESSAGE={1}: {0}
```

PrimeFaces CSV messages follow the MyFaces convention by default. To show only the message without the label, override the format to use `{0}` only. To include the label, use `{1}: {0}`.

Override in JavaScript (e.g. in a locale):
```js
// With label (default)
PrimeFaces.locales['en_US'].messages['jakarta.faces.validator.BeanValidator.MESSAGE'] = '{1}: {0}';

// Without label
PrimeFaces.locales['en_US'].messages['jakarta.faces.validator.BeanValidator.MESSAGE'] = '{0}';
```

---

## Configuration

Use the steps below to enable client-side validation and to control how validation messages are resolved (Faces message bundle, Bean Validation, and CSV).

### 1. Enable client-side validation and message interpolation

Add these context parameters to `web.xml` so validation runs in the browser and Bean Validation message placeholders are interpolated on the client:

```xml
<context-param>
    <param-name>primefaces.CLIENT_SIDE_VALIDATION</param-name>
    <param-value>true</param-value>
</context-param>
<context-param>
    <param-name>primefaces.INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES</param-name>
    <param-value>true</param-value>
</context-param>
```

- **`primefaces.CLIENT_SIDE_VALIDATION`** — Turns on client-side validation so constraints are checked in the browser before submit.
- **`primefaces.INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES`** — Interpolates messages from your validation message bundles in client-side messages, so users see the same text as on the server.

### 2. Register a custom message bundle in `faces-config.xml`

Declare your application message bundle so Faces uses your custom properties file. The value is the bundle base name; the corresponding file must be on the classpath (e.g. under `src/main/resources` with the same package path).

Example: base name `org.acme.ui.Messages` → file at `src/main/resources/org/acme/ui/Messages.properties`.

```xml
<faces-config xmlns="http://xmlns.jcp.org/xml/ns/javaee"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                                  http://xmlns.jcp.org/xml/ns/javaee/web-facesconfig_4_0.xsd"
              version="4.0">
    <application>
        <message-bundle>org.acme.ui.Messages</message-bundle>
    </application>
</faces-config>
```

### 3. Override the BeanValidator message in `Messages.properties`

To show only the validation message without the field label (Mojarra-style), override the BeanValidator message key in your custom bundle. Here `{0}` is the message and `{1}` is the component label; omit `{1}` to hide the label.

In your bundle file (e.g. `src/main/resources/org/acme/ui/Messages.properties`):

```properties
jakarta.faces.validator.BeanValidator.MESSAGE={0}
```

Use `{1}: {0}` if you want the label included (MyFaces default).

### 4. Override Jakarta Bean Validation messages

Jakarta Bean Validation (e.g. `@NotNull`, `@Size`) uses its own resource bundle. To customize those messages, add `ValidationMessages.properties` in `src/main/resources` (and optionally locale variants such as `ValidationMessages_de.properties`). The Bean Validation provider loads this file; it applies to both server-side and client-side validation when messages are interpolated.

Example — `src/main/resources/ValidationMessages.properties`:

```properties
jakarta.validation.constraints.NotNull.message = This field is required.
jakarta.validation.constraints.Size.message = Size must be between {min} and {max}.
```

Keep the file at the classpath root so the resource name remains `ValidationMessages`.
