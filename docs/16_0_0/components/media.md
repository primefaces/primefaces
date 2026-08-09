# Media

Media component is used for embedding multimedia content.

## Getting started with Media
In its simplest form media component requires a source to play;

```xhtml
<p:media value="/media/ria_with_primefaces.mov" />
```

## Player Types
By default, players are identified using the value extension so for instance mov files will be played
by quicktime player. You can customize which player to use with the player attribute.

```xhtml
<p:media value="http://www.youtube.com/v/ABCDEFGH" player="flash"/>
```
Following is the supported players and file types.

| Player | Types |
| --- | --- |
windows | asx, asf, avi, wma, wmv
quicktime | aif, aiff, aac, au, bmp, gsm, mov, mid, midi, mpg, mpeg, mp4, m4a, psd, qt, qtif, qif, qti, snd, tif, tiff, wav, 3g2, 3pg
flash | flv, mp3, swf
real | ra, ram, rm, rpm, rv, smi, smil
pdf | pdf

## Parameters
Different proprietary players might have different configuration parameters, these can be specified
using f:param tags.

```xhtml
<p:media value="/media/ria_with_primefaces.mov">
    <f:param name="param1" value="value1" />
</p:media>
```

## StreamedContent Support
Media component can also play binary media content, example for this use case is storing media
files in database using binary format. In order to implement this, bind a StreamedContent.
Please see our core documentation about it: [Dynamic Content Streaming / Rendering](/core/dynamiccontent.md)

```xhtml
<p:media value="#{mediaBean.media}" width="250" height="225" player="quicktime"/>
```
```java
public class MediaBean {
    private StreamedContent media;

    public MediaController() {
        media = DefaultStreamedContent.builder()
                    .contentType("video/quicktime")
                    .stream(() -> //Create binary stream from database)
                    .build();
    }
    public StreamedContent getMedia() { 
        return media;
    }
}
```
