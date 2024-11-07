This patch replaces deprecated jQuery features with their modern equivalent:

* `$.type` => `typeof` and `Array.isArray`
* `$.isArray` => `Array.isArray`
* `$.isFunction` => `typeof`
* `$(...).submit` => `$(...).trigger("submit")`