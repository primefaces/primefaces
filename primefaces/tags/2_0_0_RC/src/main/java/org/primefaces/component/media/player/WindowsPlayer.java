package org.primefaces.component.media.player;

public class WindowsPlayer implements MediaPlayer {

	private final static String[] supportedTypes = new String[]{"asx", "asf", "avi", "wma", "wmv"};
	
	public String getClassId() {
		return "clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6";
	}

	public String getCodebase() {
		return "http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=6,4,7,1112";
	}

	public String getPlugingPage() {
		return "http://www.microsoft.com/Windows/MediaPlayer/";
	}
	
	public String getSourceParam() {
		return "url";
	}

	public String getType() {
		return "application/x-mplayer2";
	}
	
	public boolean isAppropriatePlayer(String sourceType) {
		for(String supportedType : supportedTypes) {
			if(sourceType.equals(supportedType))
				return true;
		}
		
		return false;
	}
}