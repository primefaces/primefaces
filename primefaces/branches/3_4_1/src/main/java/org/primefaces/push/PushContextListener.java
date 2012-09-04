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

/**
 * Received {@link PushContext} notification when a message has been successfully pushed (@link PushContext#push) or
 * when a client disconnected (the connection has been closed).
 */
public interface PushContextListener {

    /**
     * Invoked when a message has been successfully pushed to channel.
     * @param channel A String used when calling the {@link PushContext#push(String, Object)}
     * @param message The message pushed.
     */
    void onComplete(String channel, Object message);

    /**
     * Return the original request that was suspended by the {@link PushServlet}.
     * @param request
     */
    void onDisconnect(Object request);

}

