/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.model.map;

public class Marker<T> extends Overlay<T> {

    private static final long serialVersionUID = 1L;

    private boolean clickable = true;

    private String cursor;

    private boolean draggable;

    private boolean flat;

    /**
     * Either a URL (as {@link String}) pointing to an image file or {@link Symbol} to display instead of the default
     * Google Maps pushpin icon.
     */
    private Object icon;

    private LatLng latlng;

    private String shadow;

    private String title;

    private MarkerLabel label;

    private boolean visible = true;

    private Animation animation;

    public Marker(LatLng latlng) {
        this.latlng = latlng;
    }

    public Marker(LatLng latlng, String title) {
        this.latlng = latlng;
        this.title = title;
    }

    public Marker(LatLng latlng, String title, T data) {
        super(data);
        this.latlng = latlng;
        this.title = title;
    }

    public Marker(LatLng latlng, String title, T data, Object icon) {
        super(data);
        this.latlng = latlng;
        this.title = title;
        this.icon = icon;
    }

    public Marker(LatLng latlng, String title, T data, Object icon, String shadow) {
        super(data);
        this.latlng = latlng;
        this.title = title;
        this.icon = icon;
        this.shadow = shadow;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public boolean isFlat() {
        return flat;
    }

    public void setFlat(boolean flat) {
        this.flat = flat;
    }

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public String getShadow() {
        return shadow;
    }

    public void setShadow(String shadow) {
        this.shadow = shadow;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public MarkerLabel getLabel() {
        return label;
    }

    public void setLabel(MarkerLabel label) {
        this.label = label;
    }
}
