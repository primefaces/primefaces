/**
 * Copyright 2009-2018 PrimeTek.
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

    private static final String SB_ESCAPE = XMLUtils.class.getName() + "#escape";

    /**
     * Ensure a valid XMLElement name is returned.<br>
     * Uses the {@link org.apache.xmlbeans.impl.common.XMLChar}<br>
     * Replaces spaces by underscores, &lt; by .lt, &gt; by .gt. and all other characters by '.X.', where is the output of
     * {@link java.lang.Integer}.toHexString()
     *
     * @param intag the source for the element name
     * @return valid XML element name
     */
    public static String escapeTag(String intag) {
        if (XMLChar.isValidName(intag) || intag == null || intag.length() == 0) {
            return intag;
        }

        StringBuilder sb = SharedStringBuilder.get(SB_ESCAPE, intag.length());
        sb.append(intag);

        char c;
        for (int i = sb.length() - 1; i >= 0; i--) {
            c = intag.charAt(i);
            if (!XMLChar.isName(c)) {
                switch (c) {
                    case ' ':
                        sb.setCharAt(i, '_');
                        break;
                    case '<':
                        sb.setCharAt(i, '.');
                        sb.insert(i + 1, "lt.");
                        break;
                    case '>':
                        sb.setCharAt(i, '.');
                        sb.insert(i + 1, "gt.");
                        break;
                    default:
                        sb.setCharAt(i, '.');
                        sb.insert(i + 1, '.');
                        sb.insert(i + 1, Integer.toHexString(c));
                        break;
                }
            }
        }
        // Make sure the first character is an allowed one
        if (!XMLChar.isNameStart(sb.charAt(0))) {
            sb.insert(0, '_');
        }

        return sb.toString();
    }

    /**
     * Escapes <, >, ", &, and ' in XML strings.
     *
     * @param value the value to escape
     * @return the escaped XML string
     */
    public static String escapeXml(String value) {
        StringBuilder sb = SharedStringBuilder.get(SB_ESCAPE, value.length());

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '\"':
                    sb.append("&quot;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    if (c > 0x7e) {
                        sb.append("&#" + ((int) c) + ";");
                    }
                    else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}
