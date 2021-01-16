# Client Side Validation - JavaScript API

## PrimeFaces.validation.validate

This is the main method used by PrimeFaces commands like `p:commandButton` when `validateClient` is activated.

```js
/**
 * Triggers client-side-validation of single or multiple containers (complex validation or simple inputs).
 * @function
 * @param {string | HTMLElement | JQuery} process The elements to be processed.
 * @param {string | HTMLElement | JQuery} update The elements to be updated.
 * @param {boolean} highlight If invalid elements should be highlighted.
 * @param {boolean} focus If the first invalid element should be focused.
 * @param {boolean} renderMessages If messages should be rendered.
 * @param {boolean} validateInvisibleElements If invisible elements should be validated.
 * @return {boolean} `true` if the request would not result in validation errors, or `false` otherwise.
 */
validate : function(process, update, highlight, focus, renderMessages, validateInvisibleElements) {
    ...
}
```

To validate a form or container, you can simply use:
```js
PrimeFaces.validation.validate($('#myForm'), $('#myForm'), true, true, true, false);
```

## PrimeFaces.validation.validateInstant

This is the main method used by `p:clientBehavior` which only validates a single input.

```js
/**
 * Performs a client-side validation of the given element. The context of this validation is a single field only.
 * If the element is valid, removes old messages from the element.
 * If the value of the element is invalid, adds the appropriate validation failure messages.
 * This is used by `p:clientValidator`.
 * @function
 * @param {string | HTMLElement | JQuery} el The ID of an element to validate, or the element itself.
 * @return {boolean} `true` if the element is valid, or `false` otherwise.
 */
validateInstant : function(el) {
    ...
}
```

Example:
```js
PrimeFaces.validation.validateInstant($('#myInput'));
```