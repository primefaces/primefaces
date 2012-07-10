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
import org.atmosphere.cpr.MetaBroadcaster;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Simple {@link org.atmosphere.cpr.AtmosphereHandler} who delegate HTTP METHOD GET to {@link PushRule}. See the
 * {@link DefaultPushRule}
 * <br/>
 * If your application needs to support POST for whatever reason, extends the {@link #onRequest(org.atmosphere.cpr.AtmosphereResource)}
 * method and add your logic there.
 * <strong>This AtmosphereHandler doesn't invoke the {@link org.atmosphere.cpr.AtmosphereResource#suspend()}.
 * The {@link org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor} is taking care of it</strong>
 */
public class PrimeAtmosphereHandler extends AbstractReflectorAtmosphereHandler {

    private final List<PushRule> rules;

    public PrimeAtmosphereHandler(List<PushRule> rules) {
        this.rules = rules;
    }

    public void onRequest(AtmosphereResource resource) throws IOException {
        AtmosphereRequest r = resource.getRequest();
        // We only handle GET. POST are supported by PrimeFaces directly via the Broadcaster.
        if (r.getMethod().equalsIgnoreCase("GET")) {
            applyRules(resource);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = null;
            try {
                InputStream inputStream = r.getRequest().getInputStream();
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
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex) {
                    }
                }
            }
            MetaBroadcaster.getDefault().broadcastTo("/*", stringBuilder.toString());
        }
    }

    protected void applyRules(AtmosphereResource resource) {
        boolean ok;
        for (PushRule r : rules) {
            ok = r.apply(resource);
            if (!ok) return;
        }
    }

    public void destroy() {
    }
}