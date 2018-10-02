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

public class RealPlayer implements MediaPlayer {

    private static final String[] SUPPORTED_TYPES = new String[]{"ra", "ram", "rm", "rpm", "rv", "smi", "smil"};

    @Override
    public String getClassId() {
        return "clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA";
    }

    @Override
    public String getCodebase() {
        return null;
    }

    @Override
    public String getSourceParam() {
        return "src";
    }

    @Override
    public String getType() {
        return "audio/x-pn-realaudio-plugin";
    }

    @Override
    public String[] getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
}
