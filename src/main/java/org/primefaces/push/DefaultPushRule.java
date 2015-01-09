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
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;

/**
 * The Default {@link PushRule} that creates channel based on the {@link org.atmosphere.cpr.AtmosphereRequest#getPathInfo()}.
 * @deprecated
 */
public class DefaultPushRule implements PushRule {

    /**
     * Creates channel (named {@link Broadcaster} in Atmosphere) based on the {@link org.atmosphere.cpr.AtmosphereRequest#getPathInfo()}
     * @param resource  An {@link AtmosphereResource}
     * @return
     */
    public boolean apply(AtmosphereResource resource) {
        String pathInfo = resource.getRequest().getPathInfo();
        BroadcasterFactory f = resource.getAtmosphereConfig().getBroadcasterFactory();
        if (pathInfo == null) {
            resource.setBroadcaster(f.lookup("/*"));
            return true;
        }

        final Broadcaster b = f.lookup(pathInfo, true);
        resource.setBroadcaster(b);

        return true;
    }
}
