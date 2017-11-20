/**
 * Copyright 2009-2017 PrimeTek.
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

/**
 * A PushRule configure the <a href="http://github.com/Atmosphere/atmosphere">Atmosphere Framework</a> behavior.
 * By default, the {@link DefaultPushRule} is used to creates channel of communication based on the value of the request's pathInfo.
 * <br/>
 * PushRule can be implemented to customize the behavior of Atmosphere and to create channel (named {@link org.atmosphere.cpr.Broadcaster})
 * on the fly. For example, the following PushRule will create a single Channel of communication. All invocation of
 * {@link PushContext#push(String, Object)} will be shared to all connected browser
 * <blockquote>
 *   boolean apply(AtmosphereResource resource) {
 *       Broadcaster all = BroadcasterFactory.getDefault().lookup("/*", true);
 *       resource.setBroadcaster(all);
 *       return true;
 *   }
 * </blockquote>
 *
 * For more information on how to manipulate the Atmosphere's object, see
 *
 *    <a href="">https://github.com/Atmosphere/atmosphere/wiki/Understanding-Broadcaster</a>Broadcaster</a>
 *    <a href="https://github.com/Atmosphere/atmosphere/wiki/Understanding-AtmosphereResource">AtmosphereResource</a>
 *
 * @deprecated With PrimeFaces 4.1 and up, it is recommended to use {@link PushEndpoint}
 */
public interface PushRule {

    /**
     * Configure an {@link AtmosphereResource} and creates channel of communication on the fly.
     *
     * @param resource an {@link AtmosphereResource}
     * @return true if other rule can be invoked, false if this rule terminate the process.
     */
    boolean apply(AtmosphereResource resource);

}
