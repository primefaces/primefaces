/*
 * Copyright 2009-2012 Prime Teknoloji.
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
 * A PushContext is an object that can be used to send message to application connected to the {@link PushServlet}.
 * A communication's channel is always created when an application connects to the PushServlet, and a PushContext
 * can be used to push (or communicate) with the communication's channel. For example, let say an application
 * set a GET request to the Push Servlet in the form of
 * <blockquote>
 *   GET http://127.0.0.1:8080/foo/channel/chat
 * </blockquote>
 * The PushServlet will create a communication's channel named '/channel/chat'. Sending messages to that channel can
 * be executed by simply doing:
 * <blockquote>
 *   PushContextFactory.getDefault().getPushContext().push('/channel/chat', "some messages");
 * </blockquote>
 *
 * Using {@link #push(String, Object)}, you can asynchronous push message to one or more channel. You can also schedule periodic push
 * {@link #schedule(String, Object, int, java.util.concurrent.TimeUnit)}. You can also delay push operation by using the
 * {@link #delay(String, Object, int, java.util.concurrent.TimeUnit)}.
 */
public interface PushContext {

    /**
     * Push message to the one or more channel of communication.
     * @param channel a channel of communication.
     * @param t a message
     * @param <T> The type of the message
     * @return a Future that can be used to block until the push completes
     */
    <T> Future<T> push(String channel, T t);

    /**
     * Schedule a period push operation.
     * @param channel a channel of communication.
     * @param t a message
     * @param time the time
     * @param unit the {@link @TimeUnit}
     * @param <T> The type of the message
     * @return a Future that can used to cancel the periodic push
     */
    <T> Future<T> schedule(String channel, T t, int time, TimeUnit unit);

    /**
     * Delay the push operation until the time expires.
     * @param channel a channel of communication.
     * @param t a message
     * @param time the time
     * @param unit the {@link @TimeUnit}
     * @param <T> The type of the message
     * @return a Future that can used to cancel the delayed push
     */
    <T> Future<T> delay(String channel, T t, int time, TimeUnit unit);

    /**
     * Add an event listener.
     * @param p {@link PushContextListener}
     * @return this
     */
    PushContext addListener(PushContextListener p);

    /**
     * Remove a event listener.
     * @param p {@link PushContextListener}
     * @return this
     */
    PushContext removeListener(PushContextListener p);

}
