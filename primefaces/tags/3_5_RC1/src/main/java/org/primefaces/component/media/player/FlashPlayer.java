/*
 * Copyright 2009-2012 Prime Teknoloji.
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

	private final static String[] supportedTypes = new String[]{"flv", "mp3", "swf"};
	
	public String getClassId() {
		return "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000";
	}

	public String getCodebase() {
		return "http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7";
	}
	
	public String getSourceParam() {
		return "movie";
	}

	public String getType() {
		return "application/x-shockwave-flash";
	}

    public String[] getSupportedTypes() {
        return supportedTypes;
    }
}