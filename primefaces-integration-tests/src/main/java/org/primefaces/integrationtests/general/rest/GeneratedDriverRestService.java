/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.integrationtests.general.rest;

import org.primefaces.integrationtests.general.model.Driver;
import org.primefaces.integrationtests.general.service.GeneratedDriverService;
import org.primefaces.model.rest.AutoCompleteSuggestion;
import org.primefaces.model.rest.AutoCompleteSuggestionResponse;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("/generateddriver")
public class GeneratedDriverRestService {

    @Inject
    private GeneratedDriverService service;

    @GET
    @Path("/autocomplete")
    @Produces({ MediaType.APPLICATION_JSON })
    public AutoCompleteSuggestionResponse autocomplete(@QueryParam("query") String query) {
        String queryLowerCase = query.toLowerCase();
        List<Driver> allDrivers = service.getDrivers();
        return new AutoCompleteSuggestionResponse(allDrivers.stream()
                .filter(d -> d.getName().toLowerCase().contains(queryLowerCase))
                .map(d -> new AutoCompleteSuggestion(Integer.toString(d.getId()), d.getName()))
                .collect(Collectors.toList()));
    }
}
