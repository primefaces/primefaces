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

import org.atmosphere.config.managed.Decoder;
import org.atmosphere.config.managed.Encoder;
import org.atmosphere.config.managed.Invoker;
import org.atmosphere.config.managed.ManagedServiceInterceptor;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.cpr.AtmosphereResourceImpl;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.PerRequestBroadcastFilter;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;
import org.atmosphere.handler.AnnotatedProxy;
import org.atmosphere.util.IOUtils;
import org.primefaces.push.EventBus;
import org.primefaces.push.RemoteEndpoint;
import org.primefaces.push.Status;
import org.primefaces.push.annotation.OnClose;
import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.OnOpen;
import org.primefaces.push.annotation.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class PushEndpointHandlerProxy extends AbstractReflectorAtmosphereHandler implements AnnotatedProxy {

    private Logger logger = LoggerFactory.getLogger(PushEndpointHandlerProxy.class);
    private Object proxiedInstance;
    private List<Method> onMessageMethods;
    private Method onCloseMethod;
    private Method onTimeoutMethod;
    private Method onOpenMethod;
    private Method onResumeMethod;
    private AtmosphereConfig config;
    private EventBus eventBus;
    private boolean injectEventBus = false;
    private boolean injectEndpoint = false;
    private boolean pathParams;

    final Map<Method, List<Encoder<?, ?>>> encoders = new HashMap<Method, List<Encoder<?, ?>>>();
    final Map<Method, List<Decoder<?, ?>>> decoders = new HashMap<Method, List<Decoder<?, ?>>>();

    private final Set<String> trackedUUID = Collections.synchronizedSet(new HashSet<String>());


    // Duplicate message because of Atmosphere 2.2.x API Changes from 2.1.x
    private final BroadcastFilter onMessageFilter = new BroadcastFilter() {
        //@Override
        public BroadcastAction filter(Object originalMessage, Object message) {
            return invoke(null, originalMessage, message);

        }

        public BroadcastAction filter(String broadccasterId, Object originalMessage, Object message) {
            return filter(originalMessage, message);
        }
    };

    // Duplicate message because of Atmosphere 2.2.x API Changes from 2.1.x
    private final BroadcastFilter onPerMessageFilter = new PerRequestBroadcastFilter() {

        //@Override
        public BroadcastAction filter(Object originalMessage, Object message) {
            return invoke(null, originalMessage, message);
        }

        //@Override
        public BroadcastAction filter(AtmosphereResource r, Object originalMessage, Object message) {
            RemoteEndpointImpl rm = (RemoteEndpointImpl) r.getRequest().getAttribute(RemoteEndpointImpl.class.getName());
            return invoke(rm, originalMessage, message);
        }

        //@Override
        public BroadcastAction filter(String broadccasterId, Object originalMessage, Object message) {
            return filter(originalMessage, message);
        }

        //@Override
        public BroadcastAction filter(String broadccasterId, AtmosphereResource r, Object originalMessage, Object message) {
            return filter(r, originalMessage, message);
        }
    };

    private BroadcastFilter.BroadcastAction invoke(RemoteEndpoint r, Object originalMessage, Object message) {
        Object o;
        o = message(r, message);
        if (o != null) {
            return new BroadcastFilter.BroadcastAction(BroadcastFilter.BroadcastAction.ACTION.CONTINUE, o);
        }
        return new BroadcastFilter.BroadcastAction(BroadcastFilter.BroadcastAction.ACTION.CONTINUE, message);
    }

    public PushEndpointHandlerProxy() {
    }

    public AnnotatedProxy configure(AtmosphereConfig config, Object c) {
        this.proxiedInstance = c;
        this.onMessageMethods = populateMessage(c, OnMessage.class);
        this.onCloseMethod = populate(c, OnClose.class);
        this.onTimeoutMethod = populate(c, OnClose.class);
        this.onOpenMethod = populate(c, OnOpen.class);
        this.onResumeMethod = populate(c, OnClose.class);
        this.config = config;
        this.eventBus = (EventBus) config.properties().get("evenBus");
        this.pathParams = pathParams(c);

        if (onMessageMethods.size() > 0) {
            populateEncoders();
            populateDecoders();
        }
        return this;
    }

    //@Override
    public void onRequest(final AtmosphereResource resource) throws IOException {
        final AtmosphereRequest request = resource.getRequest();

        String body = IOUtils.readEntirely(resource).toString();

        final RemoteEndpointImpl remoteEndpoint = new RemoteEndpointImpl(request, body);
        String method = request.getMethod();

        resource.getBroadcaster().getBroadcasterConfig().addFilter(injectEndpoint ? onPerMessageFilter : onMessageFilter);

        request.setAttribute(RemoteEndpointImpl.class.getName(), remoteEndpoint);

        if (onOpenMethod != null) {
            resource.addEventListener(new AtmosphereResourceEventListenerAdapter() {
                @Override
                public void onSuspend(AtmosphereResourceEvent event) {
                    try {
                        if (!trackedUUID.add(resource.uuid())) return;

                        // TODO: Document this behavior
                        // Temporary remove the resource from being the target for event, to avoid long-poling loop.
                        event.broadcaster().removeAtmosphereResource(resource);
                        try {
                            invokeOpenOrClose(onOpenMethod, remoteEndpoint);
                        } finally {
                            event.broadcaster().addAtmosphereResource(resource);
                        }
                    } finally {
                        event.getResource().removeEventListener(this);
                    }
                }
            });
        }

        if (onResumeMethod != null) {
            resource.addEventListener(new AtmosphereResourceEventListenerAdapter() {
                @Override
                public void onResume(AtmosphereResourceEvent event) {
                    if (event.isResumedOnTimeout()) {
                        try {
                            invokeOpenOrClose(onResumeMethod, remoteEndpoint);
                        } finally {
                            event.getResource().removeEventListener(this);
                        }
                    }
                }
            });
        }

        if (method.equalsIgnoreCase("post")) {
            resource.getRequest().body(body);

            if (!body.isEmpty()) {
                Object o = null;
                try {
                    o = invoke(remoteEndpoint, body);
                } catch (IOException e) {
                    logger.error("", e);
                }
                if (o != null) {
                    resource.getBroadcaster().broadcast(o);
                }
            } else {
                logger.warn("{} received an empty body", ManagedServiceInterceptor.class.getSimpleName());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {

        // Original Value
        AtmosphereResourceImpl r = AtmosphereResourceImpl.class.cast(event.getResource());
        AtmosphereRequest request = r.getRequest(false);
        Boolean resumeOnBroadcast = r.resumeOnBroadcast();
        if (!resumeOnBroadcast) {
            // For legacy reason, check the attribute as well
            Object o = request.getAttribute(ApplicationConfig.RESUME_ON_BROADCAST);
            if (o != null && Boolean.class.isAssignableFrom(o.getClass())) {
                resumeOnBroadcast = Boolean.class.cast(o);
            }
        }

        // Disable resume so cached message can be send in one chunk.
        if (resumeOnBroadcast) {
            r.resumeOnBroadcast(false);
            request.setAttribute(ApplicationConfig.RESUME_ON_BROADCAST, false);
        }

        RemoteEndpointImpl remoteEndpoint = (RemoteEndpointImpl) request.getAttribute(RemoteEndpointImpl.class.getName());

        if (remoteEndpoint != null) {
            if (event.isCancelled() || event.isClosedByClient()) {
                remoteEndpoint.status().status(event.isCancelled() ? Status.STATUS.UNEXPECTED_CLOSE : Status.STATUS.CLOSED_BY_CLIENT);
                request.removeAttribute(RemoteEndpointImpl.class.getName());
                trackedUUID.remove(r.uuid());

                invokeOpenOrClose(onCloseMethod, remoteEndpoint);
            } else if (event.isResumedOnTimeout() || event.isResuming()) {
                remoteEndpoint.status().status(Status.STATUS.CLOSED_BY_TIMEOUT);
                request.removeAttribute(RemoteEndpointImpl.class.getName());

                invokeOpenOrClose(onTimeoutMethod, remoteEndpoint);
            } else {
                super.onStateChange(event);
            }
        }

        if (resumeOnBroadcast && r.isSuspended()) {
            r.resume();
        }
    }

    public boolean pathParams() {
        return pathParams;
    }

    protected boolean pathParams(Object o) {
        for (Field field : o.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PathParam.class)) {
                return true;
            }
        }
        return false;
    }

    public Object invoke(RemoteEndpointImpl resource, Object msg) throws IOException {
        return message(resource, msg);
    }

    private Method populate(Object c, Class<? extends Annotation> annotation) {
        for (Method m : c.getClass().getMethods()) {
            if (m.isAnnotationPresent(annotation)) {
                return m;
            }
        }
        return null;
    }

    private List<Method> populateMessage(Object c, Class<? extends Annotation> annotation) {
        ArrayList<Method> methods = new ArrayList<Method>();
        for (Method m : c.getClass().getMethods()) {
            if (m.isAnnotationPresent(annotation)) {

                Class<?>[] cls = m.getParameterTypes();
                List<Class<?>> classes = Arrays.asList(cls);
                if (classes.contains(EventBus.class)) {
                    injectEventBus = true;
                } else if (classes.contains(RemoteEndpoint.class)) {
                    injectEndpoint = true;
                }
                methods.add(m);
            }
        }
        return methods;
    }

    private void populateEncoders() {
        for (Method m : onMessageMethods) {
            List<Encoder<?, ?>> l = new CopyOnWriteArrayList<Encoder<?, ?>>();
            for (Class<? extends Encoder> s : m.getAnnotation(OnMessage.class).encoders()) {
                try {
                    l.add(config.framework().newClassInstance(Encoder.class, s));
                } catch (Exception e) {
                    logger.error("Unable to load encoder {}", s);
                }
            }
            encoders.put(m, l);
        }
    }

    private void populateDecoders() {
        for (Method m : onMessageMethods) {
            List<Decoder<?, ?>> l = new CopyOnWriteArrayList<Decoder<?, ?>>();
            for (Class<? extends Decoder> s : m.getAnnotation(OnMessage.class).decoders()) {
                try {
                    l.add(config.framework().newClassInstance(Decoder.class, s));
                } catch (Exception e) {
                    logger.error("Unable to load encoder {}", s);
                }
            }
            decoders.put(m, l);
        }
    }

    private Object message(RemoteEndpoint resource, Object o) {
        try {
            // TODO: this code is hacky, but documentation will clarify what is supported.
            // TODO: Improve Injection, allow EventBus without RemoteEndpoint Injection.
            for (Method m : onMessageMethods) {
                Object decoded = Invoker.decode(decoders.get(m), o);
                if (decoded == null) {
                    decoded = o;
                }
                Object objectToEncode = null;
                if (m.getParameterTypes().length == 3 && resource != null) {
                    objectToEncode = Invoker.invokeMethod(m, proxiedInstance, resource, eventBus, decoded);
                } else if (m.getParameterTypes().length == 2 && resource != null) {
                    if (!injectEventBus) {
                        objectToEncode = Invoker.invokeMethod(m, proxiedInstance, resource, decoded);
                    } else if (objectToEncode == null) {
                        objectToEncode = Invoker.invokeMethod(m, proxiedInstance, eventBus, decoded);
                    }
                } else {
                    objectToEncode = Invoker.invokeMethod(m, proxiedInstance, decoded);
                }

                if (objectToEncode != null) {
                    return Invoker.encode(encoders.get(m), objectToEncode);
                }
            }
        } catch (Throwable t) {
            logger.error("", t);
        }
        return null;
    }

    //@Override
    public Object target() {
        return proxiedInstance;
    }

    protected void invokeOpenOrClose(Method m, RemoteEndpointImpl r) {
        if (m == null) {
            return;
        }

        Object objectToEncode = null;
        if (m.getParameterTypes().length == 2) {
            Invoker.invokeMethod(m, proxiedInstance, r, eventBus);
        } else if (!injectEventBus) {
            Invoker.invokeMethod(m, proxiedInstance, r);
        } else if (objectToEncode == null) {
            Invoker.invokeMethod(m, proxiedInstance, eventBus);
        }
    }

    @Override
    public String toString() {
        return "PushEndpointHandlerProxy proxy for " + proxiedInstance.getClass().getName();
    }
}
