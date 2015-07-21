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

import org.atmosphere.cpr.AtmosphereResource;

import java.util.Map;

/**
 * A representation of a remote client.
 */
public interface RemoteEndpoint {
    /**
     * Return the HTTP Headers
     *
     * @return a Map of headers
     */
    Map<String, String> headersMap();

    /**
     * Return the HTTP Query String
     *
     * @return a Map of Query String Value
     */
    Map<String, String[]> queryStrings();

    /**
     * Return the underlying Atmosphere's Transport used.
     *
     * @return
     */
    AtmosphereResource.TRANSPORT transport();

    /**
     * The path matching the associated {@link org.primefaces.push.annotation.PushEndpoint}
     *
     * @return
     */
    String path();

    /**
     * Return the request URI
     *
     * @return
     */
    String uri();

    /**
     * Return a String representation of the request's body
     *
     * @return a String representation of the request's body
     */
    String body();

    /**
     * The current {@link Status}
     *
     * @return {@link Status}
     */
    Status status();

    /**
     * Return the remote client IP
     *
     * @return the remote client IP
     */
    String address();

    /**
     * Return the {@link #path()}'s segment value at the specified position, or null if there is no value at that position
     *
     * @param position the path segment
     * @return the value, or null if not found.
     */
    String pathSegments(int position);

    /**
     * Is the underlying connection to the Browser still open.
     *
     * @return
     */
    boolean isOpen();

    /**
     * The underlying {@link AtmosphereResource} in case the {@link org.atmosphere.cpr.AtmosphereRequest} or {@link AtmosphereResource}
     * is needed
     *
     * @return {@link AtmosphereResource}
     */
    AtmosphereResource resource();

}
