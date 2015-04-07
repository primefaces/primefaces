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

import javax.faces.context.FacesContext;

public class AgentUtils {

	private AgentUtils() {}
	
	public static boolean isIE(FacesContext context) {
        String userAgent = context.getExternalContext().getRequestHeaderMap().get("User-Agent");
        
		if(userAgent == null)
			return false;
		else
			return userAgent.contains("MSIE");
	}
    
    public static boolean isIE(FacesContext context, int value) {
        String userAgent = context.getExternalContext().getRequestHeaderMap().get("User-Agent");
        
		if(userAgent == null) {
			return false;
        }
        else {
            int index = userAgent.indexOf("MSIE");
            
            if(index == -1) {
                return false;
            }
            else {
                int version = Double.valueOf(userAgent.substring((index + 5), userAgent.indexOf(";", index))).intValue();
                
                return version == value;
            }
        }
	}
    
    public static boolean isLessThanIE(FacesContext context, int value) {
        String userAgent = context.getExternalContext().getRequestHeaderMap().get("User-Agent");
        
		if(userAgent == null) {
			return false;
        }
        else {
            int index = userAgent.indexOf("MSIE");
            
            if(index == -1) {
                return false;
            }
            else {
                int version = Double.valueOf(userAgent.substring((index + 5), userAgent.indexOf(";", index))).intValue();
                
                return version > value;
            }
        }
	}
}