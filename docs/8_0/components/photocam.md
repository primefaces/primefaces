# PhotoCam

PhotoCam is used to take photos with webcam and send them to the JSF backend model.

## Info

| Name | Value |
| --- | --- |
| Tag | photoCam
| Component Class | org.primefaces.component.photocam.PhotoCam
| Component Type | org.primefaces.component.PhotoCam
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.PhotoCamRenderer
| Renderer Class | org.primefaces.component.photocam.PhotoCamRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component than can be either an EL expression of a literal text
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | 0 | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method binding expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method binding expression that refers to a method for handling a valuechangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
process | null | String | Identifiers of components to process during capture.
update | null | String | Identifiers of components to update during capture.
listener | null | MethodExpr | Method expression to listen to capture events.
width | 320 | Integer | Width of the camera viewport.
height | 240 | Integer | Height of the camera viewport.
photoWidth | 320 | Integer | Width of the captured photo, defaults to width.
photoHeight | 240 | Integer | Height of the captured photo, defaults to height.
format | jpeg | Boolean | Format of the image, valid values are "jpeg" default and png.
jpegQuality | 90 | Integer | Quality of the image between 0 and 100 when the format is jpeg, default value is 90.
forceFlash | false | Boolean | Enables always using flash fallback even in an HTML5 environment.

## Getting started with PhotoCam
Capture is triggered via client side api’s _capture_ method. Also a method expression is necessary to
invoke when an image is captured. Sample below captures an image and saves it to a directory.

```xhtml
<h:form>
    <p:photoCam widgetVar="pc" listener="#{photoCamBean.oncapture}" update="photos"/>
    <p:commandButton type="button" value="Capture" onclick="PF('pc').capture()"/>
</h:form>
```
```java
public class PhotoCamBean {
    public void oncapture(CaptureEvent captureEvent) {
        byte[] data = captureEvent.getData();
        ServletContext servletContext = (ServletContext)
        FacesContext.getCurrentInstance().getExternalContext().getContext();
        String newFileName = servletContext.getRealPath("") + File.separator + "photocam" + File.separator + "captured.png";
        FileImageOutputStream imageOutput;
        try {
            imageOutput = new FileImageOutputStream(new File(newFileName));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
        }
        catch(Exception e) {
            throw new FacesException("Error in writing captured image.");
        }
    }
}
```