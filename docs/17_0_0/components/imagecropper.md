# ImageCropper

ImageCropper allows cropping a certain region of an image. A new image is created containing the
cropped area and assigned to a CroppedImage instanced on the server side.

## Getting started with the ImageCropper
ImageCropper is an input component and image to be cropped is provided via the _image_ attribute.
The cropped area of the original image is used to create a new image, this new image can be
accessed on the backing bean by setting the value attribute of the image cropper. Assuming the
image is at %WEBAPP_ROOT%/campnou.jpg

```xhtml
<p:imageCropper value="#{cropper.croppedImage}" image="/campnou.jpg" />
```
```java
public class Cropper {
    private CroppedImage croppedImage;
    //getter and setter
}
```
_org.primefaces.model.CroppedImage_ belongs a PrimeFaces API and contains handy information
about the crop process. Following table describes CroppedImage properties.

| Property | Type | Description
| --- | --- | --- |
originalFileName | String | Name of the original file that’s cropped. If using StreamedContent it will be the name set on the stream or `unknown.png` if NULL.
bytes | byte[] | Contents of the cropped area as a byte array
left | int | Left coordinate
right | int | Right coordinate
width | int | Width of the cropped image
height | int | Height of the cropped image

## External Images
ImageCropper has the ability to crop external images as well.

```xhtml
<p:imageCropper value="#{cropper.croppedImage}" image=" http://primefaces.prime.com.tr/en/images/schema.png">
</p:imageCropper>
```

> :warning **When the `image` attribute resolves to an `http`/`https` URL, PrimeFaces opens a server-side connection to fetch the image for cropping. If this value can be influenced by end-user input (e.g. `image="#{bean.userProvidedUrl}"`), the application is responsible for validating and allowlisting the URL before it reaches the component. Binding an unvalidated user-supplied URL creates a Server-Side Request Forgery (SSRF) risk, as the server will fetch whichever URL is provided — including internal network addresses.**

## Context Relative Path
For local images, ImageCropper always requires the image path to be context relative. So to
accomplish this simply just add slash ("/path/to/image.png") and imagecropper will recognize it at
%WEBAPP_ROOT%/path/to/image.png. Action url relative local images are not supported.

## Dynamic Images
A dynamic image can be used with ImageCropper which requires _org.primefaces.model.StreamedContent_ `image` as it’s value  
StreamedContent is an interface and PrimeFaces provides a built-in implementation called
_DefaultStreamedContent_. Please see our core documentation about it: [Dynamic Content Streaming / Rendering](/core/dynamiccontent.md)

## Initial Coordinates
By default, user action is necessary to initiate the cropper area on an image, you can specify an
initial area to display on page load using _initialCoords_ option in _x,y,w,h_ format.

```xhtml
<p:imageCropper value="#{cropper.croppedImage}" image="/campnou.jpg" initialCoords="225,75,300,125"/>
```
## Boundaries
minSize and maxSize attributes are control to limit the size of the area to crop.

```xhtml
<p:imageCropper value="#{cropper.croppedImage}" image="/campnou.jpg" minSize="50,100" maxSize="150,200"/>
```
## Saving Images
Below is an example to save the cropped image to file system.

```xhtml
<p:imageCropper value="#{cropper.croppedImage}" image="/campnou.jpg" />
<p:commandButton value="Crop" action="#{myBean.crop}" />
```

```java
public class Cropper {
    private CroppedImage croppedImage;
    //getter and setter

    public String crop() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String newFileName = servletContext.getRealPath("") + File.separator +
        "ui" + File.separator + "barca" + File.separator+ croppedImage.getOriginalFileName() + "cropped.jpg";
        
        FileImageOutputStream imageOutput;
        try {
            imageOutput = new FileImageOutputStream(new File(newFileName));
            imageOutput.write(croppedImage.getBytes(), 0,
            croppedImage.getBytes().length);
            imageOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
```