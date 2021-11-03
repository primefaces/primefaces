# Carousel

Carousel is a content slider featuring various customization options.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Carousel.html)

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
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| value | null | Object | A value expression that refers to a collection.
| var | null | String | Name of the request scoped iterator.
| page | null | Integer | Index of the first item.
| numVisible | 1 | Integer | Number of items per page.
| numScroll | 1 | Integer | Number of items to scroll.
| widgetVar | null | String | Name of the client side widget.
| circular | false | Boolean | Defines if scrolling would be infinite.
| autoplayInterval | 0 | Integer | Time in milliseconds to scroll items automatically.
| responsiveOptions | null | List<ResponsiveOption> | A list of options for responsive design.
| orientation | horizontal | Integer | Specifies the layout of the component, valid values are "horizontal" and "vertical".
| verticalViewPortHeight | 300px | String | Height of the viewport in vertical layout.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| contentStyleClass | null | String | Style class of main content.
| containerStyleClass | null | String | Style class of the viewport container.
| indicatorsContentStyleClass | null | String | Style class of the paginator items.
| headerText | null | String | Label for header.
| footerText | null | String | Label for footer.
| touchable | false | Boolean | Enable touch support if browser detection supports it. Default is false because it is globally enabled by default.

## Getting Started
Carousel requires a collection of items as its `value` along with a visual template to render each item.

```xhtml
<p:carousel value="#{carouselView.products}" var="product">
    #{product.name}
</p:carousel>
```

```java
public class CarouselView implements Serializable {

    private List<Product> products;

    @Inject
    private ProductService service;

    @PostConstruct
    public void init() {
        products = service.getProducts(9);
    }

    //getter setter
}
```

```java
public class Product implements Serializable {

    private int id;
    private String name;
    ...
}
```
Carousel iterates through the products collection and renders it’s children for each product.

## Items per page and Scroll Items
Number of items per page is defined using the `numVisible` attribute whereas number of items to scroll is defined with 
the `numScroll` attribute.

```xhtml
<p:carousel value="#{carouselView.products}" var="product" numVisible="3" numScroll="1">
    Content
</p:carousel>
```

## Responsive
For responsive design, `numVisible` and `numScroll` can be defined using the `responsiveOptions` attribute that should be 
a list of ResponsiveOption objects whose breakpoint defines the max-width to apply the settings.

```xhtml
<p:carousel value="#{carouselView.products}" var="product" numVisible="3" numScroll="3"
            responsiveOptions="#{carouselView.responsiveOptions}">
    <f:facet name="header">
        <h5>Basic</h5>
    </f:facet>
    <div class="product-item">
        <div class="product-item-content">
            <div class="p-mb-3">
                <p:graphicImage name="demo/images/product/#{product.image}" styleClass="product-image"/>
            </div>
            <div>
                <h4 class="p-mb-1">#{product.name}</h4>
                <h6 class="p-mt-0 p-mb-3">
                    <h:outputText value="#{product.price}">
                        <f:convertNumber type="currency" pattern="¤#0" currencySymbol="$" />
                    </h:outputText>
                </h6>
                <span class="product-badge status-#{product.inventoryStatus.name().toLowerCase()}">#{product.inventoryStatus.text}</span>
                <div class="p-mt-5">
                    <p:commandButton type="button" icon="pi pi-search" styleClass="rounded-button p-mr-2" />
                    <p:commandButton type="button" icon="pi pi-star" styleClass="ui-button-success rounded-button p-mr-2" />
                    <p:commandButton type="button" icon="pi pi-cog" styleClass="ui-button-help rounded-button" />
                </div>
            </div>
        </div>
    </div>
</p:carousel>
```

```java
public class CarouselView implements Serializable {

    private List<Product> products;

    private List<ResponsiveOption> responsiveOptions;

    @Inject
    private ProductService service;

    @PostConstruct
    public void init() {
        products = service.getProducts(9);
        responsiveOptions = new ArrayList<>();
        responsiveOptions.add(new ResponsiveOption("1024px", 3, 3));
        responsiveOptions.add(new ResponsiveOption("768px", 2, 2));
        responsiveOptions.add(new ResponsiveOption("560px", 1, 1));
    }

    //getter setter
}
```

```java
public class Product implements Serializable {

    private int id;
    private String code;
    private String name;
    private String description;
    private String image;
    private double price;
    private String category;
    private int quantity;
    private InventoryStatus inventoryStatus;
    private int rating;
    private List<Order> orders;
    ...
}
```

## Header and Footer
Header and Footer of carousel can be defined in two ways either, using _headerText_ and _footerText_
options that take simple strings as labels or by _header_ and _footer_ facets that can take any custom
content.

```xhtml
<p:carousel value="#{carouselView.products}" var="product" numVisible="3" numScroll="3"
            responsiveOptions="#{carouselView.responsiveOptions}">
    <f:facet name="header">
        <h2>Custom Header</h2>
    </f:facet>

    Content
    
    <f:facet name="footer">
        <h2>Custom Footer</h2>
    </f:facet>
</p:carousel>
```

## Orientation
Default layout of the Carousel is horizontal, other possible option is the vertical mode that is configured with the
`orientation` attribute.

```xhtml
<p:carousel value="#{carouselView.products}" var="product" orientation="vertical"
            verticalViewPortHeight="352px" style="max-width: 400px;margin-top: 2em;">
    <f:facet name="header">
        <h5>Vertical</h5>
    </f:facet>
    <div class="product-item">
        <div class="product-item-content">
            <div class="p-mb-3">
                <p:graphicImage name="demo/images/product/#{product.image}" styleClass="product-image"/>
            </div>
            <div>
                <h4 class="p-mb-1">#{product.name}</h4>
                <h6 class="p-mt-0 p-mb-3">
                    <h:outputText value="#{product.price}">
                        <f:convertNumber type="currency" pattern="¤#0" currencySymbol="$" />
                    </h:outputText>
                </h6>
                <span class="product-badge status-#{product.inventoryStatus.name().toLowerCase()}">#{product.inventoryStatus.text}</span>
                <div class="p-mt-5">
                    <p:commandButton type="button" icon="pi pi-search" styleClass="rounded-button p-mr-2" />
                    <p:commandButton type="button" icon="pi pi-star" styleClass="ui-button-success rounded-button p-mr-2" />
                    <p:commandButton type="button" icon="pi pi-cog" styleClass="ui-button-help rounded-button" />
                </div>
            </div>
        </div>
    </div>
</p:carousel>
```

## AutoPlay and Circular
When `autoplayInterval` is defined in milliseconds, items are scrolled automatically. In addition, for infinite scrolling
`circular` attribute needs to be enabled. Note that in autoplay mode, circular is enabled by default.
```xhtml
<p:carousel value="#{carouselView.products}" var="product" circular="true" autoplayInterval="3000" numVisible="3" 
            numScroll="1">
    <f:facet name="header">
        <h5>Circular, AutoPlay</h5>
    </f:facet>
    
    Content
    
</p:carousel>
```

## Content Display
Another use case of carousel is tab based content display.
```xhtml
<p:carousel circular="true">
    <f:facet name="header">
        <h5>Tabs</h5>
    </f:facet>
    <p:tab>
        <p class="p-m-0 p-p-3">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
            quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum
            dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
        </p>
    </p:tab>
    <p:tab>
        <p class="p-m-0 p-p-3">
            Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab
            illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut
            odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Consectetur, adipisci velit, sed quia non numquam eius modi.
        </p>
    </p:tab>
    <p:tab>
        <p class="p-m-0 p-p-3">
            At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores
            et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga.
            Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus.
        </p>
    </p:tab>
</p:carousel>
```

## Ajax Behavior Events
The following AJAX behavior event are available for this component. If no event is specified the default event is called.

**Default Event:** `pageChange`

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| pageChange | org.primefaces.event.PageChangeEvent | Is fired after the page changed. |

## Client Side Callbacks
`onPageChange` is called to invoke when the page changes.

```html
<p:carousel onPageChange="pageChange(pageValue);">
    //...content
</p:carousel>

<script type="text/javascript">
    function pageChange(pageValue) {
        //pageValue: index of the current page
    }
</script>
```

## Client Side API
Widget: _PrimeFaces.widget.Carousel_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| navForward() | - | void | Moves this carousel to the next page. If autoplay is active, it will stop.
| navBackward() | - | void | Moves this carousel to the previous page. If autoplay is active, it will stop.
| step(dir, page) | index | void | Moves this carousel to the given page. _dir_ is a direction of the move and takes a value of -1 or 1.
| startAutoplay() | - | void | Enables autoplay and starts the slideshow.
| stopAutoplay() | - | void | Disables autoplay and stops the slideshow.

## Skinning
Carousel resides in a container element which _style_ and _styleClass_ options apply. _contentStyleClass_ attribute 
apply style classes to the main content container, _containerStyleClass_ attribute apply style classes to the container 
of the viewport and _indicatorsContentStyleClass_ attribute apply style classes to the container of the indicators. 
Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-carousel | Main container.
| .ui-carousel-header | Header container.
| .ui-carousel-footer | Footer container.
| .ui-carousel-content | Main content container. It contains the container of the viewport.
| .ui-carousel-container | Container of the viewport. It contains navigation buttons and viewport.
| .ui-carousel-items-content | Viewport.
| .ui-carousel-item | Content items in the item container.
| .ui-carousel-indicators | Container of the indicators.
| .ui-carousel-indicator | Indicator element.

As skinning style classes are global, see the main theming section for more information.
