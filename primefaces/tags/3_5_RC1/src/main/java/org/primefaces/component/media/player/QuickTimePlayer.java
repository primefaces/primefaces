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

public class QuickTimePlayer implements MediaPlayer {

	private final static String[] supportedTypes = new String[]{"aif","aiff","aac","au","bmp","gsm","mov","mid","midi","mpg","mpeg",
																"mp4","m4a","psd","qt","qtif","qif","qti","snd","tif","tiff","wav","3g2","3pg"};
	
	public String getClassId() {
		return "clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B";
	}

	public String getCodebase() {
		return "http://www.apple.com/qtactivex/qtplugin.cab";
	}
	
	public String getSourceParam() {
		return "src";
	}

	public String getType() {
		return null;
	}
	
    public String[] getSupportedTypes() {
        return supportedTypes;
    }
}