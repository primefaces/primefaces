# Confirm

Confirm is a behavior element used to integrate with global confirm dialog/popup.

## Info

| Name | Value |
| --- | --- |
| Tag | confirm
| Behavior Id | org.primefaces.behavior.ConfirmBehavior

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| source | dialog | String | Source of confirm dialog/popup. Valid values are the clientId of the source component and "this".
| type | dialog | String | Type of confirm dialog/popup.
| header | null | String | Header of confirm dialog.
| message | null | String | Message to display in confirm dialog/popup.
| icon | null | String | Icon to display next to message.
| disabled | false | Boolean | Disables confirm behavior when true.
| escape | true | Boolean | Whether to escape the message.
| beforeShow | null | String | Callback to execute before displaying confirmation dialog. Return false to prevent dialog from appearing.

## Getting started with Confirm
See global confirm dialog/popup topic in next section for details.