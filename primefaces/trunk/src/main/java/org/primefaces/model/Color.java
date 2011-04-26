/*
 * Copyright 2009-2011 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.model;

import java.io.Serializable;

public class Color implements Serializable {

    private String hex;

    private int red;

    private int green;

    private int blue;

    private int hue;

    private int saturation;

    private int brightness;

    public Color(String hex, int red, int green, int blue, int hue, int saturation, int brightness) {
        this.hex = hex;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }

    public int getBlue() {
        return blue;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getGreen() {
        return green;
    }

    public String getHex() {
        return hex;
    }

    public int getHue() {
        return hue;
    }

    public int getRed() {
        return red;
    }

    public int getSaturation() {
        return saturation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Color other = (Color) obj;
        if ((this.hex == null) ? (other.hex != null) : !this.hex.equals(other.hex)) {
            return false;
        }
        if (this.red != other.red) {
            return false;
        }
        if (this.green != other.green) {
            return false;
        }
        if (this.blue != other.blue) {
            return false;
        }
        if (this.hue != other.hue) {
            return false;
        }
        if (this.saturation != other.saturation) {
            return false;
        }
        if (this.brightness != other.brightness) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.hex != null ? this.hex.hashCode() : 0);
        hash = 13 * hash + this.red;
        hash = 13 * hash + this.green;
        hash = 13 * hash + this.blue;
        hash = 13 * hash + this.hue;
        hash = 13 * hash + this.saturation;
        hash = 13 * hash + this.brightness;
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(hex == null) {
            return builder.toString();
        } else {
            return builder.append(hex)
                        .append(",")
                        .append(red).append("_").append(green).append("_").append(blue)
                        .append(",")
                        .append(hue).append("_").append(saturation).append("_").append(brightness)
                        .toString();
        }
    }
}
