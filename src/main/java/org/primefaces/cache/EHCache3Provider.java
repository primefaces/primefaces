/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.cache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;

public class EHCache3Provider implements CacheProvider {

    private final CacheManager cacheManager;

    public EHCache3Provider() {
        XmlConfiguration xmlConfig = new XmlConfiguration(this.getClass().getResource("/ehcache.xml"));
        cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
        cacheManager.init();
    }

    @Override
    public Object get(String region, String key) {
        Cache cacheRegion = getRegion(region);
        return cacheRegion.get(key);
    }

    @Override
    public void put(String region, String key, Object object) {
        Cache cacheRegion = getRegion(region);

        cacheRegion.put(key, object);
    }

    @Override
    public void remove(String region, String key) {
        Cache cacheRegion = getRegion(region);

        cacheRegion.remove(key);
    }

    @Override
    public void clear() {
        // not supported by EHCache 3
    }

    protected Cache getRegion(String regionName) {
        return getCacheManager().getCache(regionName, String.class, Object.class);
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }
}
