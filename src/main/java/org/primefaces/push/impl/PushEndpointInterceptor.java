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

import org.atmosphere.config.service.AtmosphereInterceptorService;
import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereFramework.AtmosphereHandlerWrapper;
import org.atmosphere.cpr.AtmosphereInterceptorAdapter;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.FrameworkConfig;
import org.atmosphere.handler.AnnotatedProxy;
import org.atmosphere.interceptor.InvokationOrder;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.annotation.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AtmosphereInterceptorService
public class PushEndpointInterceptor extends AtmosphereInterceptorAdapter {

    private final static Logger logger = LoggerFactory.getLogger(PushEndpointInterceptor.class);
    private AtmosphereConfig config;
    private boolean wildcardMapping = false;

    public PushEndpointInterceptor() {
    }

    @Override
    public void configure(AtmosphereConfig config) {
        this.config = config;
        optimizeMapping();
    }

    @Override
    public Action inspect(AtmosphereResource r) {
        if (!wildcardMapping) return Action.CONTINUE;

        mapAnnotatedService(r.getRequest(), (AtmosphereHandlerWrapper)
                r.getRequest().getAttribute(FrameworkConfig.ATMOSPHERE_HANDLER_WRAPPER));

        return Action.CONTINUE;
    }

    protected void optimizeMapping() {
        for (String w : config.handlers().keySet()) {
            if (w.contains("{") && w.contains("}")) {
                wildcardMapping = true;
                break;
            }
        }
    }

    protected void mapAnnotatedService(AtmosphereRequest request, AtmosphereHandlerWrapper w) {
        Broadcaster b = w.broadcaster;

        String path;
        String pathInfo = null;
        try {
            pathInfo = request.getPathInfo();
        } catch (IllegalStateException ex) {
            // http://java.net/jira/browse/GRIZZLY-1301
        }

        if (pathInfo != null) {
            path = request.getServletPath() + pathInfo;
        } else {
            path = request.getServletPath();
        }

        if (path == null || path.isEmpty()) {
            path = "/";
        }

        // Remove the Broadcaster with curly braces
        if (b.getID().contains("{")) {
            config.getBroadcasterFactory().remove(b.getID());
        }

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
    public PRIORITY priority() {
        return InvokationOrder.AFTER_DEFAULT;
    }

    @Override
    public String toString() {
        return "@PushEndpoint Interceptor";
    }
}
