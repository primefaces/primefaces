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

import org.atmosphere.config.managed.ServiceInterceptor;
import org.atmosphere.config.service.AtmosphereInterceptorService;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.FrameworkConfig;
import org.atmosphere.handler.AnnotatedProxy;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.annotation.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AtmosphereInterceptorService
public class PushEndpointInterceptor extends ServiceInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(PushEndpointInterceptor.class);

    protected void mapAnnotatedService(boolean reMap, String path, AtmosphereRequest request, AtmosphereFramework.AtmosphereHandlerWrapper w) {
        synchronized (config.handlers()) {
            String servletPath = (String) config.properties().get("servletPath");
            if (path.startsWith(servletPath)) {
                path = path.substring(servletPath.length());
            }

            if (config.handlers().get(path) == null) {
                if (AnnotatedProxy.class.isAssignableFrom(w.atmosphereHandler.getClass())) {
                    AnnotatedProxy ap = AnnotatedProxy.class.cast(w.atmosphereHandler);
                    PushEndpoint a = ap.target().getClass().getAnnotation(PushEndpoint.class);
                    if (a != null) {
                        String targetPath = a.value();
                        if (targetPath.indexOf("{") != -1 && targetPath.indexOf("}") != -1) {
                            try {
                                boolean singleton = ap.target().getClass().getAnnotation(Singleton.class) != null;
                                if (!singleton) {
                                    PushEndpointHandlerProxy h = config.framework().newClassInstance(PushEndpointHandlerProxy.class, PushEndpointHandlerProxy.class);
                                    h.configure(config, config.framework().newClassInstance(Object.class, ap.target().getClass()));
                                    config.framework().addAtmosphereHandler(path, h,
                                            config.getBroadcasterFactory().lookup(request.resource().getBroadcaster().getClass(), path, true), w.interceptors);
                                } else {
                                    config.framework().addAtmosphereHandler(path, w.atmosphereHandler,
                                            config.getBroadcasterFactory().lookup(request.resource().getBroadcaster().getClass(), path, true), w.interceptors);
                                }
                                request.setAttribute(FrameworkConfig.NEW_MAPPING, "true");
                            } catch (Throwable e) {
                                logger.warn("Unable to create AtmosphereHandler", e);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "@PushEndpoint Interceptor";
    }
}
