# Hello World

Once you have added the primefaces jar to your classpath, you need to add the PrimeFaces
namespace to your page to begin using the components. Here is a simple page like test.xhtml;

```html
<!DOCTYPE html>
    <html xmlns="http://www.w3c.org/1999/xhtml"
            xmlns:h="http://xmlns.jcp.org/jsf/html"
            xmlns:p="http://primefaces.org/ui">
    <h:head></h:head>
    <h:body>
        <p:textEditor />
    </h:body>
</html>
```
When you run this page through Faces Servlet mapping e.g. *.jsf, you should see a rich text editor
when you run the page with test.jsf.