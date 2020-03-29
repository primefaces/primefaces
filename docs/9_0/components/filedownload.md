# FileDownload

The legacy way to present dynamic binary data to the client is to write a servlet or a filter and
stream the binary data. FileDownload presents an easier way to do the same.

## Info

| Name | Value |
| --- | --- |
| Tag | fileDownload
| ActionListener Class | org.primefaces.component.filedownload.FileDownloadActionListener

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| value | null | StreamedContent | A streamed content instance
| contentDisposition | attachment | String | Specifies display mode.
| monitorKey | null | String | Optional key to support monitoring multiple filedownloads on same page.

## Getting started with FileDownload
A user command action is required to trigger the filedownload process. FileDownload can be
attached to any command component like a commandButton or commandLink. The value of the
FileDownload must be an _org.primefaces.model.StreamedContent_ instance. We suggest using the
built-in _DefaultStreamedContent_ implementation. First parameter of the constructor is the binary
stream, second is the mimeType and the third parameter is the name of the file.

```java
public class FileBean {
    private StreamedContent file;

    public FileDownloadController() {
        InputStream stream = this.getClass().getResourceAsStream("yourfile.pdf");
        file = new DefaultStreamedContent(stream, "application/pdf", "downloaded_file.pdf");
    }
    public StreamedContent getFile() {
        return this.file;
    }
}
```
This streamed content should be bound to the value of the fileDownload.

```xhtml
<h:commandButton value="Download">
    <p:fileDownload value="#{fileBean.file}" />
</h:commandButton>
```

Similarly a more graphical presentation would be to use a commandlink with an image.

```xhtml
<h:commandLink value="Download">
    <p:fileDownload value="#{fileBean.file}"/>
    <h:graphicImage value="pdficon.gif" />
</h:commandLink>
```
If you’d like to use PrimeFaces commandButton and commandLink, disable ajax option as
fileDownload requires a full page refresh to present the file.

```xhtml
<p:commandButton value="Download" ajax="false">
    <p:fileDownload value="#{fileBean.file}" />
</p:commandButton>
```
```xhtml
<p:commandLink value="Download" ajax="false">
    <p:fileDownload value="#{fileBean.file}"/>
    <h:graphicImage value="pdficon.gif" />
</p:commandLink>
```
## ContentDisposition
By default, content is displayed as an _attachment_ with a download dialog box, another alternative is
the _inline_ mode, in this case browser will try to open the file internally without a prompt. Note that
content disposition is not part of the http standard although it is widely implemented.

## Monitor Status
As fileDownload process is non-ajax, ajaxStatus cannot apply. Still PrimeFaces provides a feature
to monitor file downloads via client side _monitorDownload(startFunction, endFunction)_ method.
Example below displays a modal dialog when dowload begins and hides it on complete.

```js
<script type="text/javascript">
    function showStatus() {
        PF('statusDialog').show();
    }
    function hideStatus() {
        PF('statusDialog').hide();
    }
</script>
```

```xhtml
<h:form>
    <p:dialog modal="true" widgetVar="statusDialog" header="Status" draggable="false" closable="false">
        <p:graphicImage value="/design/ajaxloadingbar.gif" />
    </p:dialog>
    <p:commandButton value="Download" ajax="false" onclick="PrimeFaces.monitorDownload(showStatus, hideStatus)">
        <p:fileDownload value="#{fileDownloadController.file}"/>
    </p:commandButton>
</h:form>
```
Cookies must be enabled for monitoring.