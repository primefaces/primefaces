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


import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A distributed lightweight event bus which can encompass multiple PushEndpoint instances. The event bus implements publish / subscribe and point to point messaging.
 * Messages sent over the event bus can be of any type. For publish / subscribe, messages can be published to a Java class annotated with the {@link org.primefaces.push.annotation.PushEndpoint#value}
 * using one of the method annotated with {@link org.atmosphere.handler.OnMessage}.
 * <p/>
 * {@link org.primefaces.push.annotation.PushEndpoint} are registered against a path, defined using the {@link org.primefaces.push.annotation.PushEndpoint#value}. There can be multiple PushEndpoint registered against each path,
 * and a particular PushEndpoint can be registered against multiple paths. The event bus will route a sent message to all PushEndpoint which are registered against that path.
 * When sending a message, a {@link Reply} can be provided. If so, it will be called when the reply from the receiver has been completed.
 * Instances of EventBus are thread-safe.
 */
public interface EventBus {

    /**
     * Fire an object to all connected {@link RemoteEndpoint}
     *
     * @param o an Object
     * @return this
     */
    EventBus publish(Object o);

    /**
     * Fire an object to {@link RemoteEndpoint} that connected using the path value.
     *
     * @param path A String starting with '/'
     * @param o    an Object
     * @return this
     */
    EventBus publish(String path, Object o);

    /**
     * Fire an object to {@link RemoteEndpoint} that connected using the path value.
     *
     * @param path A String starting with '/'
     * @param o    an Object
     * @param r    an {@link Reply}
     * @return this
     */
    EventBus publish(String path, Object o, Reply r);

    /**
     * Schedule a period push operation.
     * @param path a channel of communication.
     * @param t a message
     * @param time the time
     * @param unit the {@link @TimeUnit}
     * @param <T> The type of the message
     * @return a Future that can used to cancel the periodic push
     */
    <T> Future<T> schedule(String path, T t, int time, TimeUnit unit);

    public static interface Reply {

        /**
         * Invoked when the {@link org.primefaces.push.EventBus} delivered the message to all {@link org.primefaces.push.annotation.PushEndpoint}
         * that matche the path used to initiate the publish operation.
         */
        void completed(String path);

    }

}
