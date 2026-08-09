# Dependencies

PrimeFaces only requires a Java 11+ runtime and a Jakarta Faces / Faces implementation as mandatory
dependencies. There’re some optional libraries for certain features. Licenses of all dependencies and
any 3rd part work incorporated are compatible with the PrimeFaces Licenses.

| Dependency | GroupId | ArtifactId | Version | Type | Description |
| --- | --- | --- | --- | --- | --- |
| Faces (Jakarta Faces) | | | 2.3, 3.0, 4.0 | Required | Apache MyFaces, Eclipse Mojarra, ... |
| Servlet | | | 3.0+ | Required | Apache Tomcat, Eclipse Jetty, ... |
| Expression Language | | | 2.2+ | Required | Apache Tomcat, Eclipse Jetty, ... |
| Bean Validation | | | 1.1+ | Optional | Apache BVal, Hibernate Validator, ... |
| Libre OpenPDF | com.github.librepdf | openpdf | 1.4.2 | Optional | DataExporter (PDF) |
| Apache POI | org.apache.poi | poi | 5.5.1 | Optional | DataExporter (Excel XLS or XML) |
| Apache POI | org.apache.poi | poi-ooxml | 5.5.1 | Optional | DataExporter (Excel XLSX Office 2003) |
| RSS Reader | com.apptasticsoftware | rssreader | 3.12.0 | Optional | FeedReader |
| Okapi | uk.org.okapibarcode | okapibarcode | 0.5.2 | Optional | Barcode |
| owasp-java-html-sanitizer | com.googlecode.owasp-java-html-sanitizer | owasp-java-html-sanitizer | 20260101.1 | Optional | TextEditor |
| XDEV Chart.js Java Model | software.xdev | chartjs-java-model | 2.9.0 | Optional | Charts |

*Listed versions are tested and known to be working with PrimeFaces, other versions of these
dependencies may also work but not tested.

## Jakarta Faces Runtime ##
PrimeFaces supports Jakarta Faces / Faces 2.3, 3.0 and 4.0 runtimes at the same time using feature detection and
by not having compile time dependency to a specific version. As a result some features are only
available depending on the runtime.

A good example for runtime compatibility is the passthrough attributes, a Jakarta Faces 2.2 specific feature to
display dynamic attributes. In following page, the passthrough attribute **placeholder** only gets rendered
if the runtime is Jakarta Faces 2.2.

```xml
<!DOCTYPE html>
<html xmlns="http://www.w3c.org/1999/xhtml"
        xmlns:h="jakarta.faces.html"
        xmlns:p="http://primefaces.org/ui"
        xmlns:pt="jakarta.faces.passthrough">
    <h:head>
    </h:head>
    <h:body>
        <p:inputText value="#{bean.value}" pt:placeholder="Watermark here"/>
    </h:body>
</html>
```
