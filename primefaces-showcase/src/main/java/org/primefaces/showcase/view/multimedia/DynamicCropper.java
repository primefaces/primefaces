/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Named;

import org.primefaces.model.CroppedImage;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named
@SessionScoped
public class DynamicCropper implements Serializable {

    private final int width = 500;
    private final int height = 350;
    private int numberOfIterations;

    private CroppedImage croppedImage;
    private String newImageName;

    @PostConstruct
    public void init() {
        this.numberOfIterations = 5;
    }

    public StreamedContent getImage() {
        return DefaultStreamedContent.builder()
                .contentType("image/png")
                .stream(() -> {
                    try {
                        BufferedImage image = mandelbrotSet(width, height, numberOfIterations);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", outputStream);
                        return new ByteArrayInputStream(outputStream.toByteArray());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .build();
    }

    public void crop() {
        if (this.croppedImage != null) {
            String imageName = UUID.randomUUID().toString() + ".png";
            setNewImageName(imageName);
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

            Path path = Paths.get(externalContext.getRealPath(""), "resources", "demo", "images", "crop", imageName);

            FileImageOutputStream imageOutput;
            try {
                imageOutput = new FileImageOutputStream(path.toFile());
                imageOutput.write(croppedImage.getBytes(), 0, croppedImage.getBytes().length);
                imageOutput.close();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Cropping finished."));
            }
            catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cropping failed."));
            }
        }

    }

    private BufferedImage mandelbrotSet(int width, int height, int maxIterations) throws IOException {

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final int[] colorBuffer = new int[maxIterations];
        Arrays.setAll(colorBuffer, i -> {
            float h = i / 256.0f;
            float b = i / (i + 8.0f);
            return Color.HSBtoRGB(h, 1, b);
        });

        final int widthBy2 = width / 2;
        final int heightBy2 = height / 2;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                final double re = (j - widthBy2) * 4.0 / width;
                final double im = (i - heightBy2) * 4.0 / width;
                double x = .0, y = .0, rsq = .0;
                int iteration = 0;
                for (; rsq < 4 && iteration < maxIterations; ++iteration) {
                    double newX = x * x - y * y + re;
                    y = 2 * x * y + im;
                    x = newX;
                    rsq = x * x + y * y;
                }
                if (iteration < maxIterations) {
                    result.setRGB(j, i, colorBuffer[iteration]);
                }
                else {
                    result.setRGB(j, i, 0);
                }
            }
        }
        return result;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    public void setNumberOfIterations(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
    }

    public CroppedImage getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(CroppedImage croppedImage) {
        this.croppedImage = croppedImage;
    }

    public String getNewImageName() {
        return newImageName;
    }

    public void setNewImageName(String newImageName) {
        this.newImageName = newImageName;
    }
}
