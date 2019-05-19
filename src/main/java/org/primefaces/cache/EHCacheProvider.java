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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EHCacheProvider implements CacheProvider {

    private final CacheManager cacheManager;

    public EHCacheProvider() {
        cacheManager = CacheManager.create();
    }

    @Override
    public Object get(String region, String key) {
        Cache cacheRegion = getRegion(region);
        Element element = cacheRegion.get(key);

        if (element != null) {
            return element.getObjectValue();
        }
        else {
            return null;
        }
    }

    @Override
    public void put(String region, String key, Object object) {
        Cache cacheRegion = getRegion(region);

        cacheRegion.put(new Element(key, object));
    }

    @Override
    public void remove(String region, String key) {
        Cache cacheRegion = getRegion(region);

        cacheRegion.remove(key);
    }

    @Override
    public void clear() {
        String[] cacheNames = getCacheManager().getCacheNames();
        if (cacheNames != null) {
            for (String cacheName : cacheNames) {
                Cache cache = getRegion(cacheName);
                cache.removeAll();
            }
        }
    }

    protected Cache getRegion(String regionName) {
        Cache region = getCacheManager().getCache(regionName);
        if (region == null) {
            getCacheManager().addCache(regionName);
            region = getCacheManager().getCache(regionName);
        }

        return region;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }
}
