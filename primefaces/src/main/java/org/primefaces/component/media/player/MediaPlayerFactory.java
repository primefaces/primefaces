/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import java.util.Map;

public class MediaPlayerFactory {

    private static final Map<String, MediaPlayer> PLAYERS = Map.ofEntries(
            Map.entry(MediaPlayer.QUICKTIME, new QuickTimePlayer()),
            Map.entry(MediaPlayer.WINDOWS, new WindowsPlayer()),
            Map.entry(MediaPlayer.REAL, new RealPlayer()),
            Map.entry(MediaPlayer.PDF, new PDFPlayer())
    );

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
