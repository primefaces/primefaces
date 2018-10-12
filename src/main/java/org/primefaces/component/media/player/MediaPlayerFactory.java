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

import java.util.Map;
import org.primefaces.util.MapBuilder;

public class MediaPlayerFactory {

    private static final Map<String, MediaPlayer> PLAYERS = MapBuilder.<String, MediaPlayer>builder()
            .put(MediaPlayer.QUICKTIME, new QuickTimePlayer())
            .put(MediaPlayer.FLASH, new FlashPlayer())
            .put(MediaPlayer.WINDOWS, new WindowsPlayer())
            .put(MediaPlayer.REAL, new RealPlayer())
            .put(MediaPlayer.PDF, new PDFPlayer())
            .build();

    private MediaPlayerFactory() {
    }

    /**
     * @return Provides all players configured by this factory
     */
    public static Map<String, MediaPlayer> getPlayers() {
        return PLAYERS;
    }

    /**
     * @return the specific player
     */
    public static MediaPlayer getPlayer(String type) {
        if (type == null) {
            throw new IllegalArgumentException("A media player type must be provided");
        }

        MediaPlayer player = PLAYERS.get(type);

        if (player != null) {
            return player;
        }
        else {
            throw new IllegalArgumentException(type + " is not a valid media player type");
        }
    }
}
