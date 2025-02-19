# Dynamic content rendering

Dynamic content rendering allows you to display dynamic data directly through a Data URI, which is achieved by setting `stream=false`.

!> Currently it's only supported by _p:graphicImage_ and it should only be used for very small images!

## How does it work?

- _ImageView_ and therefore _DefaultStreamedContent_ is instantiated
- the _StreamedContent_ is received via the _ValueExpression_ (_#{imageView.image}_)
- the stream from the _StreamedContent_ is now converted and into a base64 string and rendered as _src_ attribute

### Use InputStream / byte[] array

You may already have your image in memory in an `InputStream` or `byte[]` array. We will try to determine your image content using magic bytes to figure out if its a PNG, JPG, or GIF.
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
