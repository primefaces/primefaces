# Signature

Signature is used to draw a signature as an input. Various options such as background color,
foreground color, thickness are available for customization. Signature also supports touch enabled
devices and legacy browsers without canvas support.

## Info

| Name | Value |
| --- | --- |
| Tag | signature
| Component Class | org.primefaces.component.signature.Signature
| Component Type | org.primefaces.component.Signature
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.SignatureRenderer
| Renderer Class | org.primefaces.component.signature.SignatureRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component than can be either an EL expression of a literal text
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | Boolean value that specifies the lifecycle phase the valueChangeEvents should be processed, when true the events will be fired at "apply request values", if immediate is set to false, valueChange Events are fired in "process validations" phase
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method binding expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method binding expression that refers to a method for handling a valuchangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget
backgroundColor | #ffffff | String | Background color as hex value
color | #000000 | String | Foreground color as hex value
thickness | 2 | Integer | Thickness of the lines
style | null | String | Inline style of the component
styleClass | null | String | Style class of the component.
readonly | false | Boolean | When enabled, signature is used for display purposes only.
guideline | false | Boolean | Adds a guideline when enabled
guidelineColor | #a0a0a0 | String | Color of the guideline
guidelineOffset | 25 | String | Offset of guideline from bottom
guidelineIndent | 10 | Integer | Guide line indent from the edges
onchange | null | String | Client side callback to execute when signature changes.
base64Value | null | String | Write-only value used to pass the value in base64 to backing bean

## Getting started with Signature
Value is interpreted as JSON so at backing bean should be a string value.

```xhtml
<p:signature style="width:400px;height:200px" value="#{signatureView.value}" />
```

```java
public class SignatureView {
    private String value;

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
```
## Guideline
Guideline adds a horizontal line to indicate the area to sign, attributes such as guidelineColor,
guidelineOffset and guidelineIndent can be used to customize this area.

## Convert to Binary
Signature value is represented as a JSON array at client side and this value is also passed as a java
string to backend bean, however if you need to convert this to a byte[] or write it to an outputStream
following helper class can be used.

```java
import java.awt.BasicStroke; 
import java.awt.Color;
import java.awt.Graphics2D; 
import java.awt.RenderingHints;
import java.awt.image.BufferedImage; 
import java.io.ByteArrayOutputStream;
import java.io.IOException; 
import java.io.OutputStream;
import java.util.ArrayList; 
import java.util.List;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class SigGen {
    private static final String IMAGE_FORMAT = "png";
    private static final int SIGNATURE_HEIGHT = 200; 
    private static final int SIGNATURE_WIDTH = 400;
    /* A point along a line within a signature. */
    private static class Point {
        private int x; private int y;
        public Point(float x, float y) {
            this.x = Math.round(x); this.y = Math.round(y);
        } 
    }


    /** 
     * Extract a signature from its JSON encoding and redraw it as an image.
     * @param jsonEncoding the JSON representation of the signature
     * @param output the destination stream for the image * @throws IOException if a problem writing the signature
     */ 
    public static void generateSignature(String jsonEncoding, OutputStream output) throws IOException { 
        output.write(redrawSignature(extractSignature(jsonEncoding)));
        output.close(); 
    }

    /**
     * Extract the signature lines and points from the JSON encoding.
     * @param jsonEncoding the JSON representation of the signature 
     * @return the retrieved lines and points
     */
    private static List<List<Point>> extractSignature(String jsonEncoding) {
        List<List<Point>> lines = new ArrayList<List<Point>>(); 
        Matcher lineMatcher = Pattern.compile("(\\[(?:,?\\[-?[\\d\\.]+,-?[\\d\\.]+\\])+\\])").matcher(jsonEncoding);
        while (lineMatcher.find()) { 
            Matcher pointMatcher = Pattern.compile("\\[(-?[\\d\\.]+),(-?[\\d\\.]+)\\]").matcher(lineMatcher.group(1));
            List<Point> line = new ArrayList<Point>(); 
            lines.add(line);
            while (pointMatcher.find()) { 
                line.add(new Point(Float.parseFloat(pointMatcher.group(1)),
                Float.parseFloat(pointMatcher.group(2)))); 
            }
        } 
        return lines;
    }
    /** 
     * Redraw the signature from its lines definition.
     * @param lines the individual lines in the signature
     * @return the corresponding signature image * @throws IOException if a problem generating the signature
     */ 
    private static byte[] redrawSignature(List<List<Point>> lines) throws IOException {
        BufferedImage signature = new BufferedImage( SIGNATURE_WIDTH, SIGNATURE_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = (Graphics2D)signature.getGraphics(); 
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, signature.getWidth(), signature.getHeight()); 
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); 
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
        Point lastPoint = null;
        for (List<Point> line : lines) { 
            for (Point point : line) {
                if (lastPoint != null) { 
                    g.drawLine(lastPoint.x, lastPoint.y, point.x, point.y);
                } 
                lastPoint = point;
            } 
            lastPoint = null;
        } 
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(signature, IMAGE_FORMAT, output); 
        ImageIO.write(signature, IMAGE_FORMAT, output);
        return output.toByteArray(); 
    }
}
```