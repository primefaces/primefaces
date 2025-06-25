# Dynamic content streaming

Dynamic content streaming generates a dynamic URL that serves the content via an additional server request.   
This request resolves the StreamedContent and delivers the dynamic content directly to the client.

## How does it work?

### During rendering:

- The _ValueExpression_ will be resolved: _#{imageView.image}_ (StreamedContent.class) is extraced via EL API
- The _ValueExpression_ as string (_#{imageView.image}_) and the value type (_StreamedContent.class_) will be extracted
- _ImageView_ and therefore _DefaultStreamedContent_ might get instantiated here, when the EL API can't resolve the value type (_StreamedContent.class_) correctly
- a UID is generated
- the UID and the _ValueExpression_ string are stored into the HTTP session
- the UID is appended to the image URL, which points to Jakarta Faces _ResourceHandler_

### During the resource request:

- our _ResourceHandler_ gets the UID from the URL
- receive the _ValueExpression_ from the session
- call the _ValueExpression_ via EL API
- _ImageView_ and therefore _DefaultStreamedContent_ is instantiated
- the stream from the _StreamedContent_ is now copied to the HTTP response

## @ViewScoped support

As the resource is streamed in a second request, which is not bound to any viewstate, _@ViewScoped_ beans are not supported.
A common pattern is to pass the information, which is probably stored in your _@ViewScoped_ bean, via request parameters as you can see in the next chapter.

## Composite Components support

In case when a _ValueExpression_ is passed to a composite component, we need some additional transformation, as the resource request is completely unrelated to any composite or Faces view.
In the example above, we need to transform `#{cc.attrs.streamedContent}` to `#{myBean.streamedContent}`: 

```
<myLib:myMedia streamedContent="#{myBean.streamedContent}" />
```

```
<composite:interface>
    <composite:attribute name="streamedContent" required="true" />
</composite:interface>
<composite:implementation>
    <p:media value="#{cc.attrs.streamedContent}" />
</composite:implementation>
```

In this case there are some not supported features:
1) reusing a variable from JSTL (e.g. `<c:set ... />`)
2) passing a partial expression like:
    ```
    <myLib:myMedia controller="#{myBean}" />
    ```
    ```
    <composite:interface>
        <composite:attribute name="controller" required="true" />
    </composite:interface>
    <composite:implementation>
        <p:media value="#{cc.attrs.controller.streamedContent}" />
    </composite:implementation>
    ```
3) referencing the StreamedContent as _MethodExpression_
   ```
   <p:media value="#{cc.attrs.controller.getStreamedContent()}" />
   ```

## MethodExpression support

As the _ValueExpression_ is evaluated in the second request, not when rendering the view, a _MethodExpression_ and method parameters are **NOT** supported.

## Iterating component support

When e.g. _p:graphicImage_ is used in a data iteration component like _p:dataTable_ or _ui:repeat_, it's not possible to use the _var_ in the _value_ expression,
as the _var_ can't be accessed in the second resource streaming browser request:

```xhtml
<ui:repeat value="#{myView.images}" var="img">
    <p:graphicImage value="#{img}" alt="..." />
</ui:repeat>
```

A valid workaround is either the example in 'Pass parameters to the resource request' or set the _stream_ attribute to _false_.

## Pass parameters to the resource request

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
                        return this.getClass().getResourceAsStream("user" + userId + ".jpg");
                    })
                    .build();
    }

    public StreamedContent getImage() {
        return image;
    }
}
```

In case you need to access URL parameters to construct a `StreamedContent`, you need to make sure to skip the `RENDER_RESPONSE` phase:

```java
@Named
@RequestScoped
public class ImageView {

    public StreamedContent getImage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return DefaultStreamedContent.DUMMY; // might get invoked already during rendering, check the docs
        }
        
        String userId = facesContext.getExternalContext().getRequestParameterMap().get("user");
        User user = userRepository.load(userId);
 
        return DefaultStreamedContent.builder()
            .name(user.getName() + ".png")
            .contentType("image/jpeg")
            .stream(() -> {
                return new FileInputStream(userRepository.getImagePath(user));
            })
            .build();
    }
}
```

## Use InputStream / byte[] array

As alternative to the _StreamedContent_ model, you can directly use a `InputStream` or `byte[]` array.  
In this case the content-type header will not be set in the response.  
If you need to set a content-type, we recommend to use the _org.primefaces.model.StreamedContent_.

## Avoid buffering

`StreamedContent` can either be used with a `InputStream` or with a `Consumer<OutputStream>` to write directly to the response:

```java
@Named
@RequestScoped
public class ImageView {
    private StreamedContent image;

    public ImageView() {
        image = DefaultStreamedContent.builder()
                    .contentType("image/jpeg")
                    .writer((os) -> {
                        try {
                            os.write(...);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .build();
    }

    public StreamedContent getImage() {
        return image;
    }
}
```

## Dynamic Content Limit / View

PrimeFaces limits the number of dynamic contents per view to 200 by default to avoid session blow up.  
This means only 200 dynamic items (such as images or other files) can be streamed during requesting a view.

Once this limit is reached, dynamic content may fail to display correctly. In such cases, placeholders might appear, or content may not load.

The limit can be adjusted using the `primefaces.DYNAMIC_CONTENT_LIMIT` parameter in the `web.xml`:

```xml
<context-param>
    <param-name>primefaces.DYNAMIC_CONTENT_LIMIT</param-name>
    <param-value>500</param-value>
</context-param>
```
