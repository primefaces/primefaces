# FileDownload

The legacy way to present dynamic binary data to the client is to write a `servlet` or a `servlet filter` and
stream the binary data. FileDownload presents an easier way to do the same.

## Info

| Name | Value |
| --- | --- |
| Tag | fileDownload
| ActionListener Class | org.primefaces.component.filedownload.FileDownloadActionListener

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| value | null | StreamedContent | A streamed content instance.
| contentDisposition | attachment | String | Specifies display mode on non-ajax downloads.
| monitorKey | null | String | Optional key to support monitoring multiple filedownloads on same page.

## Getting started with FileDownload
A user command action is required to trigger the filedownload process.  
FileDownload can be attached to any command component like a `commandButton` or `commandLink`.
The value of the FileDownload must be an _org.primefaces.model.StreamedContent_ instance.
We suggest using the built-in _DefaultStreamedContent_ implementation.
First parameter of the constructor is the binary stream, second is the mimeType and the third parameter is the name of the file.

```java
public class FileBean {
    private StreamedContent file;

    public FileDownloadController() {
        file = DefaultStreamedContent.builder()
                    .contentType("application/pdf")
                    .name("downloaded_file.pdf")
                    .stream(() -> this.getClass().getResourceAsStream("yourfile.pdf"))
                    .build();
    }

    public StreamedContent getFile() {
        return this.file;
    }
}
```
This streamed content should be bound to the `value` attribute of the `p:fileDownload`.

```xhtml
<h:commandButton value="Download">
    <p:fileDownload value="#{fileBean.file}" />
</h:commandButton>
```

Similarly a more graphical presentation would be to use a `commandlink` with an image.

```xhtml
<h:commandLink value="Download">
    <p:fileDownload value="#{fileBean.file}"/>
    <h:graphicImage value="pdficon.gif" />
</h:commandLink>
```

## AJAX downloading
Before PrimeFaces 10, you had to disable AJAX on commandButton and commandLink. As of version 10 that's no longer needed.

The AJAX download uses the same principle you may know from the _p:graphicImage_ component. Basically we generate a URI
based on the _DefaultStreamedContent_ which is handled by JavaScript triggering a download.
Please see our core documentation about it [Dynamic Content Streaming / Rendering](/core/dynamiccontent.md)

## ContentDisposition
On regular (non-AJAX) downloads, by default, content is displayed as an _attachment_ with a download dialog box,
another alternative is the _inline_ mode, in this case browser will try to open the file internally without a prompt.
Note that content disposition is not part of the http standard although it is widely implemented.

## Monitor Status
As fileDownload process is non-AJAX, ajaxStatus cannot apply. Still PrimeFaces provides a feature
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
