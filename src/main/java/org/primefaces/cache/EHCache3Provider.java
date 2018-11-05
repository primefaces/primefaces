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

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;

public class EHCache3Provider implements CacheProvider {

    private CacheManager cacheManager;

    public EHCache3Provider() {
        XmlConfiguration xmlConfig = new XmlConfiguration(this.getClass().getResource("/ehcache.xml"));
        cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
        cacheManager.init();
    }

    @Override
    public Object get(String region, String key) {
        Cache cacheRegion = getRegion(region);
        Object val = cacheRegion.get(key);

        return val;
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

    }

    protected Cache getRegion(String regionName) {
        Cache region = getCacheManager().getCache(regionName, String.class, Object.class);

        return region;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
