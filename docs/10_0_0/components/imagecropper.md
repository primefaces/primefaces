# ImageCropper

ImageCropper allows cropping a certain region of an image. A new image is created containing the
cropped area and assigned to a CroppedImage instanced on the server side.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/primefaces.widget.imagecropper.html)

## Info

| Name | Value |
| --- | --- |
| Tag | imageCropper
| Component Class | org.primefaces.component. imagecropper.ImageCropper
| Component Type | org.primefaces.component.ImageCropper
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ImageCropperRenderer
| Renderer Class | org.primefaces.component.imagecropper.ImageCropperRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component than can be either an EL expression of a literal text
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method binding expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method binding expression that refers to a method for handling a valuchangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
image | null | Object | Binary data to stream or context relative path.
alt | null | String | Alternate text of the image.
aspectRatio | null | Double | Aspect ratio of the cropper area.
minSize | null | String | Minimum size of the cropper area (width,height).
maxSize | null | String | Maximum size of the cropper area (width,height).
initialCoords | null | String | Initial coordinates of the cropper area (x, y, width,height).
boxWidth | 0 | Integer | Maximum box width of the cropping area.
boxHeight | 0 | Integer | Maximum box height of the cropping area.
sizeLimit | 10485760 | Long | Maximum number of bytes the image.
responsive | true | Boolean | Re-render the cropper when resizing the window.
guides | true | Boolean | Show the dashed lines in the crop box.
cache | true | Boolean | Controls browser caching mode of the resource. Default is true.
viewMode | 1 | Integer | Define the view mode of the cropper. If you set viewMode to 0, the crop box can extend outside the canvas, while a value of 1, 2 or 3 will restrict the crop box to the size of the canvas. A viewMode of 2 or 3 will additionally restrict the canvas to the container. Note that if the proportions of the canvas and the container are the same, there is no difference between 2 and 3.
zoomOnTouch | true | Boolean | Enable to zoom the image by dragging touch. Default is true.
zoomOnWheel | true | Boolean | Enable to zoom the image by wheeling mouse. Default is true.

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
originalFileName | String | Name of the original file that’s cropped
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