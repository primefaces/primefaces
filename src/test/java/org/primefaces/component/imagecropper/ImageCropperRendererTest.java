/* 
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.imagecropper;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.primefaces.component.imagecropper.ImageCropper;
import org.primefaces.component.imagecropper.ImageCropperRenderer;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class ImageCropperRendererTest {
    
    private FacesContext context;
    private ExternalContext externalContext;

    @BeforeEach
    public void setup() {
        context = mock(FacesContext.class);
        externalContext = mock(ExternalContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getRealPath(anyString())).thenReturn("src/test/resources");
    }
    
    @Test
    public void checkStreamIsNullButImageIsGiven() {
        ImageCropper cropper = new ImageCropper();
        cropper.setImage("org/primefaces/images/nature/nature1.jpg");
        ImageCropperRenderer renderer = new ImageCropperRenderer();
        Object value = renderer.getConvertedValue(context, cropper, "1_100_1_100");
        Assertions.assertNotNull(value);
    }
    
    @Test
    public void checkImageIsNullButStreamIsGiven() {
        ImageCropper cropper = new ImageCropper();
        StreamedContent stream = DefaultStreamedContent.builder().contentType("image/png").stream(() -> {
            try {
                BufferedImage bufferedImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = bufferedImg.createGraphics();
                g2.drawString("This is a text", 50, 50);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(bufferedImg, "png", os);
                return new ByteArrayInputStream(os.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).build();
        cropper.setImage(stream);
        ImageCropperRenderer renderer = new ImageCropperRenderer();
        Object value = renderer.getConvertedValue(context, cropper, "1_100_1_100");
        Assertions.assertNotNull(value);
    }
    
    @Test
    public void checkImageAndStreamAreNull() {
        ImageCropper cropper = new ImageCropper();
        try {
            ImageCropperRenderer renderer = new ImageCropperRenderer();
            renderer.getConvertedValue(context, cropper, "1_100_1_100");
            Assertions.fail("should thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if(!"'image' must be either an String relative path or a StreamedObject.".equals(message)) {
                Assertions.fail("should thrown IllegalArgumentException with message: " + message);
            }
        } catch (Exception e) {
            Assertions.fail("should thrown IllegalArgumentException");
        }
    }
    
}
