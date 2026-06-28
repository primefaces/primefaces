# StaticMessage

StaticMessage component can be used to display a message without the use of a FacesMessages.

## Getting Started with StaticMessage

```xhtml
<p:staticMessage severity="info" summary="INFO!" detail="Hey, i'm a info!" />
```

## Display Mode
StaticMessage component has three different display modes;

- text : Only message text is displayed.
- icon : Only message severity is displayed and message text is visible as a tooltip.
- both (default) : Both icon and text are displayed.

## Ajax Behavior Events
StaticMessage component has one event: `close`.