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
package org.primefaces.component.feedreader;

import org.primefaces.model.feedreader.FeedItem;
import org.primefaces.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.apptasticsoftware.rssreader.AbstractRssReader;
import com.apptasticsoftware.rssreader.Item;
import com.apptasticsoftware.rssreader.RssReader;
import com.apptasticsoftware.rssreader.module.itunes.ItunesItem;
import com.apptasticsoftware.rssreader.module.itunes.ItunesRssReader;

/**
 * Static utility class to parse feed input into a list of FeedItem objects.
 */
public class RSSUtils {

    private RSSUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Parses the input into a list of FeedItem objects.
     *
     * @param input     The input source, either a String representing the URL of the feed or an InputStream.
     * @param size      The maximum number of items to retrieve from the feed.
     * @param isPodcast Specifies whether the feed is a podcast feed.
     * @return A list of FeedItem objects parsed from the input.
     * @throws IOException If an I/O error occurs during the parsing process.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<FeedItem> parse(Object input, int size, boolean isPodcast) throws IOException {
        AbstractRssReader reader;
        if (isPodcast) {
            reader = new ItunesRssReader();
        }
        else {
            reader = new RssReader();
        }
        if (input instanceof String) {
            return convert((List<Item>) reader.read((String) input).limit(size).collect(Collectors.toList()));
        }
        return convert((List<Item>) reader.read((InputStream) input).limit(size).collect(Collectors.toList()));
    }

    /**
     * Converts a list of generic RSS items into a list of FeedItem objects.
     *
     * @param items The list of generic RSS items to convert.
     * @return A list of FeedItem objects converted from the input list of items.
     */
    private static List<FeedItem> convert(List<Item> items) {
        List<FeedItem> results = new ArrayList<>(items.size());
        for (Item item : items) {
            FeedItem fi = new FeedItem();
            fi.setAuthor(item.getAuthor().orElse(Constants.EMPTY_STRING));
            fi.getCategories().addAll(item.getCategories());
            fi.setComments(item.getComments().orElse(Constants.EMPTY_STRING));
            fi.setDescription(item.getDescription().orElse(Constants.EMPTY_STRING));
            fi.setGuid(item.getGuid().orElse(Constants.EMPTY_STRING));
            fi.setIsPermaLink(item.getIsPermaLink().orElse(Boolean.FALSE));
            fi.setLink(item.getLink().orElse(Constants.EMPTY_STRING));
            fi.setPubDate(item.getPubDate().orElse(Constants.EMPTY_STRING));
            fi.setTitle(item.getTitle().orElse(Constants.EMPTY_STRING));
            if (item instanceof ItunesItem) {
                ItunesItem itunes = (ItunesItem) item;
                fi.setItunesBlock(itunes.isItunesBlock());
                fi.setItunesDuration(itunes.getItunesDuration().orElse(Constants.EMPTY_STRING));
                fi.setItunesEpisode(itunes.getItunesEpisode().orElse(0));
                fi.setItunesEpisodeType(itunes.getItunesEpisodeType().orElse(Constants.EMPTY_STRING));
                fi.setItunesExplicit(itunes.isItunesExplicit());
                fi.setItunesImage(itunes.getItunesImage().orElse(Constants.EMPTY_STRING));
                fi.setItunesKeywords(itunes.getItunesKeywords().orElse(Constants.EMPTY_STRING));
                fi.setItunesSeason(itunes.getItunesSeason().orElse(0));
                fi.setItunesSubtitle(itunes.getItunesSubtitle().orElse(Constants.EMPTY_STRING));
                fi.setItunesSummary(itunes.getItunesSummary().orElse(Constants.EMPTY_STRING));
                fi.setItunesTitle(itunes.getItunesTitle().orElse(Constants.EMPTY_STRING));
            }
            results.add(fi);
        }
        return results;
    }
}
