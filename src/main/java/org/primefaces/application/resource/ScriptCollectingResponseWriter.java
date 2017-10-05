/**
 * Copyright 2009-2017 PrimeTek.
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
package org.primefaces.application.resource;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScriptCollectingResponseWriter extends ResponseWriterWrapper {

    private ResponseWriter wrapped;

    private List<String> collectedSrc;
    private StringBuilder collectedContent;
    private int collectedContentCount; // just for statictics for now

    private boolean inScript;
    private StringBuilder src;
    private StringBuilder content;

    public ScriptCollectingResponseWriter() {
        
        inScript = false;
        src = new StringBuilder(50);
        content = new StringBuilder(75);

        collectedSrc = new ArrayList<String>(20);
        collectedContent = new StringBuilder(750);
        collectedContentCount = 0;
    }

    public void setWrapped(ResponseWriter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ResponseWriter getWrapped() {
        return wrapped;
    }

    @Override
    public void write(String str) throws IOException {
        if (inScript) {
            content.append(str);
        }
        else {
            getWrapped().write(str);
        }
    }

    @Override
    public void writeText(Object text, String property) throws IOException {
        if (inScript) {
            content.append(text);
        }
        else {
            getWrapped().writeText(text, property);
        }
    }

    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {
        if (inScript) {
            if ("src".equals(name)) {
                String strValue = (String) value;
                if (strValue != null && !strValue.trim().isEmpty()) {
                    src.append(strValue);
                }
            }
        }
        else {
            getWrapped().writeAttribute(name, value, property);
        }
    }

    @Override
    public void writeURIAttribute(String name, Object value, String property) throws IOException {
        if (inScript) {
            if ("src".equals(name)) {
                String strValue = (String) value;
                if (strValue != null && !strValue.trim().isEmpty()) {
                    src.append(strValue);
                }
            }
        }
        else {
            getWrapped().writeURIAttribute(name, value, property);
        }
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        if ("script".equals(name)) {
            inScript = true;
        }
        else {
            getWrapped().startElement(name, component);
        }
    }

    @Override
    public void endElement(String name) throws IOException {
        if ("script".equals(name)) {
            inScript = false;

            if (content.length() > 0) {
                collectedContent.append(content);
            }
            if (src.length() > 0) {
                collectedSrc.add(src.toString());
            }
            collectedContentCount++;

            src.setLength(0);
            content.setLength(0);
        }
        else if ("body".equals(name)) {
            collectedContentCount -= collectedSrc.size();

            for (int i = 0; i < collectedSrc.size(); i++) {
                String src = collectedSrc.get(i);
                if (src != null && !src.isEmpty()) {
                    getWrapped().startElement("script", null);
                    getWrapped().writeAttribute("type", "text/javascript", null);
                    getWrapped().writeAttribute("src", src, null);
                    getWrapped().endElement("script");
                }
            }

            getWrapped().startElement("script", null);
            getWrapped().writeAttribute("type", "text/javascript", null);
            getWrapped().write(minimizeScript(collectedContent.toString()));
            getWrapped().endElement("script");

            getWrapped().endElement(name);
        }
        else {
            getWrapped().endElement(name);
        }
    }

    protected String minimizeScript(String content) {
        content = content.replace("PrimeFaces.settings", "pf.settings")
            .replace("PrimeFaces.cw", "pf.cw")
            .replace("PrimeFaces.ab", "pf.ab")
            .replace("window.PrimeFaces", "pf")
            .replace(";;", ";");
        content = "var pf = window.PrimeFaces;" + content;

        return content;
    }
}
