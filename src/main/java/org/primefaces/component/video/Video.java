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
package org.primefaces.component.video;

import org.primefaces.component.api.UIMedia;

public class Video extends UIMedia {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";
    public static final String DEFAULT_RENDERER = "org.primefaces.component.VideoRenderer";
    public static final String COMPONENT_TYPE = "org.primefaces.component.Video";

    public static final String CONTAINER_CLASS = "ui-media ui-video";

    public enum PropertyKeys {
        width,
        height,
        preload,
        poster;
    }

    public Video() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidth() {
        return (String) getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(String width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public String getHeight() {
        return (String) getStateHelper().eval(PropertyKeys.height, null);
    }

    public void setHeight(String height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public String getPreload() {
        return (String) getStateHelper().eval(PropertyKeys.preload, null);
    }

    public void setPreload(String preload) {
        getStateHelper().put(PropertyKeys.preload, preload);
    }

    public String getPoster() {
        return (String) getStateHelper().eval(PropertyKeys.poster, null);
    }

    public void setPoster(String poster) {
        getStateHelper().put(PropertyKeys.poster, poster);
    }
}
