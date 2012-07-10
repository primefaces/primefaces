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

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.BroadcasterLifeCyclePolicyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.atmosphere.cpr.BroadcasterLifeCyclePolicy.EMPTY_DESTROY;

/**
 * The Default {@link PushRule} that creates channel based on the {@link org.atmosphere.cpr.AtmosphereRequest#getPathInfo()}.
 */
public class DefaultPushRule implements PushRule {

    /**
     * Creates channel (named {@link Broadcaster} in Atmosphere) based on the {@link org.atmosphere.cpr.AtmosphereRequest#getPathInfo()}
     * @param resource  An {@link AtmosphereResource}
     * @return
     */
    public boolean apply(AtmosphereResource resource) {
        String pathInfo = resource.getRequest().getPathInfo();
        if (pathInfo == null) {
            resource.setBroadcaster(BroadcasterFactory.getDefault().lookup("/*"));
            return true;
        }

        final Broadcaster b = BroadcasterFactory.getDefault().lookup(pathInfo, true);
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
        resource.setBroadcaster(b);

        return true;
    }
}
