# PhotoCam

PhotoCam is used to take photos with webcam and send them to the JSF backend model.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_primefaces.primefaces.widget.photocam-1.html)

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
device | false | String | Suggests a video input device such as 'user' (aka font camera) or 'environment' (aka rear camera).
onCameraError | false | String | Client side callback executed if the camera has an error.

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

## Video input device ids

The tag attribute `device` allows the suggestion of a device to be used by the internal camera engine to retrieve images. If it is `null` (default)
the client-side operational system & browser will choose the preferred device to use with. Usually, in mobile devices the value `"user"`
will be resolved as the front camera and `"environment"` will be resolved as the rear camera.

A specific device can be also addressed. For this purpose, the PhotoCam widget is shipped with a `getAvailableDevices()` utility to retrieve the browser native 
`InputDeviceInfo` objects. These objects can provide the required `deviceId` hash for each video input device attached to the system.
The code below shows how to fill this information into a HTML select dropdown:

```javascript
    function populateDeviceMenu() {
        var photoCam = PF('pc');
        var deviceSelector = document.querySelector("select");
        var availableDevices = photoCam.getAvailableDevices();
        if (availableDevices) {
            availableDevices.then(devices => devices.forEach(device => {
                        var option = document.createElement("option");
                        option.text = device.label;
                        option.value = device.deviceId;
                        deviceSelector.appendChild(option);
                    })
                );
        } else {
            console.log("no devices available");
        }
    }
    
    populateDeviceMenu();

```

## Error handling

The default error handling is a browser native alert() popup prompting the user about some low level / browser / device undesired event.
Lucky, `onCameraError` fallback can be defined to indicate a custom handler to deal with these errors. For instance:

```javascript
    function myCustomErrorHandler(errorObj) {
        console.log("error", errorObj);
    }
```
```xhtml
    <p:photoCam widgetVar="photoCam" onCameraError="myCustomErrorHandler(errorObj);"/>
```
will swept to console any error thrown by the PhotoCam underlying engine.

## Browser Compatibility 

This component is strongly dependent of browser / operational system faculties and constraints. Some of known restrictions are listed below:

- Navigators like Google Chrome (version 47 and later) require secure origins (HTTPS or LOCALHOST) to activate media devices functionality. Thus, unless you are accessing the page from LOCALHOST, PhotoCam is likely to do not work in a plain HTTP call. More details can be found here: https://developers.google.com/web/updates/2015/10/chrome-47-webrtc
- Third-party browsers on iOS devices (Iphone, Ipad, etc) do not have the same access to media devices as the Apple native browser Safari has. Thus, on an iOS device, PhotoCam should works fine on Safari but not on Chrome or Firefox (or other non-Safari navigator). See http://www.openradar.me/33571214 for any updates about this. Note that this restriction is applied only for iOS devices. In general, third-party browsers running on OSx systems (mackbooks, iMacs and so on) don't have this same restriction.
- Since Adobe Flash support is becoming scarse, PhotoCam is likely to do not run on Microsoft Internet Explorer 11 or below. Other legacy browsers are also not likely to be functional.

