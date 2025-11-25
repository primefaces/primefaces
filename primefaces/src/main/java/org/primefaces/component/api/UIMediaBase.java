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
package org.primefaces.component.api;

import org.primefaces.cdk.api.Property;
import org.primefaces.util.DynamicContentSrcBuilder;
import org.primefaces.util.Lazy;

import java.io.IOException;

import jakarta.faces.component.UIComponentBase;
import jakarta.faces.context.FacesContext;

/**
 * Base class for HTML5 audio and video components.
 */
public abstract class UIMediaBase extends UIComponentBase {

    public String resolveSource(FacesContext context) throws IOException {
        try {
            return DynamicContentSrcBuilder.build(context, this, this.getValueExpression("value"),
                    new Lazy<>(() -> this.getValue()), this.isCache(), true);
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Property(defaultValue = "true", description = "Whether to cache the media content")
    public abstract boolean isCache();

    @Property(description = "Specifies that the media will start playing as soon as it is ready.")
    public abstract boolean isAutoplay();

    @Property(description = "Specifies that media controls should be displayed (such as a play/pause button etc).")
    public abstract boolean isControls();

    @Property(description = "When specified, helps the browser select what controls to show. Values are nodownload, nofullscreen and noremoteplayback.")
    public abstract String getControlslist();

    @Property(description = "Indicates whether to use CORS to fetch the related media file. Values are anonymous and user-credentials.")
    public abstract String getCrossorigin();

    @Property(description = "Sets or returns the current playback position in seconds.")
    public abstract String getCurrentTime();

    @Property(description = "Specifies that the audio output should be muted by default.")
    public abstract boolean isDefaultMuted();

    @Property(description = "Sets or returns the default speed of the media playback.")
    public abstract String getDefaultPlaybackRate();

    @Property(description = "Used to disable the capability of remote playback in devices that are attached using wired and wireless technologies.")
    public abstract boolean isDisableremoteplayback();

    @Property(description = "Prevents the browser from suggesting a Picture-in-Picture context menu or to request Picture-in-Picture automatically.")
    public abstract boolean isDisablepictureinpicture();

    @Property(description = "Height of the media component.")
    public abstract String getHeight();

    @Property(description = "Specifies that the media will start over again, every time it is finished.")
    public abstract boolean isLoop();

    @Property(description = "Specifies that the audio output of the media should be muted.")
    public abstract boolean isMuted();

    @Property(description = "Client-side callback to execute when the media loading is aborted.")
    public abstract String getOnabort();

    @Property(description = "Client-side callback to execute when the browser can start playing the media.")
    public abstract String getOncanplay();

    @Property(description = "Client-side callback to execute when the browser can play through the media without stopping for buffering.")
    public abstract String getOncanplaythrough();

    @Property(description = "Client-side callback to execute when the duration of the media is changed.")
    public abstract String getOndurationchange();

    @Property(description = "Client-side callback to execute when the media resource element suddenly becomes empty.")
    public abstract String getOnemptied();

    @Property(description = "Client-side callback to execute when the media has reached the end.")
    public abstract String getOnended();

    @Property(description = "Client-side callback to execute when an error occurs while loading the media.")
    public abstract String getOnerror();

    @Property(description = "Client-side callback to execute when media data is loaded.")
    public abstract String getOnloadeddata();

    @Property(description = "Client-side callback to execute when meta data (like dimensions and duration) are loaded.")
    public abstract String getOnloadedmetadata();

    @Property(description = "Client-side callback to execute when the browser starts looking for the media.")
    public abstract String getOnloadstart();

    @Property(description = "Client-side callback to execute when the media is paused.")
    public abstract String getOnpause();

    @Property(description = "Client-side callback to execute when the media is started or is no longer paused.")
    public abstract String getOnplay();

    @Property(description = "Client-side callback to execute when the media is playing after having been paused or stopped for buffering.")
    public abstract String getOnplaying();

    @Property(description = "Client-side callback to execute when the browser is downloading the media.")
    public abstract String getOnprogress();

    @Property(description = "Client-side callback to execute when the playing speed of the media is changed.")
    public abstract String getOnratechange();

    @Property(description = "Client-side callback to execute when the user is finished moving/skipping to a new position in the media.")
    public abstract String getOnseeked();

    @Property(description = "Client-side callback to execute when the user starts moving/skipping to a new position in the media.")
    public abstract String getOnseeking();

    @Property(description = "Client-side callback to execute when the browser is trying to get media data, but data is not available.")
    public abstract String getOnstalled();

    @Property(description = "Client-side callback to execute when the browser is intentionally not getting media data.")
    public abstract String getOnsuspend();

    @Property(description = "Client-side callback to execute when the playing position has changed.")
    public abstract String getOntimeupdate();

    @Property(description = "Client-side callback to execute when the volume has been changed.")
    public abstract String getOnvolumechange();

    @Property(description = "Client-side callback to execute when the media has paused but is expected to resume.")
    public abstract String getOnwaiting();

    @Property(description = "Sets or returns the speed of the media playback.")
    public abstract String getPlaybackRate();

    @Property(description = "Media player type.")
    public abstract String getPlayer();

    @Property(description = "A Boolean attribute indicating that the video is to be played 'inline', that is within the element's playback area.")
    public abstract boolean isPlaysinline();

    @Property(description = "Specifies an image to be shown while the video is downloading, or until the user hits the play button.")
    public abstract String getPoster();

    @Property(description = "Specifies if and how the author thinks the media should be loaded when the page loads. Values are auto, metadata, and none.")
    public abstract String getPreload();

    @Property(description = "Inline style of the media component.")
    public abstract String getStyle();

    @Property(description = "Style class of the media component.")
    public abstract String getStyleClass();

    @Property(description = "Media source value.")
    public abstract Object getValue();

    @Property(description = "Sets or returns the audio volume of the media. Must be a number between 0.0 and 1.0.")
    public abstract String getVolume();

    @Property(description = "Width of the media component.")
    public abstract String getWidth();

}