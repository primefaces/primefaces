# Video

Video component is used for embedding video content.

See HTML5 Video for events and attributes: [HTML5 Video Events and Attributes](https://www.w3schools.com/tags/ref_av_dom.asp)

## Info

| Name | Value |
| --- | --- |
| Tag | media
| Component Class | org.primefaces.component.video.Video
| Component Type | org.primefaces.component.Video
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.VideoRenderer
| Renderer Class | org.primefaces.component.video.VideoRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
value | null | String | Video source to play. Can be dynamic content.
player | null | String | Type of the player, possible values are "mp4","ogg", and "webm".
style | null | String | Style of the player.
styleClass | null | String | StyleClass of the player.
cache | true | Boolean | Controls browser caching mode of the resource.
poster | null | String | Specifies an image to be shown while the video is downloading, or until the user hits the play button
autoplay | null | Boolean | Specifies that the video will start playing as soon as it is ready
controls | null | Boolean | Specifies that video controls should be displayed (such as a play/pause button etc).
loop | null | Boolean | Specifies that the video will start over again, every time it is finished
muted | null | Boolean | Specifies that the audio output of the video should be muted
preload | null | String | Specifies if and how the author thinks the video should be loaded when the page loads
height | null | String | Sets the height of the video player
width | null | String | Sets the width of the video player

## Getting started with Video
In its simplest form media component requires a source to play;

```xhtml
<p:video value="/media/primefaces.mp4" controls="true"/>
```

## Player Types
By default, players are identified using the value extension such as `mp4`. You can customize which player to use with the player attribute.

```xhtml
<p:media value="http://www.vorbis.com/v/ABCDEFGH" player="ogg" controls="true"/>
```

## StreamedContent Support
Media component can also play binary media content, example for this use case is storing media
files in database using binary format. In order to implement this, bind a StreamedContent.
Please see our core documentation about it: [Dynamic Content Streaming / Rendering](/core/dynamiccontent.md)

```xhtml
<p:video value="#{mediaController.video}" player="mp4" controls="true"/>
```
```java
public class MediaController {
    private StreamedContent media;

    public MediaController() {
        media = DefaultStreamedContent.builder()
                    .contentType(VideoType.MP4.getMediaType())
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
<p:video onplay="console.log('MP4 Started Playing')" onpause="console.log('MP4 Stopped Playing')" />
```
