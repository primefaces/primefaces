# Performance

In generell both the JSF implementations and PrimeFaces has good performance per default.
Most of the performance bottlenecks are in the backend and probably database queries.
However, there are some settings and patterns that can improve the performance.


## Recommended configuration

### Common
```xml
<context-param>
    <param-name>javax.faces.PROJECT_STAGE</param-name>
    <param-value>Production</param-value>
</context-param>
<context-param>
    <param-name>javax.faces.FACELETS_REFRESH_PERIOD</param-name>
    <param-value>-1</param-value>
</context-param>
<context-param>
    <param-name>javax.faces.STATE_SAVING_METHOD</param-name>
    <param-value>server</param-value>
</context-param>
```

### PrimeFaces
```xml
<!-- Enable partial submit in PrimeFaces - this reduces the network traffic -->
<context-param>
    <param-name>primefaces.SUBMIT</param-name>
    <param-value>partial</param-value>
</context-param>

<!-- Move above the fold scripts to the bottom (end of body).
     This is a huge improvement of the visible rendering and removes flickering between navigations. -->
<context-param>
    <param-name>primefaces.MOVE_SCRIPTS_TO_BOTTOM</param-name>
    <param-value>true</param-value>
</context-param>
```


### Mojarra
```xml
<!-- reduce saved view states -->
<context-param>
    <param-name>com.sun.faces.numberOfViewsInSession</param-name>
    <param-value>15</param-value>
</context-param>

<!-- Disable ViewState compression (better performance but more memory usage) -->
<context-param>
    <param-name>com.sun.faces.compressViewState</param-name>
    <param-value>false</param-value>
</context-param>
```


### MyFaces

```xml
<!-- Cache EL expressions; See: https://myfaces.apache.org/wiki/core/user-guide/configuration-of-special-features/cache-el-expressions.html-->
<context-param>
    <param-name>org.apache.myfaces.CACHE_EL_EXPRESSIONS</param-name>
    <param-value>alwaysRecompile</param-value>
</context-param>

<!-- reduce saved view states -->
<context-param>
    <param-name>org.apache.myfaces.NUMBER_OF_VIEWS_IN_SESSION</param-name>
    <param-value>15</param-value>
</context-param>
<context-param>
    <param-name>org.apache.myfaces.NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION</param-name>
    <param-value>3</param-value>
</context-param>

<!-- Disable ViewState compression (better performance but more memory usage) -->
<context-param>
    <param-name>org.apache.myfaces.COMPRESS_STATE_IN_SESSION</param-name>
    <param-value>false</param-value>
</context-param>

<context-param>
    <param-name>org.apache.myfaces.CHECK_ID_PRODUCTION_MODE</param-name>
    <param-value>false</param-value>
</context-param>

<!-- Flush the response directly after the head to allow start loading resources on the browser side -->
<context-param>
    <param-name>org.apache.myfaces.EARLY_FLUSH_ENABLED</param-name>
    <param-value>true</param-value>
</context-param>

<context-param>
    <param-name>org.apache.myfaces.PRETTY_HTML</param-name>
    <param-value>false</param-value>
</context-param>

<!-- Increase startup performance and EL resolution by disable deprecated features -->
<context-param>
    <param-name>org.apache.myfaces.SUPPORT_JSP_AND_FACES_EL</param-name>
    <param-value>false</param-value>
</context-param>
<!-- NOTE: the ExpressionFactory might differ e.g. on Glassfish or Wildfly.
     This parameter is optional since MyFaces 2.3.3. -->
<context-param>
    <param-name>org.apache.myfaces.EXPRESSION_FACTORY</param-name>
    <param-value>org.apache.el.ExpressionFactoryImpl</param-value>
</context-param>

<!-- Increase cache -->
<context-param>
    <param-name>org.apache.myfaces.VIEW_UNIQUE_IDS_CACHE_ENABLED</param-name>
    <param-value>true</param-value>
</context-param>
<context-param>
    <param-name>org.apache.myfaces.COMPONENT_UNIQUE_IDS_CACHE_SIZE</param-name>
    <param-value>500</param-value>
</context-param>
```

You can also try ViewPooling: http://lu4242.blogspot.com/2013/12/view-pooling-in-jsf-22-using-apache.html
and Whitespace compression: http://lu4242.blogspot.com/2012/12/html-white-space-compression-for-jsf.html


### Other

- Only required for non-JSF managed resources: Use a custom ServletFilter to set the correct expires/cache headers of your resources (images, stylesheets, javascripts).
- Compress and optimize your Javascripts in your build process. If you use maven, try primefaces-extensions' closure compiler maven plugin.
- Enable GZIP in your webserver. If it's not supported by your webserver/container, you can still add the GzipResponseFilter from OmniFaces: http://showcase.omnifaces.org/filters/GzipResponseFilter

## Patterns

- Correctly define update/render and process/execute! Often this is a huge improvement as many times the whole form is updated instead only a small part. But also think about maintainability over performance here.
- If you don't use ViewScoped beans, it's a good but small improvement to mark the view as stateless.
- Try using HTML over JSF tags
    - Especially avoid using h:outputText if you don't need the escaping or other features like converters. Just use EL expressions inside your XHTML.
    - The same applies for some other components like p:outputPanel. Just use a plain div. If you need to make it updateable, you can still use "passtrough elements" (`<div jsf:id="...">...</div>`)
- Try to avoid logic in getters because they can be called multiple times - especially for the rendered attribute!
- Avoid logic (like inline if-statements) in EL expression! It's better to move those logic into the bean. It's faster and often easier to read and maintain.
- Prefer AJAX over a full postback
- If you have many p:outputLabel's on the page and you know that the input field is required or not, it's a good performance improvemet to set the input to required=true or the outputLabel to indicateRequired=true|false. The default value for indicateRequired since 6.2 is auto, which tries to lookup BeanValidation constrains to check if @NotNull|@NotEmpty|@NotBlank are defined.
- Cache data, which is required multiple times in the same view, in @RequestScoped beans.
- Avoid missusing @SessionScoped, @ViewScoped and other long living scopes if not really required.
- Try to put only small amount of data in such long living beans. Sometimes a small flag or id of an entity is enough information. Often people put many entities in such long living beans.
