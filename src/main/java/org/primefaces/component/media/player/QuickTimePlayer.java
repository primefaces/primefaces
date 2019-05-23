/**
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
package org.primefaces.component.media.player;

public class QuickTimePlayer implements MediaPlayer {

    private static final String[] SUPPORTED_TYPES = new String[]{"aif", "aiff", "aac", "au", "bmp", "gsm", "mov", "mid", "midi", "mpg", "mpeg",
        "mp4", "m4a", "psd", "qt", "qtif", "qif", "qti", "snd", "tif", "tiff", "wav", "3g2", "3pg"};

    @Override
    public String getClassId() {
        return "clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B";
    }

    @Override
    public String getCodebase() {
        return "http://www.apple.com/qtactivex/qtplugin.cab";
    }

    @Override
    public String getSourceParam() {
        return "src";
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String[] getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
}
