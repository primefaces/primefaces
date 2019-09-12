# Dependencies

PrimeFaces only requires a Java 8+ runtime and a JSF 2.x implementation as mandatory
dependencies. There’re some optional libraries for certain features. Licenses of all dependencies and
any 3rd part work incorporated are compatible with the PrimeFaces Licenses.

| Dependency | Version | Type | Description |
| --- | --- | --- | --- |
| JSF runtime | 2.0, 2.1, 2.2, 2.3 | Required | Apache MyFaces or Eclipse (former Oracle) Mojarra |
| itext | 2.1.7 | Optional | DataExporter (PDF) |
| apache poi | 3.17 | Optional | DataExporter (Excel) |
| rome | 1.9.0 | Optional | FeedReader |
| commons-fileupload | 1.3.3 | Optional | FileUpload |
| apache tika | 1.22 | Optional | Advanced security (content type validation) for FileUpload |
| barcode4j-light | 2.1 | Optional | Barcode |
| qrgen |  1.4 | Optional | QR Code support for Barcode |
| owasp-java-html-sanitizer |  20190503.1 | Optional | TextEditor |

*Listed versions are tested and known to be working with PrimeFaces, other versions of these
dependencies may also work but not tested.

## JSF Runtime ##
PrimeFaces supports JSF 2.0, 2.1, 2.2 and 2.3 runtimes at the same time using feature detection and
by not having compile time dependency to a specific version. As a result some features are only
available depending on the runtime.

A good example for runtime compatibility is the passthrough attributes, a JSF 2.2 specific feature to
display dynamic attributes. In following page, the passthrough attribute **placeholder** only gets rendered
if the runtime is JSF 2.2.

```xml
<!DOCTYPE html>
<html xmlns="http://www.w3c.org/1999/xhtml"
        xmlns:h="http://java.sun.com/jsf/html"
        xmlns:p="http://primefaces.org/ui"
        xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
    <h:head>
    </h:head>
    <h:body>
        <p:inputText value="#{bean.value}" pt:placeholder="Watermark here"/>
    </h:body>
</html>
```