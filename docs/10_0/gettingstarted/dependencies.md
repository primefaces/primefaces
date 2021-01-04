# Dependencies

PrimeFaces only requires a Java 8+ runtime and a JSF 2.x implementation as mandatory
dependencies. There’re some optional libraries for certain features. Licenses of all dependencies and
any 3rd part work incorporated are compatible with the PrimeFaces Licenses.

| Dependency | GroupId | ArtifactId | Version | Type | Description |
| --- | --- | --- | --- | --- | --- |
| JSF runtime | | |  2.0, 2.1, 2.2, 2.3, 3.0 | Required | Apache MyFaces or Eclipse (former Oracle) Mojarra |
| Libre OpenPDF | com.github.librepdf | openpdf | 1.3.23 | Optional | DataExporter (PDF) |
| Apache POI | org.apache.poi | poi | 4.1.2 | Optional | DataExporter (Excel or XML) |
| Rome | com.rometools | rome | 1.13.0 | Optional | FeedReader |
| Apache Commons FileUpload | commons-fileupload | commons-fileupload | 1.4 | Optional | FileUpload |
| barcode4j-light | net.sf.barcode4j | barcode4j-light | 2.1 | Optional | Barcode |
| qrgen | net.glxn | qrgen |  1.4 | Optional | QR Code support for Barcode |
| owasp-java-html-sanitizer | com.googlecode.owasp-java-html-sanitizer | owasp-java-html-sanitizer |  20200713.1 | Optional | TextEditor |

*Listed versions are tested and known to be working with PrimeFaces, other versions of these
dependencies may also work but not tested.

## JSF Runtime ##
PrimeFaces supports JSF 2.0, 2.1, 2.2, 2.3 and 3.0 runtimes at the same time using feature detection and
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
