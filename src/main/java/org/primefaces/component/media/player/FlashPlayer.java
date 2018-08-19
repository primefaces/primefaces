/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.media.player;

public class FlashPlayer implements MediaPlayer {

    private static final String[] SUPPORTED_TYPES = new String[]{"flv", "mp3", "swf"};

    @Override
    public String getClassId() {
        return "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000";
    }

    @Override
    public String getCodebase() {
        return "http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7";
    }

    @Override
    public String getSourceParam() {
        return "movie";
    }

    @Override
    public String getType() {
        return "application/x-shockwave-flash";
    }

    @Override
    public String[] getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
}
