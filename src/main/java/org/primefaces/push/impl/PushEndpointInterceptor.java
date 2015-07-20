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
package org.primefaces.push.impl;

import org.atmosphere.config.managed.ManagedServiceInterceptor;
import org.atmosphere.config.service.AtmosphereInterceptorService;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.handler.AnnotatedProxy;
import org.primefaces.push.annotation.PushEndpoint;

@AtmosphereInterceptorService
public class PushEndpointInterceptor extends ManagedServiceInterceptor {

    protected void mapAnnotatedService(boolean reMap, String path, AtmosphereRequest request, AtmosphereFramework.AtmosphereHandlerWrapper w) {
        synchronized (config.handlers()) {
            String servletPath = (String) config.properties().get("servletPath");
            if (path.startsWith(servletPath)) {
                path = path.substring(servletPath.length());
            }
        }
        super.mapAnnotatedService(reMap, path, request, w);
    }

    protected AnnotatedProxy proxyHandler() throws IllegalAccessException, InstantiationException {
        return config.framework().newClassInstance(AnnotatedProxy.class, PushEndpointHandlerProxy.class);
    }

    protected ManagedAnnotation managed(AnnotatedProxy ap, final AtmosphereResource r){
        final PushEndpoint a = ap.target().getClass().getAnnotation(PushEndpoint.class);
        if (a == null) return null;

        return new ManagedAnnotation() {
            public String path() {
                return a.value();
            }

            public Class<? extends Broadcaster> broadcaster() {
                return r.getBroadcaster().getClass();
            }
        };
    }

    @Override
    public String toString() {
        return "@PushEndpoint Interceptor";
    }
}
