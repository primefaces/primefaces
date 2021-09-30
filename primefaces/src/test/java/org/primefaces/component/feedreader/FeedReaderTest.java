/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import com.rometools.rome.io.ParsingFeedException;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FeedReaderTest {

    @Test()
    public void parseXXEbomb() throws Exception {
        // Check CVE-2021-33813 can not be triggered in Primefaces
        // See https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2021-33813
        // See https://alephsecurity.com/vulns/aleph-2021003
        URL feed = FeedReaderTest.class.getResource("/org/primefaces/feeds/XXEbomb.xml");
        assertThrows(ParsingFeedException.class, () -> new FeedInput().parse(feed.toString(), 1));
    }

    @Test()
    public void parseXXE() throws Exception {
        URL feed = FeedReaderTest.class.getResource("/org/primefaces/feeds/XXE.xml");
        assertThrows(ParsingFeedException.class, () -> new FeedInput().parse(feed.toString(), 1));
    }

    @Test()
    public void parseRSS() throws Exception {
        URL feed = FeedReaderTest.class.getResource("/org/primefaces/feeds/RSS2.0.xml");
        List rss = new FeedInput().parse(feed.toString(), 10);
        assertNotNull(rss);
        assertEquals(2, rss.size());
    }
}