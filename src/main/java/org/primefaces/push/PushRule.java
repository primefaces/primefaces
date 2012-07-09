package org.primefaces.push;

import org.atmosphere.cpr.AtmosphereResource;

import java.util.List;

public interface PushRule {

    boolean apply(AtmosphereResource resource);

}

