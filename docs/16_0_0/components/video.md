# Video

Video component is used for embedding video content.

See HTML5 Video for events and attributes: [HTML5 Video Events and Attributes](https://www.w3schools.com/tags/ref_av_dom.asp)

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
