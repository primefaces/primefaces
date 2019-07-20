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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * Basic cache provider for development purposes, should be avoided to use in production as there is no eviction and timeouts.
 */
public class DefaultCacheProvider implements CacheProvider {

    private static final Logger LOGGER = Logger.getLogger(DefaultCacheProvider.class.getName());

    private final Map<String, ConcurrentMap<String, Object>> cache;

    public DefaultCacheProvider() {
        cache = new ConcurrentHashMap<>();
        LOGGER.warning("DefaultCacheProvider is for development purposes only, prefer another provider such as EhCache and HazelCast in production.");
    }

    @Override
    public Object get(String region, String key) {
        Map<String, Object> cacheRegion = getRegion(region);
        return cacheRegion.get(key);
    }

    @Override
    public void put(String region, String key, Object object) {
        Map<String, Object> cacheRegion = getRegion(region);
        cacheRegion.put(key, object);
    }

    @Override
    public void remove(String region, String key) {
        Map<String, Object> cacheRegion = getRegion(region);
        cacheRegion.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    private Map<String, Object> getRegion(String name) {
        return cache.computeIfAbsent(name, k -> new ConcurrentHashMap<>());
    }
}
