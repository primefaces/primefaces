# FileUpload

FileUpload goes beyond the browser input type="file" functionality and features an HTML5
powered rich solution with graceful degradation for legacy browsers.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/primefaces.widget.fileupload.html)

## Info

| Name | Value |
| --- | --- |
| Tag | fileUpload
| Component Class | org.primefaces.component.fileupload.FileUpload
| Component Type | org.primefaces.component.FileUpload
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.FileUploadRenderer
| Renderer Class | org.primefaces.component.fileupload.FileUploadRenderer

## Attributes

| Name | Default | Type | Description|
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| value | null | Object | Value of the component than can be either an EL expression of a literal text.
| converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id.
| immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
| required | false | Boolean | Marks component as required.
| validator | null | MethodExpr | A method expression that refers to a method validationg the input.
| valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuchangeevent.
| requiredMessage | null | String | Message to be displayed when required field validation fails.
| converterMessage | null | String | Message to be displayed when conversion fails.
| validatorMessage | null | String | Message to be displayed when validation fails.
| onvalidationfailure | null | String | Handler called when client-side validation fails.
| widgetVar | null | String | Name of the client side widget.
| update | @none | String | Component(s) to update after fileupload completes.
| process | @all | String | Component(s) to process in fileupload request.
| listener | null | MethodExpr | Method to invoke when a file is uploaded.
| multiple | false | Boolean | Allows choosing of multi file uploads from native file browse dialog
| auto | false | Boolean | When set to true, selecting a file starts the upload process implicitly.
| label | Choose | String | Label of the browse button.
| allowTypes | null | String | Regular expression for accepted file types, e.g. /(\\.\|\\/)(gif\|jpe?g\|png)$/
| sizeLimit | null | Long | Individual file size limit in bytes.
| fileLimit | null | Integer | Maximum number of files allowed to upload.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| mode | advanced | String | Mode of the fileupload, can be _simple_ or _advanced_.
| uploadLabel | Upload | String | Label of the upload button.
| cancelLabel | Cancel | String | Label of the cancel button.
| invalidSizeMessage | null | String | Message to display when size limit exceeds.
| invalidFileMessage | null | String | Message to display when file is not accepted.
| fileLimitMessage | null | String | Message to display when file limit exceeds.
| dragDropSupport | true | Boolean | Specifies dragdrop based file selection from filesystem, default is true and works only on supported browsers.
| onstart | null | String | Client side callback to execute when upload begins.
| onerror | null | String | Callback to execute if fileupload request fails.
| oncomplete | null | String | Client side callback to execute when upload ends.
| disabled | false | Boolean | Disables component when set true.
| messageTemplate | {name} {size} | String | Message template to use when displaying file validation errors.
| previewWidth | 80 | Integer | Width for image previews in pixels.
| skinSimple | false | Boolean | Applies theming to simple uploader.
| accept | null | String | Filters files in native file browser dialog.
| sequential | false | Boolean | Uploads are concurrent by default, set this option to true for sequential uploads.
| chooseIcon | ui-icon-plusthick | String | The icon of choose button
| uploadIcon | ui-icon-arrowreturnthick-1-n | String | The icon of upload button
| cancelIcon | ui-icon-cancel | String | The icon of cancel button
| chooseButtonTitle | null | String | Native title tooltip for choose button
| uploadButtonTitle | null | String | Native title tooltip for upload button
| cancelButtonTitle | null | String | Native title tooltip for cancel button
| onAdd | null | String | Callback to execute before adding a file.
| validateContentType | false | Boolean | Whether content type validation should be performed, based on the types defined in the accept attribute. Default is false.
| virusScan | false | Boolean | Whether virus scan should be performed. Default is false.
| maxChunkSize | 0 | Long | To upload large files in smaller chunks, set this option to a preferred maximum chunk size. If set to 0 (default), null or undefined, or the browser does not support the required Blob API, files will be uploaded as a whole. Only works in "advanced" mode.
| maxRetries | 30 | Integer | Only for chunked file upload: Amount of retries when upload get´s interrupted due to e.g. unstable network connection. 
| retryTimeout | 1000 | Integer | Only for chunked file upload: (Base-)Timeout in milliseconds to wait until the next retry. It is multiplied with the retry count. (first retry: retryTimeout * 1, second retry: retryTimeout *2, ...)  

## Getting started with FileUpload
FileUpload engine on the server side can either be servlet 3.0 or commons fileupload. PrimeFaces
selects the most appropriate uploader engine by detection and it is possible to force one or the other
usign an **optional** configuration param.

```xml
<context-param>
    <param-name>primefaces.UPLOADER</param-name>
    <param-value>auto|native|commons</param-value>
</context-param>
```
**auto**: This is the default mode and PrimeFaces tries to detect the best method by checking the
runtime environment, if JSF runtime is at least 2.2 native uploader is selected, otherwise commons.

**native:** Native mode uses servlet 3.x Part API to upload the files and if JSF runtime is less than 2.2
and exception is being thrown.

**commons**: This option chooses commons fileupload regardless of the environment, advantage of
this option is that it works even on a Servlet 2.5 environment.

If you have decided to choose commons fileupload, it requires the following filter configration in
your web deployment descriptior.


```xml
<filter>
    <filter-name>PrimeFaces FileUpload Filter</filter-name>
    <filter-class>
    org.primefaces.webapp.filter.FileUploadFilter
    </filter-class>
</filter>
<filter-mapping>
    <filter-name>PrimeFaces FileUpload Filter</filter-name>
    <servlet-name>Faces Servlet</servlet-name>
</filter-mapping>
```
Note that the servlet-name should match the configured name of the JSF servlet which is Faces
Servlet in this case. Alternatively you can do a configuration based on url-pattern as well.

## Simple File Upload
Simple file upload mode works in legacy mode with a file input whose value should be an
UploadedFile instance. Ajax uploads are not supported in simple upload.

```xhtml
<h:form enctype="multipart/form-data">
    <p:fileUpload value="#{fileBean.file}" mode="simple" />
    <p:commandButton value="Submit" ajax="false"/>
</h:form>
```
```java
import org.primefaces.model.file.UploadedFile;
public class FileBean {
    private SingleUploadedFile file;
    //getter-setter
}
```
Enable skinSimple option to style the simple uploader to have a themed look that works the same
across different environments.

## Advanced File Upload
`listener` is the way to access the uploaded files in this mode. When a file is uploaded,
defined `listener` is processed with a `FileUploadEvent` as the parameter.

```xhtml
<p:fileUpload listener="#{fileBean.handleFileUpload}" />
```
```java
public class FileBean {
    public void handleFileUpload(FileUploadEvent event) {
        SingleUploadedFile file = event.getFile();
        //application code
    }
}
```

## Multiple Uploads
Multiple uploads can be enabled using the multiple attribute so that multiple files can be selected
from browser dialog. Multiple uploads are not supported in legacy browsers. In advanced mode, it does not send all files in one request, but always
uses a new request for each file.

```xhtml
<p:fileUpload listener="#{fileBean.handleFileUpload}" multiple="true" />
```

However, in simple mode, it is possible to get all updated files at once via the `UploadedFiles` model:
```xhtml
<p:fileUpload value="#{fileUploadView.files}" multiple="true" mode="simple" />
<p:commandButton value="Submit" action="#{fileUploadView.upload}" />
```

```java
@Named
@RequestScoped
public class FileUploadView {

    private UploadedFiles files;

    public UploadedFiles getFiles() {
        return files;
    }

    public void setFile(UploadedFiles files) {
        this.files = files;
    }

    public void upload() {
        if (files != null) {
            for (UploadedFile f : files.getFiles()) {
                FacesMessage message = new FacesMessage("Successful", f.getFileName() + " is uploaded.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        }
    }
}
```

## Auto Upload
Default behavior requires users to trigger the upload process, you can change this way by setting
auto to true. Auto uploads are triggered as soon as files are selected from the dialog.

```xhtml
<p:fileUpload listener="#{fileBean.handleFileUpload}" auto="true" />
```
## Partial Page Update
After the fileUpload process completes you can use the PrimeFaces PPR to update any component
on the page. FileUpload is equipped with the update attribute for this purpose. Following example
displays a "File Uploaded" message using the growl component after file upload.

```xhtml
<p:fileUpload listener="#{fileBean.handleFileUpload}" update="msg" />
<p:growl id="msg" />
```
```java
public class FileBean {
    public void handleFileUpload(FileUploadEvent event) {
        //add facesmessage to display with growl
        //application code
    }
}
```
## File Filters
Users can be restricted to only select the file types you’ve configured, example below demonstrates
how to accept images only.

```xhtml
<p:fileUpload listener="#{fileBean.handleFileUpload}" allowTypes="/(\.|\/)(gif|jpe?g|png)$/"/>
```
## Size Limit
Most of the time you might need to restrict the file upload size for a file, this is as simple as setting
the sizeLimit configuration. Following fileUpload limits the size to 1000 bytes for each file.

```xhtml
<p:fileUpload listener="#{fileBean.handleFileUpload}" sizeLimit="1000" />
```

## File Limit
FileLimit restricts the number of maximum files that can be uploaded.

```xhtml
<p:fileUpload listener="#{fileBean.handleFileUpload}" fileLimit="3" />
```
## Validation Messages
_invalidFileMessage_ , _invalidSizeMessage_ and _fileLimitMessage_ options are provided to display
validation messages to the users. Similar to the FacesMessage message API, these message define
the summary part, the detail part is retrieved from the _messageTemplate_ option where default value
is “{name} {size}”.

## Skinning
FileUpload resides in a container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes

| Class | Applies | 
| --- | --- | 
| .ui-fileupload | Main container element
| .ui-fileupload-buttonbar | Button bar
| .ui-fileupload-choose | Browse button
| .ui-fileupload-upload | Upload button
| .ui-fileupload-cancel | Cancel button
| .ui-fileupload-content | Content container

## Browser Compatibility
Advanced uploader is implemented with HTML5 and provides far more features compared to single
version. For legacy browsers that do not support HMTL5 features like canvas or file api, fileupload
uses graceful degradation so that iframe is used for transport, detailed file information is not shown
and a gif animation is displayed instead of progress bar. It is suggested to offer simple uploader as a
fallback.

## Filter Configuration

Filter configuration is required if you are using commons uploader only. Two configuration options
exist, threshold size and temporary file upload location.

| Parameter Name | Description |
| --- | --- |
| thresholdSize | Maximum file size in bytes to keep uploaded files in memory. If a file exceeds this limit, it’ll be temporarily written to disk.
| uploadDirectory | Disk repository path to keep temporary files that exceeds the threshold size. By default it is System.getProperty("java.io.tmpdir")

An example configuration below defined thresholdSize to be 50kb and uploads to users temporary
folder.

```xml
<filter>
    <filter-name>PrimeFaces FileUpload Filter</filter-name>
    <filter-class>org.primefaces.webapp.filter.FileUploadFilter</filter-class>
    <init-param>
        <param-name>thresholdSize</param-name>
        <param-value>51200</param-value>
    </init-param>
    <init-param>
        <param-name>uploadDirectory</param-name>
        <param-value>/Users/primefaces/temp</param-value>
    </init-param>
</filter>
```
**Note** that uploadDirectory is used internally, you always need to implement the logic to save the file
contents yourself in your backing bean.

## Chunking and Resume
FileUpload supports chunked fileupload in advanced-mode using `maxChunkSize` attribute.

Chunked file upload comes with following restrictions:
1. It is only supported for `mode="advanced"`

### Resuming chunked file uploads
FileUpload is able to resume uploads that have been canceled (e.g user abort, lost of connection etc.) At first, you'll need to enable chunking and add this servlet:
```xml
<servlet>
    <servlet-name>FileUpload Resume Servlet</servlet-name>
    <servlet-class>org.primefaces.webapp.FileUploadChunksServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>FileUpload Resume Servlet</servlet-name>
    <url-pattern>/file/resume/</url-pattern>
</servlet-mapping>
```

> You're free to choose `url-pattern` mapping, as long it doesn't conflict with an existing page

### Deleting aborted chunked uploads
For Servlet 3.0 and later versions, uploaded files are automatically removed from the internal 
upload directory after the request is destroyed.

If you're running a Servlet 2.5 container, you'll need to add the following listener to your web.xml:
```xml
<listener>
	<listener-class>org.primefaces.webapp.UploadedFileCleanerListener</listener-class>
</listener>
```

Chunks file are put either into directory from Apache Commons or Servlet 3.0, if not defined then into internal temporary upload directory [ServletContext.TMP_DIR](https://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html#TEMPDIR). They get removed:
1. after the last chunk is uploaded and the merged file is created 
2. when the user aborts the upload.

Though it is recommended to run a cron-job that deletes incomplete uploaded files.

## More secure file upload

### Introduction 

File uploads per se introduce some security risks, for best practices you should consult OWASP's recommendations: https://www.owasp.org/index.php/Unrestricted_File_Upload

### Measures

Here are some measures that can be taken into account when using PrimeFaces's `fileUpload` component:
1. Consider **limiting the size** of uploaded files. As of PrimeFaces 6.2 this will be double-checked at server side as well: `p:fileUpload sizeLimit="1024"`. See https://github.com/primefaces/primefaces/issues/3290.
2. Consider **restricting file names** of uploaded files. As of PrimeFaces 7.0 this will be double-checked at server side as well: `p:fileUpload allowTypes="/(\.|\/)(gif|jpe?g|png)$/"`. See https://github.com/primefaces/primefaces/issues/2791.
3. Consider **enabling content type validation**. This feature has been introduced with PrimeFaces 7.0 and can be used by combining the `accept` and `validateContentType` attributes: `p:fileUpload accept="image/*" validateContentType="true"`. For reliable content type validation we recommend to use Apache Tika, which will be picked up automatically if available in classpath. See https://github.com/primefaces/primefaces/issues/4244.
4. Consider **enabling virus scanning**. This feature has been introduced with PrimeFaces 7.0 and can be enabled with `p:fileUpload virusScan="true"`. See https://github.com/primefaces/primefaces/issues/4256.
   * **Built-in implementation**: You may either make use of PrimeFaces' basic built-in implementation, that just searches for the file's hash at VirusTotal. Therefore you have to configure accordingly the context param `primefaces.virusscan.VIRUSTOTAL_KEY` in `web.xml`; a key can be obtained for free at [VirusTotal](https://www.virustotal.com/#/join-us). 
   * **Built-in implementation**: ClamAV Daemon which can send a file over TCP to a running ClamAV service in your network. You have to configure the host/port context params `primefaces.virusscan.CLAMAV_HOST` and `primefaces.virusscan.CLAMAV_PORT` in `web.xml`; More information at [ClamAV API](https://linux.die.net/man/8/clamd). 
   * **Custom implementation**: Or if more sophisticated virus scanning is required, you can just drop in your custom service provider implementation that will be picked up automatically once available in classpath. In your custom implementation you may leverage your system's virus scanner by using its appropriate API for example.

      * Implementation skeleton

        ```java
        public class CustomVirusScanner implements org.primefaces.virusscan.VirusScanner {
        
            @Override
            public boolean isEnabled() {
                // maybe read some config here
                return true;
            }

            @Override
            public void scan(UploadedFile file) throws VirusException {
                // call the virus scanner's API here
                if (virusDetected) {
                    throw new VirusException();
                }
            }
        }

      * Service provider registration: 
      
      To register the service provider just place a file named `org.primefaces.virusscan.VirusScanner` in the `META-INF/services` directory within your JAR file:

        ```
        com.example.CustomVirusScanner 
        ```

   * **Multiple implementations**: If more than one service provider is available in classpath, all of them will be consulted and must give the green light.

## Client Side API
Widget: _PrimeFaces.widget.SimpleFileUpload_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Shows file chooser dialog.

Widget: _PrimeFaces.widget.FileUpload_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Shows file(s) chooser dialog.
