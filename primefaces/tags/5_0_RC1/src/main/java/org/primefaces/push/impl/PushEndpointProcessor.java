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

import org.atmosphere.annotation.Processor;
import org.atmosphere.config.AtmosphereAnnotation;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.util.IOUtils;
import org.primefaces.push.annotation.PushEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.atmosphere.annotation.AnnotationUtil.broadcaster;

@AtmosphereAnnotation(PushEndpoint.class)
public class PushEndpointProcessor implements Processor<Object> {

    private static final Logger logger = LoggerFactory.getLogger(PushEndpointProcessor.class);

    //@Override
    public void handle(AtmosphereFramework framework, Class<Object> annotatedClass) {
        try {
            Class<?> aClass = annotatedClass;
            PushEndpoint a = aClass.getAnnotation(PushEndpoint.class);
            List<AtmosphereInterceptor> l = new ArrayList<AtmosphereInterceptor>();

            Object c = framework.newClassInstance(Object.class, aClass);
            AtmosphereHandler handler = framework.newClassInstance(PushEndpointHandlerProxy.class, PushEndpointHandlerProxy.class).configure(framework.getAtmosphereConfig(), c);
            l.add(framework.newClassInstance(AtmosphereInterceptor.class, PushEndpointInterceptor.class));

            Class<? extends Broadcaster> b = (Class<? extends Broadcaster>) IOUtils.loadClass(this.getClass(), framework.getDefaultBroadcasterClassName());

            framework.addAtmosphereHandler(a.value(), handler, broadcaster(framework, b, a.value()), l);
        } catch (Throwable e) {
            logger.warn("", e);
        }
    }

}
