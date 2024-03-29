# Audio

Audio component is used for embedding audio content.

See HTML5 Audio for events and attributes: [HTML5 Audio Events and Attributes](https://www.w3schools.com/tags/ref_av_dom.asp)

## Info

| Name | Value |
| --- | --- |
| Tag | media
| Component Class | org.primefaces.component.audio.Audio
| Component Type | org.primefaces.component.Audio
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.AudioRenderer
| Renderer Class | org.primefaces.component.audio.AudioRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
autoplay | null | Boolean | Specifies that the video will start playing as soon as it is ready
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
cache | true | Boolean | Controls browser caching mode of the resource.
controls | null | Boolean | Specifies that video controls should be displayed (such as a play/pause button etc).
controlslist | null | String | When specified, helps the browser select what controls to show. The allowed values are nodownload, nofullscreen and noremoteplayback.
crossorigin | null | String | This enumerated attribute indicates whether to use CORS to fetch the related audio/video file. The allowed values are anonymous and user-credentials.
disableremoteplayback | null | Boolean | Used to disable the capability of remote playback in devices that are attached using wired (HDMI, DVI, etc.) and wireless technologies (Miracast, Chromecast, DLNA, AirPlay, etc.)
loop | null | Boolean | Specifies that the video will start over again, every time it is finished
muted | null | Boolean | Specifies that the audio output of the video should be muted
player | null | String | Type of the player, possible values are "mp3","ogg", and "wav".
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
style | null | String | Style of the player.
styleClass | null | String | StyleClass of the player.
value | null | String | Audio source to play. Can be dynamic content.


## Getting started with Audio
In its simplest form media component requires a source to play;

```xhtml
<p:audio value="/media/primefaces.mp3" controls="true"/>
```

## Player Types
By default, players are identified using the value extension such as `mp3`. You can customize which player to use with the player attribute.

```xhtml
<p:media value="http://www.vorbis.com/v/ABCDEFGH" player="ogg" controls="true"/>
```

## StreamedContent Support
Media component can also play binary media content, example for this use case is storing media
files in database using binary format. In order to implement this, bind a StreamedContent.
Please see our core documentation about it: [Dynamic Content Streaming / Rendering](/core/dynamiccontent.md)

```xhtml
<p:audio value="#{mediaController.audio}" player="mp3" controls="true"/>
```
```java
public class MediaController {
    private StreamedContent media;

    public MediaController() {
        media = DefaultStreamedContent.builder()
                    .contentType(AudioType.MP3.getMediaType())
                    .stream(() -> //Create binary stream from database)
                    .build();
    }
    public StreamedContent getMedia() { 
        return media;
    }
}
```

## Client Side Events
See the documentation at [HTML5 Audio Events and Attributes](https://www.w3schools.com/tags/ref_av_dom.asp)
All events are available such as `onplay` and `onpause`.

```xml
<p:audio onplay="console.log('MP3 Started Playing')" onpause="console.log('MP3 Stopped Playing')" />
```
