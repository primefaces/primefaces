/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.push.impl;

import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereObjectFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Basic CDI Injector for {@link org.primefaces.push.annotation.PushEndpoint}. Support {@link Inject}, {@link PostConstruct} and ...
 */
public class PushObjectFactory implements AtmosphereObjectFactory {
    public <T, U extends T> T newClassInstance(AtmosphereFramework framework, Class<T> classType, Class<U> defaultType) throws InstantiationException, IllegalAccessException {

        T instance = defaultType.newInstance();

        // TODO: Allow injection of method as well, here or in PushAtmospheeProxy.

        Field[] fields = defaultType.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Inject.class)) {
                if (field.getType().isAssignableFrom(ServletContext.class)) {
                    field.set(instance, framework.getServletContext());
                }
            }
        }

        Method[] methods = defaultType.getMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(PostConstruct.class)) {
                try {
                    m.invoke(instance);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return instance;
    }
}
