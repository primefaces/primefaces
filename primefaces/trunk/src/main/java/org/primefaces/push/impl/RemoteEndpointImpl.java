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

import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.FrameworkConfig;
import org.primefaces.push.RemoteEndpoint;
import org.primefaces.push.Status;

import java.util.Map;

public class RemoteEndpointImpl implements RemoteEndpoint {

    private final AtmosphereRequest request;
    private final String body;
    private final Status status = new Status(Status.STATUS.OPEN);

    public RemoteEndpointImpl(AtmosphereRequest request, String body) {
        this.request = request;
        this.body = body;
    }

    //@Override
    public Map<String, String> headersMap(){
        return request.headersMap();
    }

    //@Override
    public Map<String, String[]> queryStrings() {
        return request.queryStringsMap();
    }

    //@Override
    public AtmosphereResource.TRANSPORT transport() {
        return request.resource().transport();
    }

    //@Override
    public String path() {
        // WARNING, if this method returns null, that means Atmosphere 2.0.x is used.
        return (String) request.getAttribute(FrameworkConfig.MAPPED_PATH);
    }

    //@Override
    public String uri() {
        return request.getRequestURI();
    }

    //@Override
    public String body() {
        return body;
    }

    //@Override
    public Status status() {
        return status;
    }

    public String address() {
        return request.getRemoteAddr() + ":" + request.getRemotePort();
    }

    public RemoteEndpoint write(String message) {
        request.resource().write(message);
        return this;
    }

    public RemoteEndpoint write(byte[] message) {
        request.resource().write(message);
        return this;
    }

    AtmosphereResource resource() {
        return request.resource();
    }

    @Override
    public String toString() {
        return "RemoteEndpointImpl{" +
                "request=" + request +
                ", uri='" + request.getRequestURI() + '\'' +
                ", status=" + status +
                '}';
    }
}
