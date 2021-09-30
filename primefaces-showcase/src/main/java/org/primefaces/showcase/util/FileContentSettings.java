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
package org.primefaces.showcase.util;

/**
 * FileContentSettings
 *
 * @author Sébastien Lepage / last modified by $Author$
 * @version $Revision$
 * @since 6.3
 */
public class FileContentSettings {

    private Marker[] startMarkers = null;
    private Marker[] endMarkers = null;
    private String type;

    public Marker[] getStartMarkers() {
        if (startMarkers == null) {
            return new Marker[0];
        }
        return startMarkers;
    }

    public FileContentSettings setStartMarkers(Marker... startMarkers) {
        this.startMarkers = startMarkers;
        return this;
    }

    public Marker[] getEndMarkers() {
        if (endMarkers == null) {
            return new Marker[0];
        }
        return endMarkers;
    }

    public FileContentSettings setEndMarkers(Marker... endMarkers) {
        this.endMarkers = endMarkers;
        return this;
    }

    public String getType() {
        return type;
    }

    public FileContentSettings setType(String type) {
        this.type = type;
        return this;
    }
}
