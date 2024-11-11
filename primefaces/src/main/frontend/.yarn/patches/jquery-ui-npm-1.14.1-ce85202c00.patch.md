This patch fixes a focusing issue with the datepicker, when used
in combination with the `inputmask` library. The JQuery datepicker
would trigger a focus event on every `keyup`, triggering a focusing
event. This makes `inputmask` think that the user just entered the
input field, and it consequently saves the new value as the initial
value. Then, when the user leaves the input field and the browser
fires a blur event, `inputmask` does not see any changes and does
not trigger a `change` event. Finally, the result of this missing
change event is that no AJAX behavior is triggered either, i.e.
`<p:ajax event="change">` won't work.