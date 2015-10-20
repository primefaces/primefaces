/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.primefaces.component.feedreader;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
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
            changeContentLinksFromHttpToHttps((SyndEntry) f);
            if(i == size)
                break;

            entries.add(f);
            i++;
        }
        
        return entries;
    }
    
    //refs #2468
    public void changeContentLinksFromHttpToHttps(SyndEntry entry) {
        for (Object c : entry.getContents()) {
            SyndContent content = (SyndContent) c;
            int indexImg = content.getValue().indexOf("img");
            while (indexImg != -1) {
                int nextIndexImg = content.getValue().indexOf("img", indexImg + 1);
                int nextHttpIndex = content.getValue().indexOf("http", indexImg + 1);
                if (indexImg != -1 && nextHttpIndex < nextIndexImg) {
                    content.setValue(
                            content.getValue().substring(0, nextHttpIndex)
                            + "https"
                            + content.getValue().substring(nextHttpIndex + 4)
                    );
                }
                indexImg = content.getValue().indexOf("img", nextHttpIndex);
            }
        }
    }
}
