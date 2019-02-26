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
 * This class should not be used during application startup.
 */
public class PrimeApplicationContext {

    public static final String INSTANCE_KEY = PrimeApplicationContext.class.getName();

    private final PrimeEnvironment environment;
    private final PrimeConfiguration config;
    private final ValidatorFactoryAccessor validatorFactoryAccessor;
    private final ValidatorAccessor validatorAccessor;
    private final CacheProviderAccessor cacheProviderAccessor;
    private final Map<Class<?>, Map<String, Object>> enumCacheMap;
    private final Map<Class<?>, Map<String, Object>> constantsCacheMap;
    private final VirusScannerServiceAccessor virusScannerServiceAccessor;

    public PrimeApplicationContext(ClassLoader applicationClassLoader, PrimeEnvironment environment,
        PrimeConfiguration config) {

        this.environment = environment;
        this.config = config;
        validatorFactoryAccessor = new ValidatorFactoryAccessor(config);
        validatorAccessor = new ValidatorAccessor(validatorFactoryAccessor);
        cacheProviderAccessor = new CacheProviderAccessor(applicationClassLoader);
        virusScannerServiceAccessor = new VirusScannerServiceAccessor(applicationClassLoader);
        enumCacheMap = new ConcurrentHashMap<>();
        constantsCacheMap = new ConcurrentHashMap<>();
    }

    public static PrimeApplicationContext getCurrentInstance(FacesContext facesContext) {
        if (facesContext == null || facesContext.getExternalContext() == null) {
            return null;
        }

        return (PrimeApplicationContext) facesContext.getExternalContext().getApplicationMap().get(INSTANCE_KEY);
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
        return validatorFactoryAccessor.get();
    }

    private static final class ValidatorFactoryAccessor extends ThreadSafeLazyAccessor<ValidatorFactory> {

        private final PrimeConfiguration config;

        public ValidatorFactoryAccessor(PrimeConfiguration config) {
            this.config = config;
        }

        @Override
        protected ValidatorFactory computeValue() {

            if (config.isBeanValidationEnabled()) {
                return Validation.buildDefaultValidatorFactory();
            }
            else {
                return null;
            }
        }
    }

    public CacheProvider getCacheProvider() {
        return cacheProviderAccessor.get();
    }

    private static final class CacheProviderAccessor extends ThreadSafeLazyAccessor<CacheProvider> {

        private final ClassLoader applicationClassLoader;

        public CacheProviderAccessor(ClassLoader applicationClassLoader) {
            this.applicationClassLoader = applicationClassLoader;
        }

        @Override
        protected CacheProvider computeValue() {

            CacheProvider cacheProvider = null;
            String cacheProviderConfigValue = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(Constants.ContextParams.CACHE_PROVIDER);
            if (cacheProviderConfigValue == null) {
                cacheProvider = new DefaultCacheProvider();
            }
            else {
                try {
                    cacheProvider = (CacheProvider) LangUtils
                        .loadClassForName(cacheProviderConfigValue, applicationClassLoader).newInstance();
                }
                catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    throw new FacesException(ex);
                }
            }

            return cacheProvider;
        }
    }

    public Map<Class<?>, Map<String, Object>> getEnumCacheMap() {
        return enumCacheMap;
    }

    public Map<Class<?>, Map<String, Object>> getConstantsCacheMap() {
        return constantsCacheMap;
    }

    public Validator getValidator() {
        return validatorAccessor.get();
    }

    private static final class ValidatorAccessor extends ThreadSafeLazyAccessor<Validator> {

        private final ValidatorFactoryAccessor validatorFactoryAccessor;

        public ValidatorAccessor(ValidatorFactoryAccessor validatorFactoryAccessor) {
            this.validatorFactoryAccessor = validatorFactoryAccessor;
        }

        @Override
        protected Validator computeValue() {

            ValidatorFactory validatorFactory = validatorFactoryAccessor.get();

            if (validatorFactory != null) {
                return validatorFactory.getValidator();
            }
            else {
                return null;
            }
        }
    }

    public VirusScannerService getVirusScannerService() {
        return virusScannerServiceAccessor.get();
    }

    private static final class VirusScannerServiceAccessor extends ThreadSafeLazyAccessor<VirusScannerService> {

        private final ClassLoader applicationClassLoader;

        public VirusScannerServiceAccessor(ClassLoader applicationClassLoader) {
            this.applicationClassLoader = applicationClassLoader;
        }

        @Override
        protected VirusScannerService computeValue() {
            return new VirusScannerService(applicationClassLoader);
        }
    }

    public void release() {

        ValidatorFactory validatorFactory = getValidatorFactory();

        if (validatorFactory != null && getEnvironment().isAtLeastBv11()) {
            validatorFactory.close();
        }
    }
}
