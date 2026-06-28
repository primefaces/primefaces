# Signature

Signature is used to draw a signature as an input. Various options such as background color,
foreground color, thickness are available for customization. Signature also supports touch enabled
devices and legacy browsers without canvas support.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Signature-1.html)

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

## Typing in a Signature
Instead of drawing a signature, you can also type it in.  Just focus on the signature and type.

```xhtml
<p:signature style="width:400px;height:200px" value="#{signatureView.value}" textValue="#{signatureView.textValue}" fontSize="50" fontFamily="Brush Script MT, cursive" />
```

```java
public class SignatureView {
    private String value;
    private String textValue;

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getTextValue() {
        return textValue;
    }
    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }
}
```

## Save as PNG
Optionally you can save the signature as a PNG image. When submitting the form, the base64 value is passed to the backing bean but this is a "write-only" value so it can only be passed to the backing bean, not read from it.

```xhtml
<p:signature style="width:400px;height:200px" value="#{signatureView.value}" base64Value="#{signatureView.base64Value}" />
```

```java
public class SignatureView {
    private String value;
    private String base64Value;

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getBase64Value() {
        return base64Value;
    }
    public void setBase64Value(String base64Value) {
        this.base64Value = base64Value;
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

## Client Side API
Widget: _PrimeFaces.widget.Signature_


| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |
| createSignatureFromText(text) | text | void | Creates an SVG signature from the given text |

