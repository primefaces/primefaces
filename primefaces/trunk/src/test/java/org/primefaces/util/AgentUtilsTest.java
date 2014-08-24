/*
 * Copyright 2009-2014 PrimeTek.
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

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

public class AgentUtilsTest {

    @Test
    public void shouldCheckForIE() throws IOException {
    	String ua = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)";
        
        assertTrue(AgentUtils.isIE(ua));
        
        ua = "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";
        
        assertFalse(AgentUtils.isIE(ua));
    }
    
    @Test
    public void shouldCheckForIEVersion() throws IOException {
    	String ua = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)";     
        assertTrue(AgentUtils.isIE(ua, 10));
        assertFalse(AgentUtils.isIE(ua, 8));
   
        ua = "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";
        assertFalse(AgentUtils.isIE(ua, 9));
        
        ua = "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; GTB7.4; InfoPath.2; SV1; .NET CLR 3.3.69573; WOW64; en-US)";
        assertTrue(AgentUtils.isIE(ua, 8));
        
    }
}
