package org.primefaces.model.charts.bar;

import java.io.IOException;

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Set the corners of bars in charts as per the
 * <a href="https://www.chartjs.org/docs/latest/charts/bar.html#borderradius">Chartjs documentation</a>
 */
public class BarCorner {

    private int topLeft;
    private int bottomLeft;
    private int topRight;
    private int bottomRight;

    /**
     * Get the top left corner radius in pixel
     * @return the radius
     */
    public int getTopLeft() {
        return topLeft;
    }
    /**
     * Set the top left corner radius in pixel
     * @param topLeft the radius
     */
    public void setTopLeft(int topLeft) {
        this.topLeft = topLeft;
    }
    /**
     * Get the bottom left corner radius in pixel
     * @return the radius
     */
    public int getBottomLeft() {
        return bottomLeft;
    }
    /**
     * Set the bottom left corner radius in pixel
     * @return the radius
     */
    public void setBottomLeft(int bottomLeft) {
        this.bottomLeft = bottomLeft;
    }
    /**
     * Get the top right corner radius in pixel
     * @return the radius
     */
    public int getTopRight() {
        return topRight;
    }
    /**
     * Get the top right corner radius in pixel
     * @param topRight the radius
     */
    public void setTopRight(int topRight) {
        this.topRight = topRight;
    }
    /**
     * Get the bottom right corner radius in pixel
     * @return the radius
     */
    public int getBottomRight() {
        return bottomRight;
    }
    /**
     * Set the bottom right corner radius in pixel
     * @param bottomRight the radius
     */
    public void setBottomRight(int bottomRight) {
        this.bottomRight = bottomRight;
    }

    /**
     * encodes and writes the object
     * @param fsw the writer in which we send the encoded value
     * @throws IOException on error writing data
     */
    public void encode(FastStringWriter fsw) throws IOException {
        fsw.write("{");
        ChartUtils.writeDataValue(fsw, "topLeft", topLeft, false);
        ChartUtils.writeDataValue(fsw, "topRight", topRight, true);
        ChartUtils.writeDataValue(fsw, "bottomLeft", bottomLeft, true);
        ChartUtils.writeDataValue(fsw, "bottomRight", bottomRight, true);
        fsw.write("}");
    }

}
