/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.util;

import org.primefaces.mock.CollectingResponseWriter;
import java.io.IOException;
import javax.faces.context.FacesContext;

import org.junit.Test;
import org.primefaces.mock.FacesContextMock;

import static org.junit.Assert.*;
import org.primefaces.mock.pf.PrimeConfigurationMock;

public class WidgetBuilderTest {

    protected WidgetBuilder getWidgetBuilder(CollectingResponseWriter writer) {
        FacesContext context = new FacesContextMock(writer);
        return new WidgetBuilder(context, new PrimeConfigurationMock(context));
    }

    @Test
    public void init() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();

        WidgetBuilder builder= getWidgetBuilder(writer);
        builder.init("AccordionPanel", "acco", "accoId");
        builder.finish();

        assertEquals(
                "<script id=\"accoId_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"AccordionPanel\",\"acco\",{id:\"accoId\"});});</script>",
                writer.toString());
    }

    @Test
    public void initWithMoveScriptsToBottom() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();

        FacesContext context = new FacesContextMock(writer);
        PrimeConfigurationMock config = new PrimeConfigurationMock(context);
        config.setMoveScriptsToBottom(true);
        WidgetBuilder builder = new WidgetBuilder(context, config);

        builder.init("AccordionPanel", "acco", "accoId");
        builder.finish();

        assertEquals(
                "<script id=\"accoId_s\" type=\"text/javascript\">PrimeFaces.cw(\"AccordionPanel\",\"acco\",{id:\"accoId\"});</script>",
                writer.toString());
    }

    @Test
    public void initWithWindowLoad() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();

        WidgetBuilder builder= getWidgetBuilder(writer);
        builder.initWithWindowLoad("AccordionPanel", "acco", "accoId");
        builder.finish();

        assertEquals(
                "<script id=\"accoId_s\" type=\"text/javascript\">$(window).on(\"load\",function(){PrimeFaces.cw(\"AccordionPanel\",\"acco\",{id:\"accoId\"});});</script>",
                writer.toString());
    }

    @Test
    public void initWithComponentLoad() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();

        WidgetBuilder builder= getWidgetBuilder(writer);
        builder.initWithComponentLoad("AccordionPanel", "acco", "accoId", "test");
        builder.finish();

        assertEquals(
                "<script id=\"accoId_s\" type=\"text/javascript\">$(PrimeFaces.escapeClientId(\"test\")).on(\"load\",function(){PrimeFaces.cw(\"AccordionPanel\",\"acco\",{id:\"accoId\"});});</script>",
                writer.toString());
    }

    @Test
    public void shouldBuildWithAttributes() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();

        WidgetBuilder builder = getWidgetBuilder(writer);
        builder.init("DataTable", "dt", "dt1");
        builder.attr("selectionMode", "single", null);
        builder.attr("lazy", true, false);
        builder.attr("paginator", false, false);
        builder.attr("rows", 10, 10);
        builder.finish();

        assertEquals(
                "<script id=\"dt1_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"DataTable\",\"dt\",{id:\"dt1\",selectionMode:\"single\",lazy:true});});</script>",
                writer.toString());
    }

    @Test
    public void shouldBuildWithCallbacks() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();

        WidgetBuilder builder = getWidgetBuilder(writer);
        builder.init("DataTable", "dt", "dt1");
        builder.attr("selectionMode", "single", null);
        builder.attr("lazy", true, false);
        builder.attr("paginator", false, false);
        builder.attr("rows", 10, 10);
        builder.callback("onRowSelect", "function(row)", "alert(row);");
        builder.finish();

        assertEquals("<script id=\"dt1_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"DataTable\",\"dt\",{id:\"dt1\",selectionMode:\"single\",lazy:true,onRowSelect:function(row){alert(row);}});});</script>", writer.toString());
    }
}
