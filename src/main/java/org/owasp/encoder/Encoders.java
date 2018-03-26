// Copyright (c) 2012 Jeff Ichnowski
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
//
//     * Redistributions of source code must retain the above
//       copyright notice, this list of conditions and the following
//       disclaimer.
//
//     * Redistributions in binary form must reproduce the above
//       copyright notice, this list of conditions and the following
//       disclaimer in the documentation and/or other materials
//       provided with the distribution.
//
//     * Neither the name of the OWASP nor the names of its
//       contributors may be used to endorse or promote products
//       derived from this software without specific prior written
//       permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.
package org.owasp.encoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Encoders -- Public factory method for obtaining instances of Encoders.
 * Classes implementing the encoders are not directly exposed as part of the API
 * since encoding strategies are subject to change. In many cases encoders will
 * share the same implementation, but have different internal flags for how to
 * handle varied content. For example the XML_CONTENT and XML_ATTRIBUTE contexts
 * may currently share the same class with each instances having a different set
 * of flags. Future version may optimize them into different classes.
 *
 * <p>
 * All encoders returned by the factory are thread-safe.</p>
 *
 * @author Jeff Ichnowski
 */
public final class Encoders {

    /**
     * Name of {@linkplain Encode#forHtml(String) HTML general} context.
     */
    public static final String HTML = "html";
    /**
     * Name of {@linkplain Encode#forHtmlContent(String) HTML content} context.
     */
    public static final String HTML_CONTENT = "html-content";
    /**
     * Name of {@linkplain Encode#forHtmlAttribute(String) HTML attribute}
     * context.
     */
    public static final String HTML_ATTRIBUTE = "html-attribute";
    /**
     * Name of
     * {@linkplain Encode#forHtmlUnquotedAttribute(String) unquoted HTML attribute}
     * context.
     */
    public static final String HTML_UNQUOTED_ATTRIBUTE = "html-attribute-unquoted";
    /**
     * Name of {@linkplain Encode#forXml(String) XML general} context.
     */
    public static final String XML = "xml";
    /**
     * Name of {@linkplain Encode#forXmlContent(String) XML content} context.
     */
    public static final String XML_CONTENT = "xml-content";
    /**
     * Name of {@linkplain Encode#forXmlAttribute(String) XML attribute}
     * context.
     */
    public static final String XML_ATTRIBUTE = "xml-attribute";
    /**
     * Name of {@linkplain Encode#forXmlComment(String) XML comment} context.
     */
    public static final String XML_COMMENT = "xml-comment";
    /**
     * Name of {@linkplain Encode#forCDATA(String) CDATA} context.
     */
    public static final String CDATA = "cdata";
    /**
     * Name of {@linkplain Encode#forCssString(String) CSS string} context.
     */
    public static final String CSS_STRING = "css-string";
    /**
     * Name of {@linkplain Encode#forCssUrl(String) CSS URL} context.
     */
    public static final String CSS_URL = "css-url";
    /**
     * Name of {@linkplain Encode#forJava(String) Java String} context.
     */
    public static final String JAVA = "java";
    /**
     * Name of {@linkplain Encode#forJavaScript(String) JavaScript general}
     * context.
     */
    public static final String JAVASCRIPT = "javascript";
    /**
     * Name of
     * {@linkplain Encode#forJavaScriptAttribute(String) JavaScript attribute}
     * context.
     */
    public static final String JAVASCRIPT_ATTRIBUTE = "javascript-attribute";
    /**
     * Name of {@linkplain Encode#forJavaScriptBlock(String) JavaScript block}
     * context.
     */
    public static final String JAVASCRIPT_BLOCK = "javascript-block";
    /**
     * Name of {@linkplain Encode#forJavaScriptSource(String) JavaScript source}
     * context.
     */
    public static final String JAVASCRIPT_SOURCE = "javascript-source";
    /**
     * Name of {@linkplain Encode#forUri(String) URI} context.
     */
    public static final String URI = "uri";
    /**
     * Name of {@linkplain Encode#forUriComponent(String) URI component}
     * context.
     */
    public static final String URI_COMPONENT = "uri-component";
    /**
     * Map from encoder name to encoder singleton.
     */
    private static final Map<String, Encoder> ENCODERS_MAP
            = new HashMap<String, Encoder>(32);
    // XML and HTML use the same encoder implementations currently
    /**
     * Encoder for general XML/HTML contexts.
     */
    static final XMLEncoder XML_ENCODER
            = map(HTML, map(XML, new XMLEncoder(XMLEncoder.Mode.ALL)));
    /**
     * Encoder for XML/HTML content contexts.
     */
    static final XMLEncoder XML_CONTENT_ENCODER
            = map(HTML_CONTENT, map(XML_CONTENT, new XMLEncoder(XMLEncoder.Mode.CONTENT)));
    /**
     * Encoder for XML/HTML attribute contexts.
     */
    static final XMLEncoder XML_ATTRIBUTE_ENCODER
            = map(HTML_ATTRIBUTE, map(XML_ATTRIBUTE, new XMLEncoder(XMLEncoder.Mode.ATTRIBUTE)));
    /**
     * Encoder for XML comments.
     */
    static final XMLCommentEncoder XML_COMMENT_ENCODER
            = map(XML_COMMENT, new XMLCommentEncoder());
    /**
     * Encoder for CDATA contexts.
     */
    static final CDATAEncoder CDATA_ENCODER
            = map(CDATA, new CDATAEncoder());
    /**
     * Encoder for unquoted HTML attributes.
     */
    static final HTMLEncoder HTML_UNQUOTED_ATTRIBUTE_ENCODER
            = map(HTML_UNQUOTED_ATTRIBUTE, new HTMLEncoder());
    /**
     * Encoder for general JavaScript contexts.
     */
    static final JavaScriptEncoder JAVASCRIPT_ENCODER
            = map(JAVASCRIPT, new JavaScriptEncoder(JavaScriptEncoder.Mode.HTML, false));
    /**
     * Encoder for JavaScript appearing in XML/HTML attributes.
     */
    static final JavaScriptEncoder JAVASCRIPT_ATTRIBUTE_ENCODER
            = map(JAVASCRIPT_ATTRIBUTE, new JavaScriptEncoder(JavaScriptEncoder.Mode.ATTRIBUTE, false));
    /**
     * Encoder for JavaScript appearing in HTML script blocks.
     */
    static final JavaScriptEncoder JAVASCRIPT_BLOCK_ENCODER
            = map(JAVASCRIPT_BLOCK, new JavaScriptEncoder(JavaScriptEncoder.Mode.BLOCK, false));
    /**
     * Encoder for JavaScript in stand-alone contexts.
     */
    static final JavaScriptEncoder JAVASCRIPT_SOURCE_ENCODER
            = map(JAVASCRIPT_SOURCE, new JavaScriptEncoder(JavaScriptEncoder.Mode.SOURCE, false));
    /**
     * Encoder for full URIs.
     */
    static final URIEncoder URI_ENCODER
            = map(URI, new URIEncoder(URIEncoder.Mode.FULL_URI));
    /**
     * Encoder for components of URIs.
     */
    static final URIEncoder URI_COMPONENT_ENCODER
            = map(URI_COMPONENT, new URIEncoder(URIEncoder.Mode.COMPONENT));
    /**
     * Encoder for Java strings.
     */
    static final JavaEncoder JAVA_ENCODER
            = map(JAVA, new JavaEncoder());
    /**
     * Encoder for CSS strings.
     */
    static final CSSEncoder CSS_STRING_ENCODER
            = map(CSS_STRING, new CSSEncoder(CSSEncoder.Mode.STRING));
    /**
     * Encoder for CSS URL values.
     */
    static final CSSEncoder CSS_URL_ENCODER
            = map(CSS_URL, new CSSEncoder(CSSEncoder.Mode.URL));

    /**
     * Internal method to setup and map encoder singletons.
     *
     * @param name -- name of the encoder (one of the constants above)
     * @param encoder -- the encoder singleton instance
     * @param <T> the encoder type
     * @return the encoder argument.
     */
    private static <T extends Encoder> T map(String name, T encoder) {
        Encoder old = ENCODERS_MAP.put(name, encoder);
        assert old == null;
        return encoder;
    }

    /**
     * Returns a new instance of an Encoder for the specified context. The
     * returned instance is thread-safe.
     *
     * @param contextName the context name (one of the String constants defined
     * in this class)
     * @return an encoder for the specified context.
     * @throws NullPointerException if {@code contextName} is null
     * @throws UnsupportedContextException if {@code contextName} is not
     * recognized.
     */
    public static Encoder forName(String contextName) throws NullPointerException, UnsupportedContextException {
        if (contextName == null) {
            throw new NullPointerException();
        }
        Encoder encoder = ENCODERS_MAP.get(contextName);
        if (encoder == null) {
            throw new UnsupportedContextException(contextName);
        }
        return encoder;
    }

    /**
     * No instances.
     */
    private Encoders() {
    }
}
