package org.primefaces.push;

import org.atmosphere.cpr.AsyncSupportListener;
import org.atmosphere.cpr.BroadcasterListener;

/**
 * Advanced {@link PushContextListener} to listen to all available Atmosphere callbacks.
 * The default {@link PushContextListener} should be enough for the most cases.
 */
public interface AdvancedPushContextListener extends PushContextListener, AsyncSupportListener, BroadcasterListener {

}
