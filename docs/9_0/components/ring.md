# Ring

Ring is a data display component with a circular animation.

## Info

| Name | Value |
| --- | --- |
| Tag | ring
| Component Class | org.primefaces.component.ring.Ring
| Component Type | org.primefaces.component.Ring
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.RingRenderer
| Renderer Class | org.primefaces.component.ring.RingRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
value | null | Object | Collection to display.
var | null | String | Name of the data iterator.
style | null | String | Inline style of the container element.
styleClass | null | String | Style class of the container element.
easing | swing | String | Type of easing to use in animation.
autoplay | false | Boolean | When true, Ring will automatically advance the moving elements to the next child at a regular interval.
autoplayDuration | 1000 | Integer | Time in milliseconds between animation triggers when a Ring's autoplay is playing.
autoplayPauseOnHover | false | Boolean | When true, Ring will pause autoPlay when the user mouseover the Ring container.
autoplayInitialDelay | 0 | Integer | Time in milliseconds to delay the start of Ring's configured autoplay option.

## Getting started with Ring
A collection is required to use the Ring component.

```xhtml
<p:ring value="#{ringBean.players}" var="player”>
   <p:graphicImage value="/images/barca/#{player.photo}"/>
</p:ring>
```
```java
public class RingBean {
   private List<Player> players;

   public RingBean() {
      players = new ArrayList<Player>();
      players.add(new Player("Messi", 10, "messi.jpg", "CF"));
      players.add(new Player("Iniesta", 8, "iniesta.jpg", "CM"));
      //more players
   }
   //getter&setter for players
}
```
## Item Selection
A column is required to process item selection from ring properly.

```xhtml
<p:ring value="#{ringBean.players}" var="player">
   <p:column>
      //UI to select an item e.g. commandLink
   </p:column>
</p:ring>
```
## Easing
Following is the list of available options for easing animation.

- swing
- easeInQuad
- easeOutQuad
- easeInOutQuad
- easeInCubic
- easeOutCubic
- easeInOutCubic
    - easeInQuart
    - easeOutQuart
    - easeInOutQuart
    - easeInQuint
    - easeOutQuint
    - easeInOutQuint
    - easeInSine
       - easeOutSine
       - easeInExpo
       - easeOutExpo
       - easeInOutExpo
       - easeInCirc
       - easeOutCirc
       - easeInOutCirc
          - easeInElastic
          - easeOutElastic
          - easeInOutElastic
          - easeInBack
          - easeOutBack
          - easeInOutBack
             - easeInBounce
             - easeOutBounce
             - easeInOutBounce


## Skinning
Ring resides in a main container which _style_ and _styleClass_ attributes apply. Following is the list of
structural style classes.

| Class | Applies | 
| --- | --- | 
.ui-ring | Main container element.
.ui-ring-item | Each item in the list.
