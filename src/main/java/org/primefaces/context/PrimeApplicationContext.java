/**
 * Copyright 2009-2019 PrimeTek.
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
import javax.servlet.ServletContext;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.primefaces.cache.CacheProvider;
import org.primefaces.cache.DefaultCacheProvider;

import org.primefaces.config.PrimeConfiguration;
import org.primefaces.config.PrimeEnvironment;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.virusscan.VirusScannerService;

/**
 * A {@link PrimeApplicationContext} is a contextual store for the current application.
 *
 * It can be accessed via:
 * <blockquote>
 * PrimeApplicationContext.getCurrentInstance(context)
 * </blockquote>
 */
public class PrimeApplicationContext {

    public static final String INSTANCE_KEY = PrimeApplicationContext.class.getName();

    private ClassLoader applicationClassLoader;
    private PrimeEnvironment environment;
    private PrimeConfiguration config;
    private ValidatorFactory validatorFactory;
    private Validator validator;
    private CacheProvider cacheProvider;
    private Map<Class<?>, Map<String, Object>> enumCacheMap;
    private Map<Class<?>, Map<String, Object>> constantsCacheMap;
    private VirusScannerService virusScannerService;

    public PrimeApplicationContext(FacesContext context) {
        this.environment = new PrimeEnvironment(context);
        this.config = new PrimeConfiguration(context, environment);

        if (this.config.isBeanValidationEnabled()) {
            this.validatorFactory = Validation.buildDefaultValidatorFactory();
            this.validator = validatorFactory.getValidator();
        }

        enumCacheMap = new ConcurrentHashMap<>();
        constantsCacheMap = new ConcurrentHashMap<>();

        if (environment.isPortlet()) {
            //the method is new in Porlets3.x, so we can't use it now
            //applicationClassLoader = ((PortletContext) context.getExternalContext().getContext()).getClassLoader();
            applicationClassLoader = LangUtils.getContextClassLoader();
        }
        else if (context.getExternalContext().getContext() instanceof ServletContext) {
            applicationClassLoader = ((ServletContext) context.getExternalContext().getContext()).getClassLoader();
        }
    }

    public static PrimeApplicationContext getCurrentInstance(FacesContext facesContext) {
        if (facesContext == null || facesContext.getExternalContext() == null) {
            return null;
        }

        PrimeApplicationContext applicationContext =
                (PrimeApplicationContext) facesContext.getExternalContext().getApplicationMap().get(INSTANCE_KEY);

        if (applicationContext == null) {
            applicationContext = new PrimeApplicationContext(facesContext);
            setCurrentInstance(applicationContext, facesContext);
        }

        return applicationContext;
    }

    public static PrimeApplicationContext getCurrentInstance(ServletContext context) {
        return (PrimeApplicationContext) context.getAttribute(INSTANCE_KEY);
    }

    public static void setCurrentInstance(final PrimeApplicationContext context, final FacesContext facesContext) {
        facesContext.getExternalContext().getApplicationMap().put(INSTANCE_KEY, context);

        if (facesContext.getExternalContext().getContext() instanceof ServletContext) {
            ((ServletContext) facesContext.getExternalContext().getContext()).setAttribute(INSTANCE_KEY, context);
        }
    }

    public PrimeEnvironment getEnvironment() {
        return environment;
    }

    public PrimeConfiguration getConfig() {
        return config;
    }

    public ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    public CacheProvider getCacheProvider() {
        if (cacheProvider == null) {
            initCacheProvider();
        }

        return cacheProvider;
    }

    /**
     * Lazy init cacheProvider. Not required if no cache component is used in the application.
     */
    protected synchronized void initCacheProvider() {
        if (cacheProvider == null) {
            String cacheProviderConfigValue = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(Constants.ContextParams.CACHE_PROVIDER);
            if (cacheProviderConfigValue == null) {
                cacheProvider = new DefaultCacheProvider();
            }
            else {
                try {
                    cacheProvider = (CacheProvider) LangUtils.loadClassForName(cacheProviderConfigValue).newInstance();
                }
                catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    throw new FacesException(ex);
                }
            }
        }
    }

    public Map<Class<?>, Map<String, Object>> getEnumCacheMap() {
        return enumCacheMap;
    }

    public Map<Class<?>, Map<String, Object>> getConstantsCacheMap() {
        return constantsCacheMap;
    }

    public Validator getValidator() {
        return validator;
    }

    public VirusScannerService getVirusScannerService() {
        if (virusScannerService == null) {
            initVirusScannerService();
        }

        return virusScannerService;
    }

    protected synchronized void initVirusScannerService() {
        if (virusScannerService == null) {
            virusScannerService = new VirusScannerService(applicationClassLoader);
        }
    }

    public void release() {
        if (validatorFactory != null && environment != null && environment.isAtLeastBv11()) {
            validatorFactory.close();
        }
    }
}
