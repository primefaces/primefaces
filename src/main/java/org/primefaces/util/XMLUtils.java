/*
 * Copyright 2009-2015 PrimeTek.
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

import org.apache.xmlbeans.impl.common.XMLChar;

public class XMLUtils {
    
    /**
     * Ensure a valid XMLElement name is returned.<br>
     * Uses the {@link org.apache.xmlbeans.impl.common.XMLChar}<br>
     * Replaces spaces by underscores,  &lt; by .lt, &gt; by .gt. and
     * all other characters by '.X.', 
     * where is the output of {@link java.lang.Integer}.toHexString()
     * 
     * @param intag the source for the element name
     * 
     * @return valid XML element name
     */
    public static String escapeTag(String intag) {
        if ( XMLChar.isValidName(intag) ) {
            return intag;
        }
        
        StringBuilder sb = new StringBuilder(intag);
        char c;
        for( int i=sb.length()-1; i>=0; i-- ) {
            c = intag.charAt(i);
            if ( !XMLChar.isName(c) ) {
                switch( c ) {
                    case ' ':
                        sb.setCharAt(i, '_');
                        break;
                    case '<':
                        sb.setCharAt(i, '.');
                        sb.insert( i+1, "lt." );
                        break;
                    case '>':
                        sb.setCharAt(i, '.');
                        sb.insert( i+1, "gt." );
                        break;
                    default:
                        sb.setCharAt(i, '.');
                        sb.insert( i+1, '.');
                        sb.insert( i+1, Integer.toHexString(c));
                        break;
                }
            }
        }
        // Make sure the first character is an allowed one
        if ( !XMLChar.isNameStart( sb.charAt(0)) ) {
            sb.insert(0, '_' ); 
        }
        
        return sb.toString();
    }
}
