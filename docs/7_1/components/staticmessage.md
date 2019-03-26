# StaticMessage

StaticMessage component can be used to display a message without the use of a FacesMessages. 

## Info

| Name | Value |
| --- | --- |
| Tag | inputText
| Component Class | org.primefaces.component.staticmessage.StaticMessage
| Component Type | org.primefaces.component.StaticMessage
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.StaticMessageRenderer
| Renderer Class | org.primefaces.component.staticmessage.StaticMessageRender

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
summary | null | String | The message summary.
detail | null | String | The message detail.
severity | null | String | The severity of the message: error, info, warn, fatal.
escape | true | Boolean | Defines whether html would be escaped or not.
style | null | String | Style of main container element.
styleClass | null | String | Style class of main container.


## Getting Started with StaticMessage

```xhtml
<p:staticMessage severity="info" summary="INFO!" detail="Hey, i'm a info!" />
```

