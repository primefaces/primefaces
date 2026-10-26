This patch replaces deprecated jQuery features with their modern equivalent:

* `$.type` => `typeof` and `Array.isArray`
* `$.isArray` => `Array.isArray`
* `$.isFunction` => `typeof`
* `$(...).submit` => `$(...).trigger("submit")`

It also removes the `$.cleanData` override of the vendored jQuery UI widget factory: PrimeFaces triggers the `remove`
event in `$.cleanData` already (`packages/core/src/jquery-extensions/jquery.remove-event.ts`), a second override
triggered it twice.
