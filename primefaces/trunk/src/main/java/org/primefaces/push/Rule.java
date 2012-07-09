package org.primefaces.push;

import org.atmosphere.cpr.AtmosphereResource;

import java.util.List;

public interface Rule {

    boolean apply(AtmosphereResource resource);

}

