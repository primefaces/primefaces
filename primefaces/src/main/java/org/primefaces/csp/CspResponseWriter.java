/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.csp;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.Lazy;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.context.ResponseWriterWrapper;

public class CspResponseWriter extends ResponseWriterWrapper {

    /**
     * @see <a href="https://www.w3schools.com/jsref/dom_obj_event.asp">List of all HTML DOM Events</a>
     */
    static final Set<String> DOM_EVENTS = new HashSet<>(
            Arrays.asList("onabort", "onafterprint", "onanimationend", "onanimationiteration", "onanimationstart", "onbeforeprint",
                    "onbeforeunload", "onblur", "oncanplay", "oncanplaythrough", "onchange", "onclick", "oncontextmenu", "oncopy", "oncut",
                    "ondblclick", "ondrag", "ondragend", "ondragenter", "ondragleave", "ondragover", "ondragstart", "ondrop", "ondurationchange",
                    "onended", "onerror", "onfocus", "onfocusin", "onfocusout", "onfullscreenchange", "onfullscreenerror", "onhashchange",
                    "oninput", "oninvalid", "onkeydown", "onkeypress", "onkeyup", "onload", "onloadeddata", "onloadedmetadata", "onloadstart",
                    "onmessage", "onmousedown", "onmouseenter", "onmouseleave", "onmousemove", "onmouseover", "onmouseout", "onmouseup",
                    "onmousewheel", "onoffline", "ononline", "onopen", "onpagehide", "onpageshow", "onpaste", "onpause", "onplay", "onplaying",
                    "onpopstate", "onprogress", "onratechange", "onresize", "onreset", "onscroll", "onsearch", "onseeked", "onseeking",
                    "onselect", "onshow", "onstalled", "onstorage", "onsubmit", "onsuspend", "ontimeupdate", "ontoggle", "ontouchcancel",
                    "ontouchend", "ontouchmove", "ontouchstart", "ontransitionend", "onunload", "onvolumechange", "onwaiting", "onwheel"));

    private CspState cspState;

    private String lastElement;
    private String lastId;
    private String lastNonce;
    private Map<String, String> lastEvents;

    private Lazy<Boolean> policyProvided;

    public CspResponseWriter(ResponseWriter wrapped, CspState cspState) {
        super(wrapped);
        this.cspState = cspState;

        policyProvided = new Lazy<>(() ->
                PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getConfig().isPolicyProvided());
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        listenOnEndAttribute();

        lastElement = name;

        getWrapped().startElement(name, component);
    }

    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {

        if ("nonce".equalsIgnoreCase(name) && value != null) {
            lastNonce = (String) value;
        }
        else if ("id".equalsIgnoreCase(name) && value != null) {
            lastId = (String) value;
        }

        String lowerCaseName = name.toLowerCase();
        if (lowerCaseName.startsWith("on") && DOM_EVENTS.contains(lowerCaseName)) {
            if (value != null) {
                if (lastEvents == null) {
                    lastEvents = new HashMap<>(1);
                }
                lastEvents.put(name, (String) value);
            }
            return;
        }

        getWrapped().writeAttribute(name, value, property);
    }

    @Override
    public void endElement(String name) throws IOException {
        listenOnEndAttribute();

        if ("body".equalsIgnoreCase(name)) {
            writeJavascriptHandlers();
        }

        getWrapped().endElement(name);
    }

    @Override
    public void flush() throws IOException {
        listenOnEndAttribute();

        getWrapped().flush();
    }

    @Override
    public void endDocument() throws IOException {
        listenOnEndAttribute();

        getWrapped().endDocument();
    }

    @Override
    public void writeComment(Object comment) throws IOException {
        listenOnEndAttribute();

        getWrapped().writeComment(comment);
    }

    @Override
    public void writeText(Object text, String property) throws IOException {
        listenOnEndAttribute();

        getWrapped().writeText(text, property);
    }

    @Override
    public void writeText(Object text, UIComponent component, String property) throws IOException {
        listenOnEndAttribute();

        getWrapped().writeText(text, component, property);
    }

    @Override
    public void writeText(char[] text, int off, int len) throws IOException {
        listenOnEndAttribute();

        getWrapped().writeText(text, off, len);
    }

    @Override
    public void close() throws IOException {
        listenOnEndAttribute();

        getWrapped().close();
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        listenOnEndAttribute();

        getWrapped().write(cbuf, off, len);
    }

    /**
     * Write needed attributes before the starting element will be closed by adding a trailing '>' character when calling e.g.
     * {@link ResponseWriter#writeText}.
     * See {@link ResponseWriter#startElement(String, UIComponent)}
     */
    private void listenOnEndAttribute() throws IOException {

        if (lastElement == null) {
            return;
        }

        // no nonce written -> do it if the CSP policy is not provided
        if (Boolean.FALSE.equals(policyProvided.get())) {
            if ("script".equalsIgnoreCase(lastElement) && LangUtils.isBlank(lastNonce)) {
                getWrapped().writeAttribute("nonce", cspState.getNonce(), null);
            }
        }

        if (lastEvents != null && !lastEvents.isEmpty()) {
            String id = lastId;

            // no id written -> generate a new one and write it
            // otherwise we can't identify the element for our scripts
            if (LangUtils.isBlank(id)) {
                id = lastElement.toLowerCase() + "-" + UUID.randomUUID();
                getWrapped().writeAttribute("id", id, null);
            }

            // add current collected events to our state
            cspState.getEventHandlers().put(id, lastEvents);
        }

        reset();
    }

    public void reset() {
        lastElement = null;
        lastId = null;
        lastNonce = null;
        lastEvents = null;
    }

    /**
     * Write javascript collected from event/URI handlers to a separate <code>script</code> block.
     */
    void writeJavascriptHandlers() throws IOException {
        reset();

        if (cspState.getEventHandlers() == null || cspState.getEventHandlers().isEmpty()) {
            return;
        }

        startElement("script", null);
        StringBuilder javascriptBuilder = new StringBuilder(cspState.getEventHandlers().size() * 25);

        for (Map.Entry<String, Map<String, String>> elements : cspState.getEventHandlers().entrySet()) {
            String id = elements.getKey();

            for (Map.Entry<String, String> events : elements.getValue().entrySet()) {
                String event = events.getKey();
                String javascript = events.getValue();

                javascriptBuilder.append("PrimeFaces.csp.register('");
                javascriptBuilder.append(EscapeUtils.forJavaScript(id));
                javascriptBuilder.append("','");
                javascriptBuilder.append(event);
                javascriptBuilder.append("',function(event){");
                javascriptBuilder.append(javascript);
                javascriptBuilder.append("});");
            }
        }

        String javascript = javascriptBuilder.toString();
        writeText(javascript, null);
        endElement("script");

        cspState.getEventHandlers().clear();
    }

    @Override
    public ResponseWriter cloneWithWriter(Writer writer) {
        return new CspResponseWriter(getWrapped().cloneWithWriter(writer), this.cspState);
    }

    /**
     * Special scenario where for indexed id's we need to replace the old id with new one.
     *
     * @param oldId the old id
     * @param newId the new id
     */
    public void updateId(String oldId, String newId) {
        Map<String, String> events = cspState.getEventHandlers().remove(oldId);
        if (events != null && !events.isEmpty()) {
            for (Map.Entry<String, String> entry : events.entrySet()) {
                String oldValue = entry.getValue();
                // replace 'id=' and 'source:' values
                String newValue = oldValue.replaceAll("\\sid=\"" + oldId + "\"", " id=\"" + newId + "\"");
                newValue = newValue.replaceAll("source:\"" + oldId + "\"", " source:\"" + newId + "\"");
                entry.setValue(newValue);
            }
            CspState cspState = this.cspState;
            cspState.getEventHandlers().put(newId, events);
        }
    }
}
