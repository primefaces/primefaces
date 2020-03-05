# Cache
Cache component is used to reduce page load time by caching the content after initial rendering.

## Info

| Name | Value |
| --- | --- |
| Tag | cache
| Component Class | org.primefaces.component.cache.UICache
| Component Type | org.primefaces.component.Cache
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.UICacheRenderer
| Renderer Class | org.primefaces.component.cache.UICacheRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| disabled | false | Boolean | Disables caching.
| region | View Id | String | Unique id of the cache region, defaults to view id.
| key | null | String | Unique id of the cache entry in region, defaults to client id of component.
| processEvents | false | Boolean | When enabled, lifecycle events such as button actions are executed.

## Getting Started with Cache
A cache store is required to use the cache component, two different providers are supported as cache
implementation; Default (org.primefaces.cache.DefaultCacheProvider based on ConcurrentHashMap), EHCache 2 (org.primefaces.cache.EHCacheProvider), EHCache 3 (org.primefaces.cache.EHCache3Provider) and Hazelcast (org.primefaces.cache.HazelcastCacheProvider).

Provider is configured via a context-param.

```xml
<context-param>
    <param-name>primefaces.CACHE_PROVIDER</param-name>
    <param-value>org.primefaces.cache.EHCacheProvider</param-value>
</context-param>
```
Here is a sample ehcache.xml to configure cache regions, there are two regions in this
configuration.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="ehcache.xsd"
    updateCheck="true" monitoring="autodetect"
    dynamicConfig="true">
    
    <diskStore path="java.io.tmpdir"/>
    
    <defaultCache maxEntriesLocalHeap="10000" eternal="false" timeToIdleSeconds="120" timeToLiveSeconds="120"
        diskSpoolBufferSizeMB="30" maxEntriesLocalDisk="10000000" diskExpiryThreadIntervalSeconds="120" memoryStoreEvictionPolicy="LRU">
        <persistence strategy="localTempSwap"/>
    </defaultCache>

    <cache name="testcache" maxEntriesLocalHeap="10000" eternal="false" timeToIdleSeconds="120" timeToLiveSeconds="120"
        diskSpoolBufferSizeMB="30" maxEntriesLocalDisk="10000000" diskExpiryThreadIntervalSeconds="120" memoryStoreEvictionPolicy="LRU">    
        <persistence strategy="localTempSwap"/>
    </cache>

</ehcache>
```
After the configuration, at UI side, the cached part needs to be wrapped inside the p:cache
component.

```xhtml
<p:cache>
    //content to cache
</p:cache>
```
Once the page is loaded initially, content inside p:cache component is cached inside the cache
region of the cache provider. Postbacks on the same page or reopening the page retrieve the output
from cache instead of rendering the content regularly.

## Cache Provider API
CacheProvider can be accessed via;

_PrimeRequestContext.getCurrentInstance().getApplicationContext().getCacheProvider()_

For example using this API, all cache regions can be cleaned using _clear()_ method. Refer to javadoc
of CacheProvider for the full list of available methods.

