# Dynamic content rendering / streaming

Per default JSF only supports static content placed in resource folders.
Streaming of dynamic content can be done with a custom Servlet or with PrimeFaces:

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

## Dynamic content streaming

### What happens when rendering the _p:graphicImage_:

- _ImageView_ and therefore _DefaultStreamedContent_ is instantiated the first time
- the _ValueExpression_ string (_#{imageView.image}_) is extracted 
- a UID is generated
- the UID and the _ValueExpression_ string are stored into the HTTP session
- the UID is appended to the image URL, which points to JSF _ResourceHandler_

### What happens when the browser requests the URL:

- our _ResourceHandler_ gets the UID from the URL
- receive the _ValueExpression_ from the session
- call the _ValueExpression_ via EL API
- _ImageView_ and therefore _DefaultStreamedContent_ is instantiated the second time
- the stream from the _StreamedContent_ is now copied to the HTTP response

### @ViewScoped support

As the resource is streamed in a second request, which is not bound to any viewstate, _@ViewScoped_ beans are not supported.
A common pattern is to pass the information, which is probably stored in your _@ViewScoped_ bean, via request parameters as you can see in the next chapter.

### MethodExpression support

As the _ValueExpression_ is evaluated in the second request, not when rendering the view, a _MethodExpression_ and method parameters are **NOT** supported.

### Iterating component support

When e.g. _p:graphicImage_ is used in a data iteration component like _p:dataTable_ or _ui:repeat_, it's not possible to use the _var_ in the _value_ expression,
as the _var_ can't be accessed in the second resource streaming browser request:

```xhtml
<ui:repeat value="#{myView.images}" var="img">
    <p:graphicImage value="#{img}" alt="..." />
</ui:repeat>
```

A valid workaround is either the example in 'Pass parameters to the resource request' or set the _stream_ attribute to _false_.

### Pass parameters to the resource request

You can pass request parameters via _f:param_, which will be appended to the resource URL.

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

## Dynamic content rendering via Data URI (stream=_false_ - currently only supported by _p:graphicImage_)

### What happens when rendering the _p:graphicImage_:

- _ImageView_ and therefore _DefaultStreamedContent_ is instantiated
- the _StreamedContent_ is received via the _ValueExpression_ (_#{imageView.image}_)
- the stream from the _StreamedContent_ is now converted and into a base64 string and rendered as _src_ attribute

### Advantages

- it supports _@ViewScoped_ beans
- it supports _MethodExpression_ and parameters

### Disadvantages

- slower rendering time; it should be avoided to use it very often in the view or inside repeating components like _ui:repeat_
- larger content size
