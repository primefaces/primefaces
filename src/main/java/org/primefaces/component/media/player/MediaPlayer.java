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

public interface MediaPlayer {

    String WINDOWS = "windows";
    String QUICKTIME = "quicktime";
    String FLASH = "flash";
    String REAL = "real";
    String PDF = "pdf";

    String getClassId();

    String getCodebase();

    String getSourceParam();

    String getType();

    String[] getSupportedTypes();
}
