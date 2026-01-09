/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.model.feedreader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing a RSS item. A channel may contain any number of items. An item may represent a "story" -- much
 * like a story in a newspaper or magazine; if so its description is a synopsis of the story, and the link points
 * to the full story.
 */
public class FeedItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private String link;
    private String author;
    private final List<String> categories = new ArrayList<>();
    private String guid;
    private Boolean isPermaLink;
    private String pubDate;
    private String comments;
    private String itunesDuration;
    private String itunesImage;
    private boolean itunesExplicit;
    private String itunesTitle;
    private String itunesSubtitle;
    private String itunesSummary;
    private String itunesKeywords;
    private Integer itunesEpisode;
    private Integer itunesSeason;
    private String itunesEpisodeType;
    private boolean itunesBlock;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Boolean getIsPermaLink() {
        return isPermaLink;
    }

    public void setIsPermaLink(Boolean isPermaLink) {
        this.isPermaLink = isPermaLink;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getItunesDuration() {
        return itunesDuration;
    }

    public void setItunesDuration(String itunesDuration) {
        this.itunesDuration = itunesDuration;
    }

    public String getItunesImage() {
        return itunesImage;
    }

    public void setItunesImage(String itunesImage) {
        this.itunesImage = itunesImage;
    }

    public boolean isItunesExplicit() {
        return itunesExplicit;
    }

    public void setItunesExplicit(boolean itunesExplicit) {
        this.itunesExplicit = itunesExplicit;
    }

    public String getItunesTitle() {
        return itunesTitle;
    }

    public void setItunesTitle(String itunesTitle) {
        this.itunesTitle = itunesTitle;
    }

    public String getItunesSubtitle() {
        return itunesSubtitle;
    }

    public void setItunesSubtitle(String itunesSubtitle) {
        this.itunesSubtitle = itunesSubtitle;
    }

    public String getItunesSummary() {
        return itunesSummary;
    }

    public void setItunesSummary(String itunesSummary) {
        this.itunesSummary = itunesSummary;
    }

    public String getItunesKeywords() {
        return itunesKeywords;
    }

    public void setItunesKeywords(String itunesKeywords) {
        this.itunesKeywords = itunesKeywords;
    }

    public Integer getItunesEpisode() {
        return itunesEpisode;
    }

    public void setItunesEpisode(Integer itunesEpisode) {
        this.itunesEpisode = itunesEpisode;
    }

    public Integer getItunesSeason() {
        return itunesSeason;
    }

    public void setItunesSeason(Integer itunesSeason) {
        this.itunesSeason = itunesSeason;
    }

    public String getItunesEpisodeType() {
        return itunesEpisodeType;
    }

    public void setItunesEpisodeType(String itunesEpisodeType) {
        this.itunesEpisodeType = itunesEpisodeType;
    }

    public boolean isItunesBlock() {
        return itunesBlock;
    }

    public void setItunesBlock(boolean itunesBlock) {
        this.itunesBlock = itunesBlock;
    }

    public List<String> getCategories() {
        return categories;
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, categories, comments, description, guid, isPermaLink, itunesBlock, itunesDuration, itunesEpisode,
                itunesEpisodeType, itunesExplicit, itunesImage, itunesKeywords, itunesSeason, itunesSubtitle, itunesSummary, itunesTitle, link, pubDate, title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FeedItem other = (FeedItem) obj;
        return Objects.equals(author, other.author) && Objects.equals(categories, other.categories)
                && Objects.equals(comments, other.comments) && Objects.equals(description, other.description) && Objects.equals(guid, other.guid)
                && Objects.equals(isPermaLink, other.isPermaLink) && itunesBlock == other.itunesBlock && Objects.equals(itunesDuration, other.itunesDuration)
                && Objects.equals(itunesEpisode, other.itunesEpisode) && Objects.equals(itunesEpisodeType, other.itunesEpisodeType)
                && itunesExplicit == other.itunesExplicit && Objects.equals(itunesImage, other.itunesImage)
                && Objects.equals(itunesKeywords, other.itunesKeywords) && Objects.equals(itunesSeason, other.itunesSeason)
                && Objects.equals(itunesSubtitle, other.itunesSubtitle) && Objects.equals(itunesSummary, other.itunesSummary)
                && Objects.equals(itunesTitle, other.itunesTitle) && Objects.equals(link, other.link) && Objects.equals(pubDate, other.pubDate)
                && Objects.equals(title, other.title);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FeedItem [title=").append(title).append(", description=").append(description).append(", link=").append(link).append(", author=")
                .append(author).append(", categories=").append(categories).append(", guid=").append(guid)
                .append(", isPermaLink=").append(isPermaLink).append(", pubDate=").append(pubDate).append(", comments=").append(comments)
                .append(", itunesDuration=").append(itunesDuration).append(", itunesImage=").append(itunesImage).append(", itunesExplicit=")
                .append(itunesExplicit).append(", itunesTitle=").append(itunesTitle).append(", itunesSubtitle=").append(itunesSubtitle)
                .append(", itunesSummary=").append(itunesSummary).append(", itunesKeywords=").append(itunesKeywords).append(", itunesEpisode=")
                .append(itunesEpisode).append(", itunesSeason=").append(itunesSeason).append(", itunesEpisodeType=").append(itunesEpisodeType)
                .append(", itunesBlock=").append(itunesBlock).append("]");
        return builder.toString();
    }

}
