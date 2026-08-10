# PhotoCam

PhotoCam is used to take photos with webcam and send them to the Faces backend model.

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
- Adobe Flash support ended in January 2021, PhotoCam is not likely to run on Microsoft Internet Explorer 11 or below. Other legacy browsers are also not likely to be functional.

