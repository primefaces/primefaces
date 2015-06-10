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
    private final String path;
    private final String[] pathSegments;
    private final AtmosphereResource resource;

    public RemoteEndpointImpl(AtmosphereRequest request, String body) {
        this.request = request;
        this.body = body;
        this.path = (String) request.getAttribute(FrameworkConfig.MAPPED_PATH);
        this.pathSegments = path.split("/");
        this.resource = request.resource();
    }

    //@Override
    public Map<String, String> headersMap() {
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
        return path;
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

    //@Override
    public String address() {
        return request.getRemoteAddr() + ":" + request.getRemotePort();
    }

    //@Override
    public String pathSegments(int position) {
        if (position < pathSegments.length) {
            return pathSegments[position];
        }
        return null;
    }

    //@Override
    public boolean isOpen() {
        return resource.isSuspended();
    }

    public RemoteEndpoint write(String message) {
        resource.write(message);
        return this;
    }

    public RemoteEndpoint write(byte[] message) {
        resource.write(message);
        return this;
    }

    public AtmosphereResource resource() {
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
