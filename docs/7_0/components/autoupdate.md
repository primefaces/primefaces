# AutoUpdate

AutoUpdate is a tag handler to mark a component to be updated at every ajax request.

## Info

| Name | Value |
| --- | --- |
| Tag | autoUpdate
| Handler Class | org.primefaces.component.autoupdate.AutoUpdateTagHandler

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| disabled | false | Boolean | Whether the autoUpdate functionality is enabled.

## Getting Started with AutoUpdate
AutoUpdate is used by nesting inside a parent component.

```xhtml
<p:panel>
    <p:autoUpdate />
</p:panel>
```