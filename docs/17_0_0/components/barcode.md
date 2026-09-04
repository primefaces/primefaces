# Barcode

Barcode component is used to display various barcode formats.

## Getting started with Barcode
Barcode type should be provided along with the value to display. Supported formats are;

- int2of5
- codabar
- code39
- code128
- ean8
- ean13
- upca
- postnet
- pdf417
- datamatrix
- qr

```xhtml
<p:barcode value="0123456789" type="int2of5" />
```

Value can also be retrieved from a backend value.

```xhtml
<p:barcode value="#{bean.barcodeValue}" type="int2of5" />
```
## Format
Default display format is _svg_ and other possible option is _png_. In case the client browser does not
support svg e.g. IE8, barcode automatically chooses png format.

```xhtml
<p:barcode value="#{bean.barcodeValue}" type="int2of5" format="png" />
```
## Orientation
In order to change the orientation, choose the angle from the 4 pre-defined values.

```xhtml
<p:barcode value="#{bean.barcodeValue}" type="int2of5" orientation="90"/>
```
## Dependencies
Barcode component uses **Okapi** library underneath to generate barcodes. The following version is supported officially.

```xml
<dependency>
    <groupId>uk.org.okapibarcode</groupId>
    <artifactId>okapibarcode</artifactId>
    <version>0.5.0</version>
</dependency>
```