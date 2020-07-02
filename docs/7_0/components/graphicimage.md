# GraphicImage

GraphicImage extends standard JSF graphic image component with the ability of displaying binary
data like an inputstream. Main use cases of GraphicImage is to make displaying images stored in
database or on-the-fly images easier. Legacy way to do this is to come up with a Servlet that does
the streaming, GraphicImage does all the hard work without the need of a Servlet.

## Info

| Name | Value |
| --- | --- |
| Tag | graphicImage
| Component Class | org.primefaces.component.graphicimage.GraphicImage
| Component Type | org.primefaces.component.GraphicImage
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.GraphicImageRenderer
| Renderer Class | org.primefaces.component.graphicimage.GraphicImageRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Binary data to stream or context relative path.
alt | null | String | Alternate text for the image
url | null | String | Alias to value attribute
width | null | String | Width of the image
height | null | String | Height of the image
title | null | String | Title of the image
dir | null | String | Direction of the text displayed
lang | null | String | Language code
ismap | false | Boolean | Specifies to use a server-side image map
usemap | null | String | Name of the client side map
style | null | String | Style of the image
styleClass | null | String | Style class of the image
onclick | null | String | onclick dom event handler
ondblclick | null | String | ondblclick dom event handler
onkeydown | null | String | onkeydown dom event handler
onkeypress | null | String | onkeypress dom event handler
onkeyup | null | String | onkeyup dom event handler
onmousedown | null | String | onmousedown dom event handler
onmousemove | null | String | onmousemove dom event handler
onmouseout | null | String | onmouseout dom event handler
onmouseover | null | String | onmouseover dom event handler
onmouseup | null | String | onmouseup dom event handler
cache | true | String | Enables/Disables browser from caching the image
name | null | String | Name of the image.
library | null | String | Library name of the image.
stream | true | Boolean | Defines if the image is streamed or rendered directly as data uri / base64 with ViewScoped support.

## Getting started with GraphicImage
GraphicImage requires an _org.primefaces.model.StreamedContent_ content as it’s value for dynamic
images. StreamedContent is an interface and PrimeFaces provides a built-in implementation called
_DefaultStreamedContent_. Following examples loads an image from the classpath.

```xhtml
<p:graphicImage value="#{imageBean.image}" />
```
```java
public class ImageBean {
    private StreamedContent image;

    public DynamicImageController() {
        InputStream stream = this.getClass().getResourceAsStream("barcalogo.jpg");
        image = new DefaultStreamedContent(stream, "image/jpeg");
    }
    public StreamedContent getImage() {
        return this.image;
    }
}
```
DefaultStreamedContent gets an inputstream as the first parameter and mime type as the second.


In a real life application, you can create the inputstream after reading the image from the database.
For example _java.sql.ResultsSet_ API has the getBinaryStream() method to read blob files stored in
database.

## Displaying Charts with JFreeChart
See static images section at chart component for a sample usage of graphicImage with jFreeChart.

## Displaying a Barcode
Similar to the chart example, a barcode can be generated as well. This sample uses barbecue project
for the barcode API.

```xhtml
<p:graphicImage value="#{backingBean.barcode}" />
```
```java
public class BarcodeBean {
    private StreamedContent barcode;

    public BackingBean() {
        try {
            File barcodeFile = new File("dynamicbarcode");
            BarcodeImageHandler.saveJPEG(
            BarcodeFactory.createCode128("PRIMEFACES"), barcodeFile);
            barcode = new DefaultStreamedContent(
            new FileInputStream(barcodeFile), "image/jpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public BarcodeBean getBarcode() {
        return this.barcode;
    }
}
```
## Displaying Regular Images
As GraphicImage extends standard graphicImage component, it can also display regular non
dynamic images just like standard graphicImage component using name and optional library.

```xhtml
<p:graphicImage name="barcalogo.jpg" library="yourapp" />
```
## How It Works
Default dynamic image display works as follows;

- Streamed content is put in http session with an encrypted key
- This key is appended to the image url that points to JSF resource handler.


- Custom PrimeFaces ResourceHandler gets the key from the url, decrypts it to get the instance of
    StreamedContent from session, evaluates the content and streams it to client. Finally key is
    removed from http session.

As a result there will be 2 requests to display an image, at first browser will make a request to load
the page initially and then another one to the dynamic image url that points to JSF resource handler.
Note that you cannot use viewscope beans in this way as they are not available in resource loading
request. See Data URI section below for an alternative to support view scope.

You can pass request parameters to the graphicImage via f:param tags, as a result the actual request
rendering the image can have access to these values. This is extremely handy to display dynamic
images if your image is in a data iteration component like datatable or ui:repeat.

## ViewScope Support via Data URI
Setting stream attribute to false uses an alternative approach by converting the value to base64 and
displays the image via data uri. In this approach, only one request is required so view scope is
supported.

## StreamedContent
There are two StreamedContent implementations out of the box; DefaultStreamedContent is not
uses an InputStream and not serializable whereas the serializable ByteArrayContent uses a byte
array.
