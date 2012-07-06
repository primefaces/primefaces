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

import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.BroadcasterLifeCyclePolicyListener;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.websocket.WebSocketEventListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.atmosphere.cpr.BroadcasterLifeCyclePolicy.EMPTY_DESTROY;

public class PushServlet extends AtmosphereServlet {

    @Override
    public void init(final ServletConfig sc) throws ServletException {
        super.init(sc);

        framework.interceptor(new AtmosphereResourceLifecycleInterceptor())
                .addAtmosphereHandler("/*", new PrimeAtmosphereHandler())
                .initAtmosphereHandler(sc);
    }

    public final static class PrimeAtmosphereHandler extends AbstractReflectorAtmosphereHandler {

        public void onRequest(AtmosphereResource resource) throws IOException {
            AtmosphereRequest r = resource.getRequest();
            if (r.getMethod().equalsIgnoreCase("GET")) {
                resource.setBroadcaster(lookupBroadcaster(r.getPathInfo(), true))
                        .addEventListener(new WebSocketEventListenerAdapter());
            } else {
                String message = loadRequestBody(r.getInputStream());
                if (message != null) {
                    Broadcaster b = lookupBroadcaster(r.getPathInfo(), false);
                    if (b != null) {
                        b.broadcast(message);
                    }
                }
            }
        }

        Broadcaster lookupBroadcaster(String pathInfo, boolean create) {
            if (pathInfo == null) {
                pathInfo = "/root";
            }
            String[] decodedPath = pathInfo.split("/");
            final Broadcaster b = BroadcasterFactory.getDefault().lookup("/" + decodedPath[decodedPath.length - 1], create);
            if (create) {
                b.setBroadcasterLifeCyclePolicy(EMPTY_DESTROY);
                b.addBroadcasterLifeCyclePolicyListener(new BroadcasterLifeCyclePolicyListener() {

                    private final Logger logger = LoggerFactory.getLogger(BroadcasterLifeCyclePolicyListener.class);

                    public void onEmpty() {
                        logger.trace("onEmpty {}", b.getID());
                    }

                    public void onIdle() {
                        logger.trace("onIdle {}", b.getID());
                    }

                    public void onDestroy() {
                        logger.trace("onDestroy {}", b.getID());
                    }
                });
            }
            return b;
        }

        private String loadRequestBody(ServletInputStream inputStream) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = null;
            try {
                if (inputStream != null) {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    char[] charBuffer = new char[8192];
                    int bytesRead = -1;
                    while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                        stringBuilder.append(charBuffer, 0, bytesRead);
                    }
                } else {
                    stringBuilder.append("");
                }
            } catch (IOException ex) {
                logger.warn("", ex);
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex) {
                        logger.warn("", ex);
                    }
                }
            }
            return stringBuilder.toString();
        }

        public void destroy() {
        }
    }


}
