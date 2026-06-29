/* 
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.junit.jupiter.api.Test;
import org.primefaces.component.panel.Panel;
import org.primefaces.config.PrimeEnvironment;
import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.mock.pf.PrimeConfigurationMock;

public class WidgetBuilderTest {

    protected WidgetBuilder getWidgetBuilder(FacesContext context) {
        return new WidgetBuilder(context, new PrimeConfigurationMock(context, new PrimeEnvironment(context)));
    }

    @Test
    public void init() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);

        Panel panel = mock(Panel.class);
        when(panel.resolveWidgetVar(context)).thenReturn("acco");
        when(panel.getClientId(context)).thenReturn("accoId");

        WidgetBuilder builder= getWidgetBuilder(context);
        builder.init("AccordionPanel", panel);
        builder.finish();

        assertEquals(
                "<script id=\"accoId_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"AccordionPanel\",\"acco\",{id:\"accoId\"});});</script>",
                writer.toString());
    }

    @Test
    public void initWithMoveScriptsToBottom() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();

        FacesContext context = new FacesContextMock(writer);
        PrimeConfigurationMock config = new PrimeConfigurationMock(context, new PrimeEnvironment(context));
        config.setMoveScriptsToBottom(true);
        WidgetBuilder builder = new WidgetBuilder(context, config);

        Panel panel = mock(Panel.class);
        when(panel.resolveWidgetVar(context)).thenReturn("acco");
        when(panel.getClientId(context)).thenReturn("accoId");
        builder.init("AccordionPanel", panel);
        builder.finish();

        assertEquals(
                "<script id=\"accoId_s\" type=\"text/javascript\">PrimeFaces.cw(\"AccordionPanel\",\"acco\",{id:\"accoId\"});</script>",
                writer.toString());
    }

    @Test
    public void initWithWindowLoad() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);

        WidgetBuilder builder = getWidgetBuilder(context);
        builder.initWithWindowLoad("AccordionPanel", "acco", "accoId");
        builder.finish();

        assertEquals(
                "<script id=\"accoId_s\" type=\"text/javascript\">$(window).on(\"load\",function(){PrimeFaces.cw(\"AccordionPanel\",\"acco\",{id:\"accoId\"});});</script>",
                writer.toString());
    }

    @Test
    public void initWithComponentLoad() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);

        WidgetBuilder builder= getWidgetBuilder(context);
        builder.initWithComponentLoad("AccordionPanel", "acco", "accoId", "test");
        builder.finish();

        assertEquals(
                "<script id=\"accoId_s\" type=\"text/javascript\">PrimeFaces.onElementLoad($(PrimeFaces.escapeClientId(\"test\")),function(){PrimeFaces.cw(\"AccordionPanel\",\"acco\",{id:\"accoId\"});});</script>",
                writer.toString());
    }

    @Test
    public void shouldBuildWithAttributes() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);

        Panel table = mock(Panel.class);
        when(table.resolveWidgetVar(context)).thenReturn("dt");
        when(table.getClientId(context)).thenReturn("dt1");

        WidgetBuilder builder = getWidgetBuilder(context);
        builder.init("DataTable", table);
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
        FacesContext context = new FacesContextMock(writer);

        Panel table = mock(Panel.class);
        when(table.resolveWidgetVar(context)).thenReturn("dt");
        when(table.getClientId(context)).thenReturn("dt1");

        WidgetBuilder builder = getWidgetBuilder(context);
        builder.init("DataTable", table);
        builder.attr("selectionMode", "single", null);
        builder.attr("lazy", true, false);
        builder.attr("paginator", false, false);
        builder.attr("rows", 10, 10);
        builder.callback("onRowSelect", "function(row)", "alert(row);");
        builder.finish();

        assertEquals("<script id=\"dt1_s\" type=\"text/javascript\">$(function(){PrimeFaces.cw(\"DataTable\",\"dt\",{id:\"dt1\",selectionMode:\"single\",lazy:true,onRowSelect:function(row){alert(row);}});});</script>", writer.toString());
    }

    @Test
    public void attributeDefaultValueIsNotEncoded() throws IOException {
        CollectingResponseWriter writer = new CollectingResponseWriter();

        FacesContext context = new FacesContextMock(writer);
        PrimeConfigurationMock config = new PrimeConfigurationMock(context, new PrimeEnvironment(context));
        config.setMoveScriptsToBottom(true);
        WidgetBuilder builder = new WidgetBuilder(context, config);

        Panel panel = mock(Panel.class);
        when(panel.resolveWidgetVar(context)).thenReturn("myComponent");
        when(panel.getClientId(context)).thenReturn("myComponent1");

        builder.init("MyComponent", panel);
        String defaultValue = "'My custom default value'";
        builder.attr("someAttribute", null, defaultValue);
        builder.finish();
        
        String output = writer.toString();
        assertFalse(output.contains(defaultValue));

        String expectedOutput = "<script id=\"myComponent1_s\" type=\"text/javascript\">PrimeFaces.cw(\"MyComponent\",\"myComponent\",{id:\"myComponent1\"});</script>";
        assertEquals(expectedOutput, output);
    }

    @Test
    public void attrJavascriptEscapeJavascript() throws IOException {
    	CollectingResponseWriter writer = new CollectingResponseWriter();

        FacesContext context = new FacesContextMock(writer);
        PrimeConfigurationMock config = new PrimeConfigurationMock(context, new PrimeEnvironment(context));
        config.setMoveScriptsToBottom(true);
        WidgetBuilder builder = new WidgetBuilder(context, config);

        Panel panel = mock(Panel.class);
        when(panel.resolveWidgetVar(context)).thenReturn("myComponent");
        when(panel.getClientId(context)).thenReturn("myComponent1");

        builder.init("MyComponent", panel);
        builder.attr("someAttribute", "<script>alert('Hello World!')</script>", null);
        builder.finish();
        
        String output = writer.toString();

        String expectedOutput = "<script id=\"myComponent1_s\" type=\"text/javascript\">PrimeFaces.cw(\"MyComponent\",\"myComponent\",{id:\"myComponent1\",someAttribute:\"<script>alert(\\x27Hello World!\\x27)<\\/script>\"});</script>";
        assertEquals(expectedOutput, output);
    }
}
