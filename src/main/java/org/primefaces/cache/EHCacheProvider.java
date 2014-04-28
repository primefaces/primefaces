/*
 * Copyright 2009-2014 PrimeTek.
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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EHCacheProvider implements CacheProvider {

    private CacheManager cacheManager;
    
    public EHCacheProvider() {
        cacheManager = new CacheManager();
    }
    
    public Object get(String region, String key) {
        Cache cacheRegion = getRegion(region);
        Element element = cacheRegion.get(key);
        
        if(element != null) {
            return element.getObjectValue();
        }
        else {
            return null;
        }
    }

    public void put(String region, String key, Object object) {
        Cache cacheRegion = getRegion(region);
        
        cacheRegion.put(new Element(key, object));
    }

    public void remove(String region, String key) {
        Cache cacheRegion = getRegion(region);
        
        cacheRegion.remove(key);
    }

    public void clear() {
        String[] cacheNames = cacheManager.getCacheNames();
        if(cacheNames != null) {
            for(int i = 0; i < cacheNames.length; i++) {
                Cache cache = getRegion(cacheNames[i]);
                cache.removeAll();
            }
        }
    } 
    
    private Cache getRegion(String regionName) {
        Cache region = cacheManager.getCache(regionName);
        if(region == null) {
            cacheManager.addCache(regionName);
            region = cacheManager.getCache(regionName);
        }
        
        return region;
    }
}
