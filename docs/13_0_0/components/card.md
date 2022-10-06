# Card

Card is a flexible container component.

## Info

| Name | Value |
| --- | --- |
| Tag | Card
| Component Class | org.primefaces.component.card.Card
| Component Type | org.primefaces.component.Card
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.CardRenderer
| Renderer Class | org.primefaces.component.card.CardRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| style | null | String | Style of the card.
| styleClass | null | String | StyleClass of the card.
| header | null | String | Header text of the card.
| title | null | String | Title text of the card.
| subtitle | null | String | Subtitle text of the card.
| footer | null | String | Footer text of the card.

## Getting Started
Card provides `header`, `title`, `subtitle` and `footer` facets to place content, or you can also use corresponding attributes if only text is required.

```xhtml
<p:card>
    <f:facet name="header">
        <img alt="user header" src="../../resources/demo/images/usercard.png"/>
    </f:facet>
    <f:facet name="title">
        Advanced Card
    </f:facet>
    <f:facet name="subtitle">
        Card subtitle
    </f:facet>

    <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Inventore sed consequuntur error repudiandae numquam deserunt
        quisquam repellat libero asperiores earum nam nobis, culpa ratione quam perferendis esse, cupiditate neque quas!</p>

    <f:facet name="footer">
        <p:commandButton icon="pi pi-check" value="Save"/>
        <p:commandButton icon="pi pi-times" value="Cancel" class="ui-secondary-button" style="margin-left: .5em"/>
    </f:facet>
</p:card>
```

## Skinning of Card
Card resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
| .ui-card | Container element.
| .ui-card-title | Title element.
| .ui-card-subtitle | Subtitle element.
| .ui-card-content | Content of the card.
| .ui-card-footer | Footer of the card.
