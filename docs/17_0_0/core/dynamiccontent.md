# Dynamic content

By default, Jakarta Faces only supports static content located in resource folders.  
Without PrimeFaces, developers typically rely on custom Servlets or Servlet Filters to manage dynamic content.  
PrimeFaces simplifies this process by introducing dynamic content streaming and rendering, offering a more efficient and simple way to handle dynamic resources, as shown below:

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


## When to use _Streaming_ vs. _Rendering_

By default, **Streaming** is the preferred approach as it delivers the content through an additional browser request.  
**Rendering**, on the other hand, directly embeds the dynamic content as a Data URI.

The main disadvantage of **Streaming** is its lack of support for `@ViewScoped` beans.

**Rendering** offers the advantage of supporting `@ViewScoped` beans and allows the use of `MethodExpression`, including parameters. 
However, it comes with slower performance and larger content size, making it less suitable for frequent use, especially within repeating components like `ui:repeat`.  
For this reason, rendering should be used sparingly and only when necessary. Additionally, it is currently only supported by `p:graphicImage`.

