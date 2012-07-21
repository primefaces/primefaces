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

public class WindowsPlayer implements MediaPlayer {

	private final static String[] supportedTypes = new String[]{"asx", "asf", "avi", "wma", "wmv"};
	
	public String getClassId() {
		return "clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6";
	}

	public String getCodebase() {
		return "http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=6,4,7,1112";
	}
	
	public String getSourceParam() {
		return "url";
	}

	public String getType() {
		return "application/x-mplayer2";
	}
	
    public String[] getSupportedTypes() {
        return supportedTypes;
    }
}