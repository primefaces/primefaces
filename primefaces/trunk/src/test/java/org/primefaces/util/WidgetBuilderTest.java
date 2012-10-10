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
package org.primefaces.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class WidgetBuilderTest {
    
    @Test
    public void shouldBuildBasicWidget() {
        WidgetBuilder builder = new WidgetBuilder("AccordionPanel", "acco", "accoId");
        String script = builder.build();
        
        assertEquals("PrimeFaces.cw('AccordionPanel','acco',{id:'accoId'});", script);
    }
    
    @Test
    public void shouldBuildWithAttributes() {
        WidgetBuilder builder = new WidgetBuilder("DataTable", "dt", "dt1");
        builder.attr("selectionMode", "single", null);
        builder.attr("lazy", true, false);
        builder.attr("paginator", false, false);
        builder.attr("rows", 10, 10);
        
        String script = builder.build();
        
        assertEquals("PrimeFaces.cw('DataTable','dt',{id:'dt1',selectionMode:'single',lazy:true});", script);
    }
    
    @Test
    public void shouldBuildWithCallbacks() {
        WidgetBuilder builder = new WidgetBuilder("DataTable", "dt", "dt1");
        builder.attr("selectionMode", "single", null);
        builder.attr("lazy", true, false);
        builder.attr("paginator", false, false);
        builder.attr("rows", 10, 10);
        builder.callback("onRowSelect", "function(row)", "alert(row);");
        
        String script = builder.build();
        
        assertEquals("PrimeFaces.cw('DataTable','dt',{id:'dt1',selectionMode:'single',lazy:true,onRowSelect:function(row){alert(row);}});", script);
    }
}
