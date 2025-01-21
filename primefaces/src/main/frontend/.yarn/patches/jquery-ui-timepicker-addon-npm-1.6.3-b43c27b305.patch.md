This patch

* updates some deprecated JQuery feature to their modern equivalent,
  such as `$.isFunction` -> `typeof x === "function"`.
* Replaces `$.trim` with `PrimeFaces.trim`
* Uses `PrimeFaces.escapeHTML` when building HTML string from user input
* Fixes PrimeFaces github issue #1421,
  see also https://github.com/trentrichardson/jQuery-Timepicker-Addon/issues/848
