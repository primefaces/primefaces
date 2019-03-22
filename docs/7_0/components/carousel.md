# Carousel

Carousel is a multi purpose component to display a set of data or general content with slide effects.

## Info

| Name | Value |
| --- | --- |
| Tag | carousel
| Component Class | org.primefaces.component.carousel.Carousel
| Component Type | org.primefaces.component.Carousel
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.CarouselRenderer
| Renderer Class | org.primefaces.component.carousel.CarouselRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| value | null | Object | A value expression that refers to a collection
| var | null | String | Name of the request scoped iterator
| numVisible | 3 | Integer | Number of visible items per page
| firstVisible | 0 | Integer | Index of the first element to be displayed
| widgetVar | null | String | Name of the client side widget.
| circular | false | Boolean | Sets continuous scrolling
| vertical | false | Boolean | Sets vertical scrolling
| autoPlayInterval | 0 | Integer | Sets the time in milliseconds to have Carousel start scrolling automatically after being initialized
| pageLinks | 3 | Integer | Defines the number of page links of paginator.
| effect | slide | String | Name of the animation, could be “fade” or “slide”.
| easing | easeInOutCirc | String | Name of the easing animation.
| effectDuration | 500 | Integer | Duration of the animation in milliseconds.
| dropdownTemplate | {page} | String | Template string for dropdown of paginator.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| itemStyle | null | String | Inline style of each item.
| itemStyleClass | null | String | Style class of each item.
| headerText | null | String | Label for header.
| footerText | null | String | Label for footer.
| responsive | false | Boolean | In responsive mode, carousel adjusts its content based on screen size.
| breakpoint | 560 | Integer | Breakpoint value in pixels to switch between small and large viewport.

## Getting Started with Carousel
Carousel has two main use-cases; data and general content display. To begin with data iteration let’s
use a list of cars to display with carousel.

``` java
public class Car {
    private String model;
    private int year;
    private String manufacturer;
    private String color;
    ...
}
```

```java
public class CarBean {
    private List<Car> cars;

    public CarListController() {
        cars = new ArrayList<Car>();
        cars.add(new Car("myModel", 2005, "ManufacturerX", "blue"));
        //add more cars
    }
    //getter setter
}
```
```xhtml
<p:carousel value="#{carBean.cars}" var="car">
    <p:graphicImage value="/images/cars/#{car.manufacturer}.jpg"/>
    <h:outputText value="Model: #{car.model}" />
    <h:outputText value="Year: #{car.year}" />
    <h:outputText value="Color: #{car.color}" />
</p:carousel>
```
Carousel iterates through the cars collection and renders it’s children for each car.

## Limiting Visible Items
Bu default carousel lists its items in pages with size 3. This is customizable with the rows attribute.

```xhtml
<p:carousel value="#{carBean.cars}" var="car" numVisible="1" itemStyle="width:200px" >
    ...
</p:carousel>
```
## Effects

Paging happens with a slider effect by default and following easing options are supported.

- backBoth
- backIn
- backOut
- bounceBoth
- bounceIn
- bounceOut
- easeBoth
- easeBothStrong
- easeIn
- easeInStrong
- easeNone
- easeOut
- easeInOutCirc
- easeOutStrong
- elasticBoth
- elasticIn
- elasticOut

## SlideShow
Carousel can display the contents in a slideshow, for this purpose _autoPlayInterval_ and _circular_
attributes are used. Following carousel displays a collection of images as a slideshow.

```xhtml
<p:carousel autoPlayInterval="2000" rows="1" effect="easeInStrong" circular="true" itemStyle=”width:200px” >
    <p:graphicImage value="/images/nature1.jpg"/>
    <p:graphicImage value="/images/nature2.jpg"/>
    <p:graphicImage value="/images/nature3.jpg"/>
    <p:graphicImage value="/images/nature4.jpg"/>
</p:carousel>
```
## Content Display
Another use case of carousel is tab based content display.

```xhtml
<p:carousel rows="1" itemStyle="height:200px;width:600px;">
    <p:tab title="Godfather Part I">
        <h:panelGrid columns="2" cellpadding="10">
            <p:graphicImage value="/images/godfather/godfather1.jpg" />
            <h:outputText value="The story begins as Don Vito ..." />
        </h:panelGrid>
    </p:tab>
    <p:tab title="Godfather Part II">
        <h:panelGrid columns="2" cellpadding="10">
            <p:graphicImage value="/images/godfather/godfather2.jpg" />
            <h:outputText value="Francis Ford Coppola's ..."/>
        </h:panelGrid>
    </p:tab>
    <p:tab title="Godfather Part III">
        <h:panelGrid columns="2" cellpadding="10">
            <p:graphicImage value="/images/godfather/godfather3.jpg" />
            <h:outputText value="After a break of ..." />
        </h:panelGrid>
    </p:tab>
</p:carousel>
```
## Item Selection
Sample below selects an item from the carousel and displays details within a dialog.

```xhtml
<h:form id=”form">
    <p:carousel value="#{carBean.cars}" var="car" itemStyle=”width:200px” >
        <p:graphicImage value="/images/cars/#{car.manufacturer}.jpg"/>
        <p:commandLink update=":form:detail" oncomplete="PF('dlg').show()">
            <h:outputText value="Model: #{car.model}" />
            <f:setPropertyActionListener value="#{car}" target="#{carBean.selected}" />
        </p:commandLink>
    </p:carousel>
    <p:dialog widgetVar="dlg">
        <h:outputText id="detail" value="#{carBean.selected}" />
    </p:dialog>
</h:form>
```
```java
public class CarBean {
    private List<Car> cars;
    private Car selected;
    //getters and setters
}
```

## Header and Footer
Header and Footer of carousel can be defined in two ways either, using _headerText_ and _footerText_
options that take simple strings as labels or by _header_ and _footer_ facets that can take any custom
content.

## Responsive
When responsive mode is enabled via setting responsive option to true, carousel switches between
small and large viewport depending on the breakpoint value which is 560 by default.

## Client Side API
Widget: _PrimeFaces.widget.Carousel_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| next() | - | void | Displays next page.
| prev() | - | void | Displays previous page.
| setPage(index) | index | void | Displays page with given index.
| startAutoplay() | - | void | Starts slideshow.
| stopAutoplay() | - | void | Stops slideshow.

## Skinning
Carousel resides in a container element which _style_ and _styleClass_ options apply. _itemStyle_ and
_itemStyleClass_ attributes apply to each item displayed by carousel. Following is the list of structural
style classes;

| Class | Applies | 
| --- | --- | 
| .ui-carousel | Main container
| .ui-carousel-header | Header container
| .ui-carousel-header-title | Header content
| .ui-carousel-viewport | Content container
| .ui-carousel-button | Navigation buttons
| .ui-carousel-next-button | Next navigation button of paginator
| .ui-carousel-prev-button | Prev navigation button of paginator
| .ui-carousel-page-links | Page links of paginator.
| .ui-carousel-page-link | Each page link of paginator.
| .ui-carousel-item | Each item.

As skinning style classes are global, see the main theming section for more information.
