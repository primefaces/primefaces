# FileUpload

FileUpload goes beyond the browser input type="file" functionality and features an HTML5
powered rich solution with graceful degradation for legacy browsers.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.FileUpload-1.html)

## Simple FileUpload
Simple FileUpload mode works with a plain HTML `input type=file`.
AJAX uploads are not supported in simple upload, however AJAX is used to automatically upload the file when using `auto=true`.

You can enable `skinSimple` option to style the simple uploader, to have a themed look, that works the same across different environments.

### Single
In single mode, you can use `UploadedFile` as `value` binding or `FileUploadEvent` via `listener`.

```xhtml
<h:form enctype="multipart/form-data">
    <p:fileUpload value="#{fileUploadView.file}" mode="simple" />
    <p:commandButton value="Submit" ajax="false"/>
</h:form>
```
```java
@Named
@RequestScoped
public class FileUploadView {
    private UploadedFile file;
    //getter-setter

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        //application code
    }
}
```

### Multiple
Multiple file selection and upload can be enabled by setting `multiple=true`.

!> This is not supported by legacy browsers!

In multiple mode, you can use `UploadedFiles` as `value` binding or `FilesUploadEvent` via `listener`,
as all files get uploaded within a single request:

```xhtml
<p:fileUpload value="#{fileUploadView.files}" multiple="true" mode="simple" />
<p:commandButton value="Submit" action="#{fileUploadView.upload}" ajax="false"/>
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

## Advanced FileUpload
Advanced FileUpload provies a more complex UI compared to the Simple FileUpload.

### Single
In single mode, you can use `UploadedFile` as `value` binding or `FileUploadEvent` via `listener`.

```xhtml
<p:fileUpload listener="#{fileUploadView.handleFileUpload}" />
```
```java
@Named
@RequestScoped
public class FileUploadView {
    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        //application code
    }
}
```

### Multiple
Multiple file selection and upload can be enabled by setting `multiple=true`.
As advanced mode does _not_ send all files in one request, you must use the `listener` with `FileUploadEvent`, which will be called multiple times:

```xhtml
<p:fileUpload listener="#{fileUploadView.handleFileUpload}" multiple="true" />
```
```java
@Named
@RequestScoped
public class FileUploadView {
    public void handleFileUpload(FileUploadEvent event) {
        // will be invoked for each uploaded file
    }
}
```

**Attention**: Since the files are send asynchronously in parallel to the server, the backing bean has to be **@RequestScoped**!<br/>
Each file upload request is processed in a separate thread on server side. Special care has to be taken on thread safty.<br/>
Be aware each file upload runs through the entire Jakarta Faces lifecycle which implies that several attributes of the component
(such as `process`, `update`, `oncomplete` and others) are processed or updated for each file. Consider to use `p:remoteCommand`
which is called from client side after _all_ files are uploaded by checking the widgets `files` property. E.g.

```xhtml
<p:remoteCommand name="updateAfterAllFilesUploaded"
    update="..." actionListener="..."/>
<p:fileUpload listener="#{fileBean.handleFileUpload}" multiple="true"
    widgetVar="fileUpload"
    oncomplete="if (!PF('fileUpload').files.length) updateAfterAllFilesUploaded();"/>
```

### Partial Page Update
After the upload process completes, you can use the PrimeFaces PPR to update any component on the page.
FileUpload is equipped with the `update` attribute for this purpose.
Following example displays a "File Uploaded" message using the Growl component after upload:

```xhtml
<p:fileUpload listener="#{fileUploadView.handleFileUpload}" update="msg" />
<p:growl id="msg" />
```
```java
public class FileUploadView {
    public void handleFileUpload(FileUploadEvent event) {
        //add facesmessage to display with growl
        //application code
    }
}
```

### Confirmation Before Upload
You can add a client side callback, if you want a confirmation dialog before the uploads begin.
Any return of `false` from the `onupload` callback will not send the files:

```xhtml
<p:fileUpload listener="#{fileUploadView.handleFileUpload}" onupload="return confirm('Are you sure?')"/>
```

### Validate Files Before Adding
You can add a client side callback, if you want to use a custom function to approve a file before adding using `onAdd`.
For example only add a file if its named exactly `primefaces.pdf`.

```javascript
function onAddFile(file, callback) {
   if (file.name === "primefaces.pdf") {
       // this callback adds the file to the list
       callback.call(this, file);
   }
}
```

```xhtml
<p:fileUpload listener="#{fileUploadView.handleFileUpload}" onAdd="onAddFile(file, callback);"/>
```

### Empty Facet

The empty-facet is rendered when no files are added yet. It's rendered in `div` classed `ui-fileupload-empty`.

```xhtml
<p:fileUpload ...>
    <f:facet name="empty">Drag and drop files to here to upload.</f:facet>
</p:fileUpload>
```

### Header Facet

Use the `header` facet to render custom content in the header, next to the buttons.

```xhtml
<p:fileUpload advanced="true" ...>
    <f:facet name="header">Only image files are allowed</f:facet>
</p:fileUpload>
```

## Auto Upload
Default behavior requires a user to trigger the upload process, you can change this way by setting `auto` to `true`.
Auto uploads are triggered as soon as files are selected from the dialog.

```xhtml
<p:fileUpload listener="#{fileUploadView.handleFileUpload}" auto="true" />
```

## Client Side Validation

You can use [client side validation (CSV)](/core/csv.md) to avoid roundtrips to the server with `p:validateFile` see [Validate File Component](/validators/validateFile.md).
Especially useful if you want to prevent a huge file from being uploaded to the server if its over its size limit.

```xhtml
<p:fileUpload listener="#{fileUploadView.handleFileUpload}" >
   <p:validateFile sizeLimit="10000" />
</p:fileUpload>
```

## File Filters
Users can be restricted to only select the file types you’ve configured, example below demonstrates
how to accept images only.

```xhtml
<p:fileUpload listener="#{fileBean.handleFileUpload}" >
   <p:validateFile allowTypes="/(\.|\/)(gif|jpeg|jpg|png)$/"/>
</p:fileUpload>
```


## File Limit
FileLimit restricts the number of maximum files that can be uploaded.

```xhtml
<p:fileUpload listener="#{fileBean.handleFileUpload}">
   <p:validateFile fileLimit="2" sizeLimit="100" virusScan="true" allowTypes="/(\.|\/)(csv)$/"/>
</p:fileUpload>
```


## Custom Drop Zone
The `dropZone` attribute allows you to specify a component that will serve as a custom drop zone for file uploads. 
When a file is dragged over the specified component, the `ui-state-drag` CSS class is automatically added to the target element and removed when the file is no longer being dragged. This allows you to apply custom styling to indicate when files are being dragged over the drop zone.


## Size Limit
Most of the time you might need to restrict the file upload size for a file, this is as simple as setting
the sizeLimit configuration. Following fileUpload limits the size to 1000 bytes for each file.

```xhtml
<p:fileUpload listener="#{fileBean.handleFileUpload}" >
   <p:validateFile  sizeLimit="1000" />
</p:fileUpload>
```

## Skinning
FileUpload resides in a container element which `style` and `styleClass` options apply. As skinning
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
| .ui-state-drag | Added to main container element when file is dragged over this component
| .ui-fileupload-withdropzone | Added to main container element if a custom drop zone is set

## Browser Compatibility
Advanced uploader is implemented with HTML5 and provides far more features compared to single version.
For legacy browsers, that do not support HMTL5 features like canvas or file api, fileupload uses graceful degradation
(iframe is used for transport, detailed file informations are not shown,  GIF animation instead of progress bar).
It is suggested to offer simple uploader as a fallback.


## Configuration for Tomcat since June 2025 (>= 9.0.106, >= 10.1.42, >= 11.0.8)
> maxPartCount limits the total number of parts in a multi-part request and maxPartHeaderSize limits the size of the headers provided with each part.

See https://tomcat.apache.org/tomcat-11.0-doc/config/http.html#Common_Attributes for additional information.

## Chunking and Resume
FileUpload supports chunked upload using the `maxChunkSize` attribute but only in advanced mode!

### Resuming chunked file uploads
FileUpload is able to resume uploads that have been canceled (e.g. user abort, lost of connection etc.)
At first, you'll need to enable chunking and add this servlet:

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
**Note** that resuming chunked file uploads is not supported with commons uploader.

### Deleting aborted chunked uploads
For Servlet 3.0 and later versions, uploaded files are automatically removed from the internal
upload directory after the request is destroyed.

If you're running a Servlet 2.5 container, you'll need to add the following listener to your web.xml:
```xml
<listener>
	<listener-class>org.primefaces.webapp.UploadedFileCleanerListener</listener-class>
</listener>
```

Chunks file are put either into directory from Servlet 3.0, if not defined then into internal temporary upload directory [ServletContext.TMP_DIR](https://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html#TEMPDIR). They get removed:
1. after the last chunk is uploaded and the merged file is created
2. when the user aborts the upload.

To configure a custom location for uploaded chunks, you can set the `location` attribute in the `multipart-config` element of your `FacesServlet` in `web.xml`:

```xml
<servlet>
    <servlet-name>FacesServlet</servlet-name>
    <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
    <multipart-config>
        <location>/path/to/upload/directory</location>
        <max-file-size>52428800</max-file-size>
        <max-request-size>52428800</max-request-size>
        <file-size-threshold>0</file-size-threshold>
    </multipart-config>
</servlet>
```

If the `location` is not specified in `multipart-config`, the system will default to the temporary directory.

Though it is recommended to run a cron-job that deletes incomplete uploaded files.

## More secure file upload

### Introduction

File uploads per se introduce some security risks, for best practices you should consult OWASP's recommendations: https://www.owasp.org/index.php/Unrestricted_File_Upload

### Measures

Here are some measures that can be taken into account when using PrimeFaces's `fileUpload` component:
1. Consider **limiting the size** of uploaded files. This will be double-checked at server side as well: `p:validateFile sizeLimit="1024"`.
2. Consider **restricting file names** of uploaded files. This will be double-checked at server side as well: `p:validateFile allowTypes="/(\.|\/)(gif|jpeg|jpg|png)$/"`.
3. Consider **enabling content type validation**. This can be used by combining the `contentType` attributes: `p:validateFile allowTypes="/(\.|\/)(gif|jpeg|jpg|png)$/" contentType="true"`.
   For reliable content type validation we recommend to use [Apache Tika](https://tika.apache.org/) or [mime-types](https://github.com/overview/mime-types), which will be picked up automatically if available in classpath.
   If you wish to use your own [FileTypeDetector](https://docs.oracle.com/javase/8/docs/api/java/nio/file/spi/FileTypeDetector.html) or use one which is not registered as a SPI service, then register it in your webapp in `META-INF/services` directory with filename `java.nio.file.spi.FileTypeDetector`.
   Finally, if you need to execute several FileTypeDetector, you can control order of execution over your SPI file.
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
		```

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
clear() | - | void | Clears the currently selected file.

Widget: _PrimeFaces.widget.FileUpload_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
show() | - | void | Shows file(s) chooser dialog.
clear() | - | void | Clears the currently selected file(s).
