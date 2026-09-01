/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.showcase.rest;

import org.primefaces.showcase.util.SessionStatsListener;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Exposes the session counters collected by {@link SessionStatsListener}, to see how much of the traffic
 * is bots hammering the showcase without accepting cookies.
 *
 * The endpoint is public unless a token is configured, either as the context-param "showcase.STATS_TOKEN"
 * or as the environment variable SHOWCASE_STATS_TOKEN. When configured, the same value must be passed as
 * "?token=" to get a response.
 */
@Path("/stats")
public class SessionStatsRestService {

    public static final String TOKEN_PARAM = "showcase.STATS_TOKEN";

    public static final String TOKEN_ENV = "SHOWCASE_STATS_TOKEN";

    @Context
    private ServletContext servletContext;

    @GET
    @Path("/sessions")
    @Produces({MediaType.APPLICATION_JSON})
    public Response sessions(@QueryParam("token") String token) {
        String expected = servletContext.getInitParameter(TOKEN_PARAM);
        if (expected == null || expected.isEmpty()) {
            expected = System.getenv(TOKEN_ENV);
        }

        if (expected != null && !expected.isEmpty() && !expected.equals(token)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(new SessionStats()).build();
    }

    public static class SessionStats {

        public long getActive() {
            return SessionStatsListener.getActive();
        }

        public long getCreated() {
            return SessionStatsListener.getCreated();
        }

        public long getDestroyed() {
            return SessionStatsListener.getDestroyed();
        }

        public long getPeakActive() {
            return SessionStatsListener.getPeakActive();
        }

        public long getUptimeSeconds() {
            return SessionStatsListener.getUptimeSeconds();
        }

        public long getCreatedPerMinute() {
            long uptimeSeconds = SessionStatsListener.getUptimeSeconds();
            return uptimeSeconds < 60 ? SessionStatsListener.getCreated() : SessionStatsListener.getCreated() * 60 / uptimeSeconds;
        }
    }
}
