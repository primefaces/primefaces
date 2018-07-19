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
package org.primefaces.application.resource.csp.scripts;

import org.primefaces.util.Base64;
import org.primefaces.util.ComponentUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

/**
 * Wrapper for {@link ResponseWriterWrapper} that can be used to automatically detect script sources during JSF render phase.
 * Nonces and hashes will be created for inline and external javascript. Javascript event handlers and URI handlers will be moved to an allowed script block.
 * May be enabled by including <code>script-src</code> in {@link Constants.ContextParams#CONTENT_SECURITY_POLICY_SUPPORTED_DIRECTIVES}.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/script-src">CSP: script-src</a>
 */
// TODO support partial-response
// TODO generate hashes
public class ContentSecurityPolicyScriptsResponseWriter extends ResponseWriterWrapper {

    /**
     * @see <a href="https://www.w3schools.com/jsref/dom_obj_event.asp">List of all HTML DOM Events</a>
     */
    static final Set<String> DOM_EVENTS = new HashSet<>(
            Arrays.asList("abort", "afterprint", "animationend", "animationiteration", "animationstart", "beforeprint", "beforeunload", "blur", "canplay",
                    "canplaythrough", "change", "click", "contextmenu", "copy", "cut", "dblclick", "drag", "dragend", "dragenter", "dragleave", "dragover",
                    "dragstart", "drop", "durationchange", "ended", "error", "focus", "focusin", "focusout", "fullscreenchange", "fullscreenerror",
                    "hashchange", "input", "invalid", "keydown", "keypress", "keyup", "load", "loadeddata", "loadedmetadata", "loadstart", "message",
                    "mousedown", "mouseenter", "mouseleave", "mousemove", "mouseover", "mouseout", "mouseup", "mousewheel", "offline", "online", "open",
                    "pagehide", "pageshow", "paste", "pause", "play", "playing", "popstate", "progress", "ratechange", "resize", "reset", "scroll", "search",
                    "seeked", "seeking", "select", "show", "stalled", "storage", "submit", "suspend", "timeupdate", "toggle", "touchcancel", "touchend",
                    "touchmove", "touchstart", "transitionend", "unload", "volumechange", "waiting", "wheel"));

    /**
     * @see <a href="https://www.w3.org/TR/html4/index/attributes.html">Index of Attributes</a>
     * @see <a href="https://stackoverflow.com/questions/2725156/complete-list-of-html-tag-attributes-which-have-a-url-value">URI attributes</a>
     */
    static final Set<String> URI_ATTRIBUTES = new HashSet<>(
            Arrays.asList("action", "background", "cite", "classid", "codebase", "data", "href", "longdesc", "profile", "src", "usemap", "formaction", "icon",
                    "manifest", "poster"));
    
    private static final String SCRIPT_TAG = "script", BODY_TAG = "body";
    private static final String ID_ATTRIBUTE = "id", NONCE_ATTRIBUTE = "nonce";
    private static final String EVENT_HANDLER_ATTRIBUTE_PREFIX = "on";
    private static final String JAVASCRIPT_SCHEME = "javascript:";

    private static final String EVENT_HANDLER_TEMPLATE = "pf.csp1(\"%s\",\"%s\",function(){%s});\n";
    private static final String URI_HANDLER_TEMPLATE = "pf.csp2(\"%s\",\"%s\",\"%s\");\n";
    
    final Stack<ElementState> elements;

    final Set<ElementState> elementsToHandle;
    final Set<String> nonces;
    
    public ContentSecurityPolicyScriptsResponseWriter(ResponseWriter wrapped) {
        super(wrapped);
        elements = new Stack<>();
        elementsToHandle = new LinkedHashSet<>();
        nonces = new HashSet<>();
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        writeAttributesIfNeeded();
        elements.push(new ElementState(name.toLowerCase()));
        getWrapped().startElement(name, component);
    }

    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {
        elements.peek().hasNonce = elements.peek().hasNonce || NONCE_ATTRIBUTE.equalsIgnoreCase(name);
        if (ID_ATTRIBUTE.equalsIgnoreCase(name) && value != null) {
            elements.peek().id = (String) value;
        }
        boolean inEventHandlerAttribute = name.toLowerCase().startsWith(EVENT_HANDLER_ATTRIBUTE_PREFIX) && DOM_EVENTS.contains(
                name.toLowerCase().substring(EVENT_HANDLER_ATTRIBUTE_PREFIX.length()));
        boolean inJavascriptUriAttribute = URI_ATTRIBUTES.contains(name.toLowerCase()) && isJavascriptUri(value);
        if (!inEventHandlerAttribute && !inJavascriptUriAttribute) {
            getWrapped().writeAttribute(name, value, property);
            return;
        }
        if (inEventHandlerAttribute && value != null) {
            elements.peek().javascriptEventHandlers.put(name.toLowerCase().substring(EVENT_HANDLER_ATTRIBUTE_PREFIX.length()), (String) value);
        }
        if (inJavascriptUriAttribute && value != null) {
            elements.peek().javascriptUriHandlers.put(name, getJavascriptFromUri(value));
        }
    }

    @Override
    public void writeURIAttribute(String name, Object value, String property) throws IOException {
        boolean inJavascriptUriAttribute = isJavascriptUri(value);
        if (!inJavascriptUriAttribute) {
            getWrapped().writeURIAttribute(name, value, property);
            return;
        }
        elements.peek().javascriptUriHandlers.put(name, getJavascriptFromUri(value));
    }

    @Override
    public void endElement(String name) throws IOException {
        writeAttributesIfNeeded();
        ElementState element = elements.pop();
        if (!element.javascriptEventHandlers.isEmpty() || !element.javascriptUriHandlers.isEmpty()) {
            elementsToHandle.add(element);
        }
        if (BODY_TAG.equalsIgnoreCase(name)) {
            //TODO support partial-response
            writeJavascriptHandlers();
            registerNoncesAndHashes();
        }
        getWrapped().endElement(name);
    }

    @Override
    public void flush() throws IOException {
        writeAttributesIfNeeded();
        getWrapped().flush();
    }

    @Override
    public void endDocument() throws IOException {
        writeAttributesIfNeeded();
        getWrapped().endDocument();
    }

    @Override
    public void writeComment(Object comment) throws IOException {
        writeAttributesIfNeeded();
        getWrapped().writeComment(comment);
    }

    @Override
    public void writeText(Object text, String property) throws IOException {
        writeAttributesIfNeeded();
        getWrapped().writeText(text, property);
    }

    @Override
    public void writeText(Object text, UIComponent component, String property) throws IOException {
        writeAttributesIfNeeded();
        getWrapped().writeText(text, component, property);
    }

    @Override
    public void writeText(char[] text, int off, int len) throws IOException {
        writeAttributesIfNeeded();
        getWrapped().writeText(text, off, len);
    }

    @Override
    public void close() throws IOException {
        writeAttributesIfNeeded();
        getWrapped().close();
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        writeAttributesIfNeeded();
        getWrapped().write(cbuf, off, len);
    }

    /**
     * Write needed attributes before the starting element will be closed by adding a trailing '>' character when calling e.g. {@link ResponseWriter#writeText}.
     * See {@link ResponseWriter#startElement(String, UIComponent)}
     */
    private void writeAttributesIfNeeded() throws IOException {
        if (elements.isEmpty()) {
            return;
        }
        ElementState element = elements.peek();
        if (!element.attributesWritten) {
            if (element.id == null && (!element.javascriptEventHandlers.isEmpty() || !element.javascriptUriHandlers.isEmpty())) {
                element.id = element.tag.toLowerCase() + "-" + UUID.randomUUID().toString();
                getWrapped().writeAttribute(ID_ATTRIBUTE, element.id, null);
            }
            if (SCRIPT_TAG.equalsIgnoreCase(element.tag) && !element.hasNonce) {
                element.hasNonce = true;
                getWrapped().writeAttribute(NONCE_ATTRIBUTE, generateNonce(), null);
            }
            element.attributesWritten = true;
        }
    }

    /**
     * Example: <code>&lt;form action="javascript:alert(1);" /&gt;</code>
     * @param attributeValue e.g. <code>javascript:alert(1);</code>
     */
    private static boolean isJavascriptUri(Object attributeValue) {
        if (attributeValue == null) {
            return false;
        }
        return ((String) attributeValue).trim().toLowerCase().startsWith(JAVASCRIPT_SCHEME);
    }

    /**
     * Return just the javascript code without the <code>javascript:</code> scheme handler
     * @param javascriptUriAttributeValue e.g. <code>javascript:alert(1);</code>
     * @return e.g. <code>alert(1);</code>
     */
    private static String getJavascriptFromUri(Object javascriptUriAttributeValue) {
        if (javascriptUriAttributeValue == null) {
            return "";
        }
        return ((String) javascriptUriAttributeValue).trim().substring(JAVASCRIPT_SCHEME.length());
    }

    /**
     * @return cryptographically secure pseudo-random nonce (aka number used once), Base64-encoded
     */
    private String generateNonce() {
        String nonce = Base64.encodeToString(UUID.randomUUID().toString().getBytes(), false);
        nonces.add(nonce);
        return nonce;
    }

    /**
     * Write javascript collected from event/URI handlers to a separate <code>script</code> block.
     */
    private void writeJavascriptHandlers() throws IOException {
        if (!elementsToHandle.isEmpty()) {
            getWrapped().startElement(SCRIPT_TAG, null);
            getWrapped().writeAttribute(NONCE_ATTRIBUTE, generateNonce(), null);
            StringBuilder javascriptBuilder = new StringBuilder("var pf=PrimeFaces;\n");
            javascriptBuilder.append("pf.csp1=function(id,evt,js){");
            javascriptBuilder.append("document.getElementById(id).addEventListener(evt,js);}\n");
            javascriptBuilder.append("pf.csp2=function(id,attr,jsUri){");
            javascriptBuilder.append("document.getElementById(id).setAttribute(attr,jsUri);}\n");
            for (ElementState element : elementsToHandle) {
                for (Map.Entry<String, String> eventHandler : element.javascriptEventHandlers.entrySet()) {
                    String event = eventHandler.getKey();
                    String javascript = eventHandler.getValue();
                    javascriptBuilder.append(
                            String.format(EVENT_HANDLER_TEMPLATE, ComponentUtils.escapeText(element.id), ComponentUtils.escapeText(event), javascript));
                }
                for (Map.Entry<String, String> uriHandler : element.javascriptUriHandlers.entrySet()) {
                    String attribute = uriHandler.getKey();
                    String javascript = uriHandler.getValue();
                    javascriptBuilder.append(
                            String.format(URI_HANDLER_TEMPLATE, ComponentUtils.escapeText(element.id), ComponentUtils.escapeText(attribute), javascript));
                }
            }
            getWrapped().writeText(javascriptBuilder.toString(), null);
            getWrapped().endElement(SCRIPT_TAG);
        }
    }

    /**
     * Make generated nonces and hashes available via request attribute that may be retrieved to set the Content-Security-Policy header later.
     */
    private void registerNoncesAndHashes() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        //TODO support hashes
        Set<String> sha256Hashes = new HashSet<>();
        request.setAttribute(ContentSecurityPolicyScripts.class.getName(), new ContentSecurityPolicyScripts(nonces, sha256Hashes));
    }

    @Override
    public ResponseWriter cloneWithWriter(Writer writer) {
        return getWrapped().cloneWithWriter(writer);
    }

    /**
     * State holder for the currently written element
     */
    static class ElementState implements Serializable {

        final String tag;
        String id;
        Map<String, String> javascriptEventHandlers;
        Map<String, String> javascriptUriHandlers;
        boolean hasNonce;
        boolean attributesWritten;

        ElementState(String tag) {
            this.tag = tag;
            javascriptEventHandlers = new LinkedHashMap<>(1);
            javascriptUriHandlers = new LinkedHashMap<>(1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ElementState that = (ElementState) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

    }
    
}
