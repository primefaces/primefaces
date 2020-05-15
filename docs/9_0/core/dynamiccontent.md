# Dynamic content rendering / streaming

Per default JSF only supports static content placed in resource folders.
Streaming of dynamic content can be done with a custom Servlet or with PrimeFaces.

Lets take this example:

```xhtml
<p:graphicImage value="#{imageView.image}" />
```

```java
@Named
@RequestScoped
public class ImageView {
    private StreamedContent image;

    public ImageView() {
        image = DefaultStreamedContent.builder()
                    .contentType("image/jpeg")
                    .stream(() -> this.getClass().getResourceAsStream("barcalogo.jpg"))
                    .build();
    }

    public StreamedContent getImage() {
        return image;
    }
}
```

## Dynamic content streaming (stream=`true`, which is the default)

### What happens when rendering the `p:graphicImage`:

- `ImageView` and therefore `DefaultStreamedContent` is instantiated the first time
- the `ValueExpression` string (`#{imageView.image}`) is extracted 
- a UID is generated
- the UID and the `ValueExpression` string are stored into the HTTP session
- the UID is appended to the image URL, which points to JSF `ResourceHandler`

### What happens when the browser requests the URL:

- our `ResourceHandler` gets the UID from the URL
- receive the `ValueExpression` from the session
- call the `ValueExpression` via EL API
- `ImageView` and therefore `DefaultStreamedContent` is instantiated the second time
- the stream from the `StreamedContent` is now copied to the HTTP response

### @ViewScoped support

As the resource is streamed in a second request, which isn't bound to any viewstate, `@ViewScoped` beans are not supported.
A common pattern is to pass the informations, which are probably stored in your `@ViewScoped` bean, via request parameters as you can see in the next chapter.

### Pass parameters to the resource request

You can pass request parameters via `f:param`, which will be appended to the resource URL.
This is extremely handy to display dynamic content, if your image is in a data iteration component like `p:dataTable` or `ui:repeat`.

```xhtml
<p:graphicImage value="#{imageView.image}">
    <f:param name="user" value="#{cc.attrs.userId}" />
</p:graphicImage>
```

```java
@Named
@RequestScoped
public class ImageView {
    private StreamedContent image;

    public ImageView() {
        image = DefaultStreamedContent.builder()
                    .contentType("image/jpeg")
                    .stream(() -> {
                        FacesContext context = FacesContext.getCurrentInstance();
                        String userId = context.getExternalContext().getRequestParameterMap().get("user");
                        return this.getClass().getResourceAsStream("user" + userId + ".jpg")
                    })
                    .build();
    }

    public StreamedContent getImage() {
        return image;
    }
}
```

## Dynamic content rendering via Data URI (stream=`false`)

### What happens when rendering the `p:graphicImage`:

- `ImageView` and therefore `DefaultStreamedContent` is instantiated
- the `StreamedContent` is received via the `ValueExpression` (`#{imageView.image}`)
- the stream from the `StreamedContent` is now converted and into a base64 string and rendered as `src` attribute

### Advantages

- it supports `@ViewScoped` beans

### Disadvantages

- slower rendering time; it should be avoided to use it very often in the view or inside reapeating components like `ui:repeat`
- bigger content size
