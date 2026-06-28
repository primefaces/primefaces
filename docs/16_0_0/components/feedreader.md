# FeedReader

FeedReader is used to display content from a feed.

## Getting started with FeedReader

FeedReader requires a feed URL to display and renders its content for each feed item.

```xhtml
<p:feedReader value="http://rss.news.yahoo.com/rss/sports" var="feed">
    <h:outputText value="#{feed.title}" style="font-weight: bold"/>
    <h:outputText value="#{feed.description}" escape="false"/>
    <p:divider />
    <f:facet name="error">
        Something went wrong.
    </f:facet>
</p:feedReader>
```

!> NOTE: You need the [RSS Reader](https://github.com/w3stling/rssreader) library in your classpath to make FeedReader work.