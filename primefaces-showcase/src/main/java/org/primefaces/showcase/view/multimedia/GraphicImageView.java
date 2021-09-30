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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Named
@RequestScoped
public class GraphicImageView {

    public StreamedContent getGraphicText() {
        try {
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
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public StreamedContent getChart() {
        try {
            return DefaultStreamedContent.builder()
                    .contentType("image/png")
                    .stream(() -> {
                        try {
                            JFreeChart jfreechart = ChartFactory.createPieChart("Cities", createDataset(), true, true, false);
                            File chartFile = new File("dynamichart");
                            ChartUtilities.saveChartAsPNG(chartFile, jfreechart, 375, 300);
                            return new FileInputStream(chartFile);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public StreamedContent getChartWithoutBuffering() {
        try {
            return DefaultStreamedContent.builder()
                    .contentType("image/png")
                    .writer((os) -> {
                        try {
                            JFreeChart jfreechart = ChartFactory.createPieChart("Cities", createDataset(), true, true, false);
                            ChartUtilities.writeChartAsPNG(os, jfreechart, 375, 300);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public InputStream getChartAsStream() {
        return getChart().getStream().get();
    }

    public byte[] getChartAsByteArray() throws IOException {
        InputStream is = getChartAsStream();
        byte[] array = new byte[is.available()];
        is.read(array);
        return array;
    }

    private PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("New York", Double.valueOf(45.0));
        dataset.setValue("London", Double.valueOf(15.0));
        dataset.setValue("Paris", Double.valueOf(25.2));
        dataset.setValue("Berlin", Double.valueOf(14.8));

        return dataset;
    }
}
