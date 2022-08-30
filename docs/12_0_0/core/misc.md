# Misc

## Servlet Filters

### CharacterEncodingFilter

PrimeFaces provides a CharacterEncodingFilter with default encoding UTF-8.

```xml
<filter>
    <filter-name>Character Encoding Filter</filter-name>
    <filter-class>org.primefaces.webapp.filter.CharacterEncodingFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>Character Encoding Filter</filter-name>
    <servlet-name>Faces Servlet</servlet-name>
</filter-mapping>
```

You can also specify a custom encoding:

```xml
<filter>
    <filter-name>Character Encoding Filter</filter-name>
    <filter-class>org.primefaces.webapp.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
</filter>
```

### NoCacheFilter

Per default JSF correctly sets the cache headers for resources like JS and CSS.
However it does not set no-cache headers for JSF views, which can be enabled with this filter.

```xml
<filter>
    <filter-name>No Cache Filter</filter-name>
    <filter-class>org.primefaces.webapp.filter.NoCacheFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>No Cache Filter</filter-name>
    <servlet-name>Faces Servlet</servlet-name>
</filter-mapping>
```

### Cache Busting Resource Handler

Currently you can disable appending the version number to every URL by changing the `web.xml` parameter `primefaces.RESOURCE_VERSION`. 
It can be argued whether putting the version number on the URL is a security risk or not, but this property disables it:

```xml
<context-param>
    <param-name>primefaces.RESOURCE_VERSION</param-name>
    <param-value>false</param-value>
</context-param>
```

However, this version number appended to the URL allows PrimeFaces to "bust the cache" and force the browser to load
the latest JavaScript and CSS.  Users have requested being able to customize this cache busting string to not use 
the PrimeFaces version number.  Simply implement your own resource handler to append whatever value you wish to the URL. In this
case it appends `"v=abc12345"` to every PrimeFaces URL.

```java
public class CustomResourceWrapper extends ResourceWrapper {

    private Resource wrapped;
    private String version;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public CustomResourceWrapper(final Resource resource) {
        super();
        wrapped = resource;

        FacesContext context = FacesContext.getCurrentInstance();
        version = "&v=abc12345";
    }

    @Override
    public Resource getWrapped() {
        return wrapped;
    }

    @Override
    public String getRequestPath() {
        return super.getRequestPath() + version;
    }

    @Override
    public String getContentType() {
        return getWrapped().getContentType();
    }

    @Override
    public String getLibraryName() {
        return getWrapped().getLibraryName();
    }

    @Override
    public String getResourceName() {
        return getWrapped().getResourceName();
    }

    @Override
    public void setContentType(final String contentType) {
        getWrapped().setContentType(contentType);
    }

    @Override
    public void setLibraryName(final String libraryName) {
        getWrapped().setLibraryName(libraryName);
    }

    @Override
    public void setResourceName(final String resourceName) {
        getWrapped().setResourceName(resourceName);
    }

    @Override
    public String toString() {
        return getWrapped().toString();
    }
}

public class CustomResourceHandler extends ResourceHandlerWrapper {

    private final ResourceHandler wrapped;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public CustomResourceHandler(ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ResourceHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public Resource createResource(String resourceName, String libraryName) {
        Resource resource = super.createResource(resourceName, libraryName);
        return wrapResource(resource, libraryName);
    }

    @Override
    public Resource createResource(String resourceName, String libraryName, String contentType) {
        Resource resource = super.createResource(resourceName, libraryName, contentType);
        return wrapResource(resource, libraryName);
    }

    private Resource wrapResource(Resource resource, String libraryName) {
        if (resource != null && libraryName != null
                    && (libraryName.toLowerCase().startsWith(Constants.LIBRARY))) {
            return new CustomResourceWrapper(resource);
        }
        else {
            return resource;
        }
    }
}

```

Then in your `faces-config.xml` enable it with:

```xml
<application>
    <resource-handler>com.yourorg.CustomResourceHandler</resource-handler>
</application>
```
