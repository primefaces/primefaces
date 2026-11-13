/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.showcase.view.multimedia;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class GraphicImageView {

    public StreamedContent getGraphicText() {
        return DefaultStreamedContent.builder()
                .contentType("image/png")
                .stream(() -> {
                    try {
                        BufferedImage bufferedImg = new BufferedImage(100, 25, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2 = bufferedImg.createGraphics();
                        g2.drawString("This is a text", 0, 10);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImg, "png", os);
                        return new ByteArrayInputStream(os.toByteArray());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .build();
    }

    /**
     * Streamed: the image is loaded in a separate request (stream="true", the default).
     */
    public StreamedContent getImage() {
        return DefaultStreamedContent.builder()
                .contentType("image/jpeg")
                .stream(() -> readImage("nature5.jpg"))
                .build();
    }

    /**
     * Rendered as DataURI (stream="false"): the content type is determined from the bytes.
     */
    public byte[] getImageAsByteArray() throws IOException {
        try (InputStream is = readImage("nature6.jpg")) {
            return is.readAllBytes();
        }
    }

    /**
     * Written directly to the response, without buffering it in memory first.
     */
    public StreamedContent getImageWithoutBuffering() {
        return DefaultStreamedContent.builder()
                .contentType("image/jpeg")
                .writer(os -> {
                    try (InputStream is = readImage("nature7.jpg")) {
                        is.transferTo(os);
                    }
                    catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .build();
    }

    private static InputStream readImage(String name) {
        return FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/demo/images/nature/" + name);
    }
}
