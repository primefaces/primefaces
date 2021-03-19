# Chronoline

Chronoline visualizes a series of chained events.

## Info

| Name | Value |
| --- | --- |
| Tag | Chronoline
| Component Class | org.primefaces.component.chronoline.Chronoline
| Component Type | org.primefaces.component.Chronoline
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ChronolineRenderer
| Renderer Class | org.primefaces.component.chronoline.ChronolineRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| value | null | Object | List of items to display.
| var | null | String | Name of the iterator to display an item.
| align | left | String | Position of the chronoline bar relative to the content. Valid values are "left", "right for vertical layout and "top", "bottom" for horizontal layout and "alternate" for both.
| layout | vertical | String | Orientation of the chronoline, valid values are "vertical" and "horizontal".
| style | null | String | Style of the chronoline.
| styleClass | null | String | StyleClass of the chronoline.

## Getting Started
Chronoline receives the events with the value property as a list of objects. Example below is a sample events object that is used throughout the documentation.

```java
public class ChronolineView {
    private List<Event> events;
    private List<String> events2;
    public ChronolineView() {
        events = new ArrayList<>();
        events.add(new Event("Ordered", "15/10/2020 10:30", "pi pi-shopping-cart", "#9C27B0", "game-controller.jpg"));
        events.add(new Event("Processing", "15/10/2020 14:00", "pi pi-cog", "#673AB7"));
        events.add(new Event("Shipped", "15/10/2020 16:15", "pi pi-shopping-cart", "#FF9800"));
        events.add(new Event("Delivered", "16/10/2020 10:00", "pi pi-check", "#607D8B"));
        events2 = new ArrayList<>();
        events2.add("2020");
        events2.add("2021");
        events2.add("2022");
        events2.add("2023");
    }
    
    //getter&setter for events
    public static class Event {
        String status;
        String date;
        String icon;
        String color;
        String image;
        
        public Event(String status, String date, String icon, String color) {
            this.status = status;
            this.date = date;
            this.icon = icon;
            this.color = color;
        }
        public Event(String status, String date, String icon, String color, String image) {
            this.status = status;
            this.date = date;
            this.icon = icon;
            this.color = color;
            this.image = image;
        }
        //getters & setters
    }
}
```

```xhtml
<p:chronoline value="#{chronolineView.events}" var="event">
    #{event.status}
</p:chronoline>
```

## Layout
Default ```layout``` of the chronoline is "vertical", setting ```layout``` to "horizontal" displays the items horizontally.

```xhtml
<p:chronoline value="#{chronolineView.events}" var="event" layout="horizontal">
    #{event.status}
</p:chronoline>
```

## Alignment
Location of the chronoline bar is defined using the ```align``` property.

```xhtml
<p:chronoline value="#{chronolineView.events}" var="event" align="right">
    #{event.status}
</p:chronoline>
```

In addition, the "alternate" alignment option make the contents take turns around the chronoline bar.

```xhtml
<p:chronoline value="#{chronolineView.events}" var="event" align="alternate">
    #{event.status}
</p:chronoline>
```

## Opposite
Content to be placed at the other side of the bar is defined with the ```opposite``` facet.

```xhtml
<p:chronoline value="#{chronolineView.events}" var="event">
    #{event.status}
    <f:facet name="opposite">
        <small class="text-secondary">#{event.date}</small>
    </f:facet>
</p:chronoline>
```

## Custom Markers
```marker``` facet allows placing a custom event marker instead of the default one. Below is an example with custom markers and content.
```xhtml
<p:chronoline value="#{chronolineView.events}" var="event" align="alternate" class="customized-chronoline">
    <p:card>
        <f:facet name="title">
            #{event.status}
        </f:facet>
        <f:facet name="subtitle">
            #{event.date}
        </f:facet>
        <p:graphicImage rendered="#{not empty event.image}" width="200" styleClass="p-shadow-2"
                        value="../../resources/demo/images/product/#{event.image}" alt="#{event.image}"/>
        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Inventore sed consequuntur error repudiandae numquam deserunt
            quisquam repellat libero asperiores earum nam nobis, culpa ratione quam perferendis esse, cupiditate neque quas!</p>
        <p:button value="Read more" styleClass="text-button"/>
    </p:card>
    <f:facet name="marker">
        <span class="custom-marker p-shadow-2" style="background-color: #{event.color}">
            <i class="#{event.icon}"/>
        </span>
    </f:facet>
</p:chronoline>
```

## Skinning of Chronoline
Chronoline resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
| ui-chronoline | Container element.
| ui-chronoline-left | Container element when alignment is left.
| ui-chronoline-right | Container element when alignment is right.
| ui-chronoline-top | Container element when alignment is top.
| ui-chronoline-bottom | Container element when alignment is bottom.
| ui-chronoline-alternate | Container element when alignment is alternating.
| ui-chronoline-vertical | Container element of a vertical timeline.
| ui-chronoline-horizontal | Container element of a horizontal timeline.
| ui-chronoline-event | Event element.
| ui-chronoline-event-opposite | Opposite of an event content.
| ui-chronoline-event-content | Event content.
| ui-chronoline-event-separator | Separator element of an event.
| ui-chronoline-event-marker | Marker element of an event.
| ui-chronoline-event-connector | Connector element of an event.