package org.primefaces.push;

import org.atmosphere.cpr.AtmosphereResource;

import java.util.List;

/**
 * A PushRule configure the <a href="http://github.com/Atmosphere/atmosphere">Atmosphere Framework</a> behavior. By default, the {@link DefaultPushRule} is used to creates
 * channel of communication based on the value of the request's pathInfo.
 * <br/>
 * PushRule can be implemented to customize the behavior of Atmosphere and to create channel (named {@link org.atmosphere.cpr.Broadcaster})
 * on the fly. For example, the following PushRule will create a single Channel of communication. All invokation of
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
 */
public interface PushRule {
    /**
     * Configure an {@link AtmosphereResource} and creates channel of communication on the fly.
     * @param resource an {@link AtmosphereResource}
     * @return true if other rule can be invoked, false if this rule terminate the process.
     */
    boolean apply(AtmosphereResource resource);

}

