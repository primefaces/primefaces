/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    private final static Logger LOG = Logger.getLogger(DefaultCacheProvider.class.getName());

    private ConcurrentMap<String, ConcurrentMap<String, Object>> cache;

    public DefaultCacheProvider() {
        cache = new ConcurrentHashMap<>();
        LOG.warning("DefaultCacheProvider is for development purposes only, prefer another provider such as EhCache and HazelCast in production.");
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
        ConcurrentMap<String, Object> region = cache.get(name);
        if (region == null) {
            region = new ConcurrentHashMap<>();
            cache.put(name, region);
        }

        return region;
    }
}
