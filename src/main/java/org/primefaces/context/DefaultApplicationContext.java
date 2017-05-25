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
package org.primefaces.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.primefaces.cache.CacheProvider;
import org.primefaces.cache.DefaultCacheProvider;

import org.primefaces.config.PrimeConfiguration;
import org.primefaces.util.Constants;

public class DefaultApplicationContext extends ApplicationContext {

	private PrimeConfiguration config;
	private ValidatorFactory validatorFactory;
    private Validator validator;
    private CacheProvider cacheProvider;
    private Map<Class<?>, Map<String, Object>> enumCacheMap;
    private Map<Class<?>, Map<String, Object>> constantsCacheMap;

    public DefaultApplicationContext(FacesContext context) {
        this(context, new PrimeConfiguration(context));
    }
    
    public DefaultApplicationContext(FacesContext context, PrimeConfiguration config) {
    	this.config = config;
    	
    	if (this.config.isBeanValidationAvailable()) {
    	    this.validatorFactory = Validation.buildDefaultValidatorFactory();
            this.validator = validatorFactory.getValidator();
    	}
        
        enumCacheMap = new ConcurrentHashMap<Class<?>, Map<String, Object>>();
        constantsCacheMap = new ConcurrentHashMap<Class<?>, Map<String, Object>>();
    }

	@Override
	public PrimeConfiguration getConfig() {
		return config;
	}

    @Override
    public ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    @Override
    public CacheProvider getCacheProvider() {
        
        if (cacheProvider == null) {
            initCacheProvider();
        }
        
        return cacheProvider;
    }
    
    /**
     * Lazy init cache provider. Not required if no cache component is used in the application.
     */
    protected synchronized void initCacheProvider() {
        if (cacheProvider == null) {
            String cacheProviderConfigValue = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(Constants.ContextParams.CACHE_PROVIDER);
            if(cacheProviderConfigValue == null) {
                cacheProvider = new DefaultCacheProvider();
            }
            else {
                try {
                    cacheProvider = (CacheProvider) Class.forName(cacheProviderConfigValue).newInstance();
                } 
                catch (ClassNotFoundException ex) {
                    throw new FacesException(ex);
                } 
                catch (InstantiationException ex) {
                    throw new FacesException(ex);
                } 
                catch (IllegalAccessException ex) {
                    throw new FacesException(ex);
                }
            }
        }
    }

    @Override
    public Map<Class<?>, Map<String, Object>> getEnumCacheMap() {
        return enumCacheMap;
    }

    @Override
    public Map<Class<?>, Map<String, Object>> getConstantsCacheMap() {
        return constantsCacheMap;
    }

    @Override
    public Validator getValidator() {
        return validator;
    }

    @Override
    public void release() {
        if (validatorFactory != null && config != null && config.isAtLeastBV11()) {
            validatorFactory.close();
        }
    }

}
