/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.primefaces.component.feedreader;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FeedInput {
      
    public List parse(String url, int size) throws Exception {
        List entries = new ArrayList();
        URL feedSource = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        int i = 0;
        
        for(Object f : feed.getEntries()) {
            if(i == size)
                break;

            entries.add(f);
            i++;
        }
        
        return entries;
    }
}
