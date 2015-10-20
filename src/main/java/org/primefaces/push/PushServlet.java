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
package org.primefaces.push;

import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.cpr.MetaBroadcaster;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.primefaces.push.impl.PushEndpointProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * A {@link org.primefaces.push.PushServlet} is responsible for installing the {@link org.atmosphere.cpr.AtmosphereFramework} and bridge
 * the PrimeFaces runtime with Atmosphere's components.
 */
public class PushServlet extends AtmosphereServlet {
    private final Logger logger = LoggerFactory.getLogger(PushServlet.class.getName());

    /**
     * Create an Atmosphere Servlet.
     */
    public PushServlet() {
        this(false);
    }

    /**
     * Create an Atmosphere Servlet.
     *
     * @param isFilter true if this instance is used as an {@link org.atmosphere.cpr.AtmosphereFilter}
     */
    public PushServlet(boolean isFilter) {
        super(isFilter, true);
    }

    /**
     * Create an Atmosphere Servlet.
     *
     * @param isFilter           true if this instance is used as an {@link org.atmosphere.cpr.AtmosphereFilter}
     * @param autoDetectHandlers
     */
    public PushServlet(boolean isFilter, boolean autoDetectHandlers) {
        super(isFilter, autoDetectHandlers);
    }


    protected PushServlet configureFramework(ServletConfig sc) throws ServletException {
        super.configureFramework(sc, false);
        framework().interceptor(new AtmosphereResourceLifecycleInterceptor())
                .interceptor(new TrackMessageSizeInterceptor())
                .addAnnotationPackage(PushEndpointProcessor.class);

        framework().getAtmosphereConfig().startupHook(new AtmosphereConfig.StartupHook() {
            public void started(AtmosphereFramework framework) {
                configureMetaBroadcasterCache(framework);
            }
        });

        boolean notConfigured = sc.getInitParameter(ApplicationConfig.READ_GET_BODY) == null;
        if (notConfigured) {
            // For Backward compatibility with Atmosphere 2.2.x
            framework().addInitParameter(ApplicationConfig.READ_GET_BODY, "true");
        }

        framework().init(sc);

        PushContextFactory pcf = new PushContextFactory(framework().getAtmosphereConfig().metaBroadcaster());
        PushContext c = pcf.getPushContext();
        if (PushContextImpl.class.isAssignableFrom(c.getClass())) {
            framework().asyncSupportListener(PushContextImpl.class.cast(c));
        }

        try {
            framework().newClassInstance(EventBusFactory.class,EventBusFactory.class);
        } catch (Exception ex) {
            logger.warn("", ex);
        }

        if (framework().getAtmosphereHandlers().size() == 0) {
            logger.error("No Annotated class using @PushEndpoint found. Push will not work.");
        }
        logger.debug("EventBus installed {}", EventBusFactory.getDefault().eventBus());
        return this;
    }

    protected void configureMetaBroadcasterCache(AtmosphereFramework framework){
        MetaBroadcaster m = framework.metaBroadcaster();
        m.cache(new MetaBroadcaster.ThirtySecondsCache(m, framework.getAtmosphereConfig()));
    }

}
