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

import javax.validation.ValidatorFactory;
import org.primefaces.cache.CacheProvider;

import org.primefaces.config.ConfigContainer;

/**
 * A {@link ApplicationContext} is a contextual store, similar to {@link RequestContext}.
 * Only one {@link ApplicationContext} should be available in the application.
 *
 * It can be accessed via:
 * <blockquote>
 *  RequestContext.getCurrentInstance().getApplicationContext();
 * </blockquote>
 */
public abstract class ApplicationContext {

	public abstract ConfigContainer getConfig();
	
	public abstract ValidatorFactory getValidatorFactory();
    
    public abstract CacheProvider getCacheProvider();
}
