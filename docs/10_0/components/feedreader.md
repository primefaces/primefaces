# FeedReader

FeedReader is used to display content from a feed.

## Info

| Name | Value |
| --- | --- |
| Tag | feedReader
| Component Class | org.primefaces.component.feedreader.FeedReader
| Component Type | org.primefaces.component.FeedReader
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.FeedReaderRenderer
| Renderer Class | org.primefaces.component.feedreader.FeedReaderRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| value | null | String | URL of the feed.
| var | null | String | Iterator to refer each item in feed.
| size | unlimited | Integer | Number of items to display.

## Getting started with FeedReader

FeedReader requires a feed url to display and renders its content for each feed item.

```xhtml
<p:feedReader value="http://rss.news.yahoo.com/rss/sports" var="feed">
    <h:outputText value="#{feed.title}" style="font-weight: bold"/>
    <h:outputText value="#{feed.description.value}" escape="false"/>
    <p:separator />
    <f:facet name="error">
        Something went wrong.
    </f:facet>
</p:feedReader>
```
**Note** that you need the ROME library in your classpath to make feedreader work.