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

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.CoderResult;

/**
 * Encode -- fluent interface for contextual encoding.  Example usage in a JSP:
 *
 * <pre>
 *     &lt;input value="&lt;%=Encode.forHtml(value)%&gt;" /&gt;
 * </pre>
 *
 * <p>There are two versions of each contextual encoding method.  The first
 * takes a {@code String} argument and returns the encoded version as a
 * {@code String}.  The second version writes the encoded version directly
 * to a {@code Writer}.</p>
 *
 * <p>Please make sure to read and understand the context that the method encodes
 * for.  Encoding for the incorrect context will likely lead to exposing a
 * cross-site scripting vulnerability.</p>
 *
 * @author Jeff Ichnowski
 */
public final class Encode {
    /** No instances. */
    private Encode() {}

    /**
     * <p>Encodes for (X)HTML text content and text attributes.  Since
     * this method encodes for both contexts, it may be slightly less
     * efficient to use this method over the methods targeted towards
     * the specific contexts ({@link #forHtmlAttribute(String)} and
     * {@link #forHtmlContent(String)}.  In general this method should
     * be preferred unless you are really concerned with saving a few
     * bytes or are writing a framework that utilizes this
     * package.</p>
     *
     * <b>Example JSP Usage</b>
     * <pre>
     *     &lt;div&gt;&lt;%=Encode.forHtml(unsafeData)%&gt;&lt;/div&gt;
     *
     *     &lt;input value="&lt;%=Encode.forHtml(unsafeData)%&gt;" /&gt;
     * </pre>
     *
     * <table border="0" class="memberSummary" summary="Shows the input and results of encoding">
     *   <caption><b>Encoding&nbsp;Table</b></caption>
     *   <thead>
     *     <tr>
     *       <th align="left" class="colFirst">Input</th>
     *       <th align="left" class="colLast">Result</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr class="altColor">
     *       <td class="colFirst">{@code &}</td>
     *       <td class="colLast">{@code &amp;}</td>
     *     </tr>
     *     <tr class="rowColor">
     *       <td class="colFirst">{@code <}</td>
     *       <td class="colLast">{@code &lt;}</td>
     *     </tr>
     *     <tr class="altColor">
     *       <td class="colFirst">{@code >}</td>
     *       <td class="colLast">{@code &gt;}</td>
     *     </tr>
     *     <tr class="rowColor">
     *       <td class="colFirst">{@code "}</td>
     *       <td class="colLast">{@code &#34;}</td>
     *     </tr>
     *     <tr class="altColor">
     *       <td class="colFirst">{@code '}</td>
     *       <td class="colLast">{@code &#39;}</td>
     *     </tr>
     *   </tbody>
     * </table>
     *
     * <p><b>Additional Notes</b></p>
     * <ul>
     * <li>The encoding of the greater-than sign ({@code >}) is not
     * strictly required, but is included for maximum
     * compatibility.</li>
     *
     * <li>Numeric encoding is used for double-quote character ({@code
     * "}) as it shorter than the also valid {@code &quot;}.</li>
     *
     * <li>Carriage return (U+0D), line-feed (U+0A), horizontal tab
     * (U+09) and space (U+20) are valid in quoted attributes and in
     * block in an unescaped form.</li>
     *
     * <li>Surrogate pairs are passed through only if valid.</li>
     *
     * <li>Characters that are not <a
     * href="http://www.w3.org/TR/REC-xml/#charsets">valid according
     * to the XML specification</a> are replaced by a space character
     * as they could lead to parsing errors.  In particular only {@code #x9
     * | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] |
     * [#x10000-#x10FFFF]} are considered valid.</li>
     * </ul>
     *
     * @param input the data to encode
     * @return the data encoded for html.
     */
    public static String forHtml(String input) {
        return forXml(input);
    }

    /**
     * See {@link #forHtml(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forHtml(Writer out, String input) throws IOException {
        forXml(out, input);
    }

    /**
     * <p>This method encodes for HTML text content.  It does not escape
     * quotation characters and is thus unsafe for use with
     * HTML attributes.  Use either forHtml or forHtmlAttribute for those
     * methods.</p>
     *
     * <b>Example JSP Usage</b>
     * <pre>
     *     &lt;div&gt;&lt;%=Encode.forHtmlContent(unsafeData)%&gt;&lt;/div&gt;
     * </pre>
     * <table border="0" class="memberSummary" summary="Shows the input and results of encoding">
     *   <caption><b>Encoding Table</b></caption>
     *   <thead>
     *     <tr>
     *       <th align="left" class="colFirst">Input</th>
     *       <th align="left" class="colLast">Result</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr class="altColor">
     *       <td class="colFirst">{@code &}</td>
     *       <td class="colLast">{@code &amp;}</td>
     *     </tr>
     *     <tr class="rowColor">
     *       <td class="colFirst">{@code <}</td>
     *       <td class="colLast">{@code &lt;}</td>
     *     </tr>
     *     <tr class="altColor">
     *       <td class="colFirst">{@code >}</td>
     *       <td class="colLast">{@code &gt;}</td>
     *     </tr>
     *   </tbody>
     * </table>
     *
     * <p><b>Additional Notes</b></p>
     * <ul>
     * <li>Single-quote character ({@code '}) and double-quote
     * character ({@code "}) do not require encoding in HTML
     * blocks, unlike other HTML contexts.</li>
     *
     * <li>The encoding of the greater-than sign ({@code >}) is not
     * strictly required, but is included for maximum
     * compatibility.</li>
     *
     * <li>Carriage return (U+0D), line-feed (U+0A), horizontal tab
     * (U+09) and space (U+20) are valid in quoted attributes and in
     * block in an unescaped form.</li>
     *
     * <li>Surrogate pairs are passed through only if valid.</li>
     *
     * <li>Characters that are not <a
     * href="http://www.w3.org/TR/REC-xml/#charsets">valid according
     * to the XML specification</a> are replaced by a space character
     * as they could lead to parsing errors.  In particular only {@code #x9
     * | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] |
     * [#x10000-#x10FFFF]} are considered valid.</li>
     * </ul>
     *
     * @param input the input to encode
     * @return the encoded result
     */
    public static String forHtmlContent(String input) {
        return forXmlContent(input);
    }

    /**
     * See {@link #forHtmlContent(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forHtmlContent(Writer out, String input)
        throws IOException
    {
        forXmlContent(out, input);
    }

    /**
     * <p>This method encodes for HTML text attributes.</p>
     *
     * <b>Example JSP Usage</b>
     * <pre>
     *     &lt;div&gt;&lt;%=Encode.forHtml(unsafeData)%&gt;&lt;/div&gt;
     * </pre>
     *
     * <table border="0" class="memberSummary" summary="Shows the input and results of encoding">
     *   <caption><b>Encoding Table</b></caption>
     *   <thead>
     *     <tr>
     *       <th align="left" class="colFirst">Input</th>
     *       <th align="left" class="colLast">Result</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr class="altColor">
     *       <td class="colFirst">{@code &}</td>
     *       <td class="colLast">{@code &amp;}</td>
     *     </tr>
     *     <tr class="rowColor">
     *       <td class="colFirst">{@code <}</td>
     *       <td class="colLast">{@code &lt;}</td>
     *     </tr>
     *     <tr class="altColor">
     *       <td class="colFirst">{@code "}</td>
     *       <td class="colLast">{@code &#34;}</td>
     *     </tr>
     *     <tr class="rowColor">
     *       <td class="colFirst">{@code '}</td>
     *       <td class="colLast">{@code &#39;}</td>
     *     </tr>
     *   </tbody>
     * </table>
     *
     * <p><b>Additional Notes</b></p>
     * <ul>
     * <li>Both the single-quote character ({@code '}) and the
     * double-quote character ({@code "}) are encoded so this is safe
     * for HTML attributes with either enclosing character.</li>
     *
     * <li>The encoding of the greater-than sign ({@code >}) is not
     * required for attributes.</li>
     *
     * <li>Numeric encoding is used for double-quote character ({@code
     * "}) as it shorter than the also valid {@code &quot;}.</li>
     *
     * <li>Carriage return (U+0D), line-feed (U+0A), horizontal tab
     * (U+09) and space (U+20) are valid in quoted attributes and in
     * block in an unescaped form.</li>
     *
     * <li>Surrogate pairs are passed through only if valid.</li>
     *
     * <li>Characters that are not <a
     * href="http://www.w3.org/TR/REC-xml/#charsets">valid according
     * to the XML specification</a> are replaced by a space character
     * as they could lead to parsing errors.  In particular only {@code #x9
     * | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] |
     * [#x10000-#x10FFFF]} are considered valid.</li>
     * </ul>
     *
     * @param input the input to encode
     * @return the encoded result
     */
    public static String forHtmlAttribute(String input) {
        return forXmlAttribute(input);
    }

    /**
     * See {@link #forHtmlAttribute(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forHtmlAttribute(Writer out, String input)
        throws IOException
    {
        forXmlAttribute(out, input);
    }


    /**
     * <p>Encodes for unquoted HTML attribute values.  {@link
     * #forHtml(String)} or {@link #forHtmlAttribute(String)} should
     * usually be preferred over this method as quoted attributes are
     * XHTML compliant.</p>
     *
     * <p>When using this method, the caller is not required to
     * provide quotes around the attribute (since it is encoded for
     * such context).  The caller should make sure that the attribute
     * value does not abut unsafe characters--and thus should usually
     * err on the side of including a space character after the
     * value.</p>
     *
     * <p>Use of this method is discouraged as quoted attributes are
     * generally more compatible and safer.  Also note, that no
     * attempt has been made to optimize this encoding, though it is
     * still probably faster than other encoding libraries.</p>
     *
     * <b>Example JSP Usage</b>
     * <pre>
     *     &lt;input value=&lt;%=Encode.forHtmlUnquotedAttribute(input)%&gt; &gt;
     * </pre>
     *
     * <table border="0" class="memberSummary" summary="Shows the input and results of encoding">
     *   <caption><b>Encoding Table</b></caption>
     *   <thead>
     *     <tr>
     *       <th align="left" class="colFirst">Input</th>
     *       <th align="left" class="colLast">Result</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr class="altColor">
     *         <td class="colFirst">{@code U+0009} (horizontal tab)</td>
     *         <td class="colLast">{@code &#9;}</td></tr>
     *     <tr class="rowColor">
     *         <td class="colFirst">{@code U+000A} (line feed)</td>
     *         <td class="colLast">{@code &#10;}</td></tr>
     *     <tr class="altColor">
     *         <td class="colFirst">{@code U+000C} (form feed)</td>
     *         <td class="colLast">{@code &#12;}</td></tr>
     *     <tr class="rowColor">
     *         <td class="colFirst">{@code U+000D} (carriage return)</td>
     *         <td class="colLast">{@code &#13;}</td></tr>
     *     <tr class="altColor">
     *         <td class="colFirst">{@code U+0020} (space)</td>
     *         <td class="colLast">{@code &#32;}</td></tr>
     *     <tr class="rowColor">
     *         <td class="colFirst">{@code &}</td>
     *         <td class="colLast">{@code &amp;}</td></tr>
     *     <tr class="altColor">
     *         <td class="colFirst">{@code <}</td>
     *         <td class="colLast">{@code &lt;}</td></tr>
     *     <tr class="rowColor">
     *         <td class="colFirst">{@code >}</td>
     *         <td class="colLast">{@code &gt;}</td></tr>
     *     <tr class="altColor">
     *         <td class="colFirst">{@code "}</td>
     *         <td class="colLast">{@code &#34;}</td></tr>
     *     <tr class="rowColor">
     *         <td class="colFirst">{@code '}</td>
     *         <td class="colLast">{@code &#39;}</td></tr>
     *     <tr class="altColor">
     *         <td class="colFirst">{@code /}</td>
     *         <td class="colLast">{@code &#47;}</td></tr>
     *     <tr class="rowColor">
     *         <td class="colFirst">{@code =}</td>
     *         <td class="colLast">{@code &#61;}</td></tr>
     *     <tr class="altColor">
     *         <td class="colFirst">{@code `}</td>
     *         <td class="colLast">{@code &#96;}</td></tr>
     *     <tr class="rowColor">
     *         <td class="colFirst">{@code U+0085} (next line)</td>
     *         <td class="colLast">{@code &#133;}</td></tr>
     *     <tr class="altColor">
     *         <td class="colFirst">{@code U+2028} (line separator)</td>
     *         <td class="colLast">{@code &#8232;}</td></tr>
     *     <tr class="rowColor">
     *         <td class="colFirst">{@code U+2029} (paragraph separator)</td>
     *         <td class="colLast">{@code &#8233;}</td></tr>
     *   </tbody>
     * </table>
     *
     * <p><b>Additional Notes</b></p>
     * <ul>
     * <li>The following characters are <i>not</i> encoded:
     * {@code 0-9, a-z, A-Z}, {@code !}, {@code
     * #}, {@code $}, {@code %},
     * {@code (}, {@code )}, {@code
     * *}, {@code +}, {@code ,},
     * {@code -}, {@code .}, {@code
     * [}, {@code \}, {@code ]},
     * {@code ^}, {@code _}, {@code
     * }}.</li>
     *
     * <li>Surrogate pairs are passed through only if valid.  Invalid
     * surrogate pairs are replaced by a hyphen (-).</li>
     *
     * <li>Characters in the C0 and C1 control blocks and not
     * otherwise listed above are considered invalid and replaced by a
     * hyphen (-) character.</li>
     *
     * <li>Unicode "non-characters" are replaced by hyphens (-).</li>
     * </ul>
     *
     * @param input the attribute value to be encoded.
     * @return the attribute value encoded for unquoted attribute
     * context.
     */
    public static String forHtmlUnquotedAttribute(String input) {
        return encode(Encoders.HTML_UNQUOTED_ATTRIBUTE_ENCODER, input);
    }

    /**
     * See {@link #forHtmlUnquotedAttribute(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forHtmlUnquotedAttribute(Writer out, String input)
        throws IOException
    {
        encode(Encoders.HTML_UNQUOTED_ATTRIBUTE_ENCODER, out, input);
    }


    // HTML comment encoding is not currently supported because
    // of the number of vendor-specific sequences that would need
    // to be handled (e.g. "<!--[if IE]-->"

//    public static String forHtmlComment(String input) {
//        // only alphanumeric and space, everything else becomes a space
//
//        // HTML comment context needs to avoid browser extensions
//        // such as "<!--[if IE]-->"
//        throw new UnsupportedOperationException();
//    }

    /**
     * Encodes for CSS strings.  The context must be surrounded by quotation
     * characters.  It is safe for use in both style blocks and attributes in
     * HTML.
     *
     * <b>Example JSP Usage</b>
     * <pre>
     *     &lt;div style="background: url('&lt;=Encode.forCssString(...)%&gt;');"&gt;
     *
     *     &lt;style type="text/css"&gt;
     *         background: url('&lt;%=Encode.forCssString(...)%&gt;');
     *     &lt;/style&gt;
     * </pre>
     *
     * <b>Encoding  Notes</b>
     * <ul>
     *
     * <li>The following characters are encoded using hexidecimal
     * encodings: {@code U+0000} - {@code U+001f},
     * {@code "},
     * {@code '},
     * {@code \},
     * {@code <},
     * {@code &},
     * {@code (},
     * {@code )},
     * {@code /},
     * {@code >},
     * {@code U+007f},
     * line separator ({@code U+2028}),
     * paragraph separator ({@code U+2029}).</li>
     *
     * <li>Any character requiring encoding is encoded as {@code \xxx}
     * where {@code xxx} is the shortest hexidecimal representation of
     * its Unicode code point (after decoding surrogate pairs if
     * necessary).  This encoding is never zero padded.  Thus, for
     * example, the tab character is encoded as {@code \9}, not {@code
     * \0009}.</li>
     *
     * <li>The encoder looks ahead 1 character in the input and
     * appends a space to an encoding to avoid the next character
     * becoming part of the hexidecimal encoded sequence.  Thus
     * &ldquo;{@code '1}&rdquo; is encoded as &ldquo;{@code \27
     * 1}&rdquo;, and not as &ldquo;{@code \271}&rdquo;.  If a space
     * is not necessary, it is not included, thus &ldquo;{@code
     * 'x}&rdquo; is encoded as &ldquo;{@code \27x}&rdquo;, and not as
     * &ldquo;{@code \27 x}&rdquo;.</li>
     *
     * <li>Surrogate pairs are passed through only if valid.  Invalid
     * surrogate pairs are replaced by an underscore (_).</li>
     *
     * <li>Unicode "non-characters" are replaced by underscores (_).</li>
     *
     * </ul>
     *
     * @param input the input to encode
     * @return the encoded result
     */
    public static String forCssString(String input) {
        // need to watch out for CSS expressions
        return encode(Encoders.CSS_STRING_ENCODER, input);
    }

    /**
     * See {@link #forCssString(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forCssString(Writer out, String input)
        throws IOException
    {
        encode(Encoders.CSS_STRING_ENCODER, out, input);
    }

    /**
     * Encodes for CSS URL contexts.  The context must be surrounded by {@code "url("}
     * and {@code ")"}.  It is safe for use in both style blocks and attributes in HTML.
     * Note: this does not do any checking on the quality or safety of the URL
     * itself.  The caller should insure that the URL is safe for embedding
     * (e.g. input validation) by other means.
     *
     * <b>Example JSP Usage</b>
     * <pre>
     *     &lt;div style="background:url(&lt;=Encode.forCssUrl(...)%&gt;);"&gt;
     *
     *     &lt;style type="text/css"&gt;
     *         background: url(&lt;%=Encode.forCssUrl(...)%&gt;);
     *     &lt;/style&gt;
     * </pre>
     * <b>Encoding  Notes</b>
     * <ul>
     *
     * <li>The following characters are encoded using hexidecimal
     * encodings: {@code U+0000} - {@code U+001f},
     * {@code "},
     * {@code '},
     * {@code \},
     * {@code <},
     * {@code &},
     * {@code /},
     * {@code >},
     * {@code U+007f},
     * line separator ({@code U+2028}),
     * paragraph separator ({@code U+2029}).</li>
     *
     * <li>Any character requiring encoding is encoded as {@code \xxx}
     * where {@code xxx} is the shortest hexidecimal representation of
     * its Unicode code point (after decoding surrogate pairs if
     * necessary).  This encoding is never zero padded.  Thus, for
     * example, the tab character is encoded as {@code \9}, not {@code
     * \0009}.</li>
     *
     * <li>The encoder looks ahead 1 character in the input and
     * appends a space to an encoding to avoid the next character
     * becoming part of the hexidecimal encoded sequence.  Thus
     * &ldquo;{@code '1}&rdquo; is encoded as &ldquo;{@code \27
     * 1}&rdquo;, and not as &ldquo;{@code \271}&rdquo;.  If a space
     * is not necessary, it is not included, thus &ldquo;{@code
     * 'x}&rdquo; is encoded as &ldquo;{@code \27x}&rdquo;, and not as
     * &ldquo;{@code \27 x}&rdquo;.</li>
     *
     * <li>Surrogate pairs are passed through only if valid.  Invalid
     * surrogate pairs are replaced by an underscore (_).</li>
     *
     * <li>Unicode "non-characters" are replaced by underscores (_).</li>
     *
     * </ul>
     *
     * @param input the input to encode
     * @return the encoded result
     */
    public static String forCssUrl(String input) {
        return encode(Encoders.CSS_URL_ENCODER, input);
    }

    /**
     * See {@link #forCssUrl(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forCssUrl(Writer out, String input)
        throws IOException
    {
        encode(Encoders.CSS_URL_ENCODER, out, input);
    }

    /**
     * <p>Performs percent-encoding of a URL according to RFC 3986.  The provided
     * URL is assumed to a valid URL.  This method does not do any checking on
     * the quality or safety of the URL itself.  In many applications it may
     * be better to use {@link java.net.URI} instead.  Note: this is a
     * particularly dangerous context to put untrusted content in, as for
     * example a "javascript:" URL provided by a malicious user would be
     * "properly" escaped, and still execute.</p>
     *
     * <b>Encoding Table</b>
     * <p>The following characters are <i>not</i> encoded:</p>
     * <pre>
     * U+20:   !   # $   &amp; ' ( ) * + , - . / 0 1 2 3 4 5 6 7 8 9 : ;   =   ?
     * U+40: @ A B C D E F G H I J K L M N O P Q R S T U V W X Y Z [   ]   _
     * U+60:   a b c d e f g h i j k l m n o p q r s t u v w x y z       ~
     * </pre>
     *
     * <b>Encoding Notes</b>
     * <ul>
     *
     *   <li>The single-quote character({@code '}) <b>is not encoded</b>.</li>
     *
     *   <li>This encoding is not intended to be used standalone.  The
     *   output should be encoded to the target context.  For example:
     *   {@code <a
     *   href="<%=Encode.forHtmlAttribute(Encode.forUri(uri))%>">...</a>}.
     *   (Note, the single-quote character ({@code '}) is not
     *   encoded.)</li>
     *
     *   <li>URL encoding is an encoding for bytes, not unicode.  The
     *   input string is thus first encoded as a sequence of UTF-8
     *   byte.  The bytes are then encoded as {@code %xx} where {@code
     *   xx} is the two-digit hexidecimal representation of the
     *   byte. (The implementation does this as one step for
     *   performance.)</li>
     *
     *   <li>Surrogate pairs are first decoded to a Unicode code point
     *   before encoding as UTF-8.</li>
     *
     *   <li>Invalid characters (e.g. partial or invalid surrogate
     *   pairs), are replaced with a hyphen ({@code -}) character.</li>
     *
     * </ul>
     *
     * @param input the input to encode
     * @return the encoded result
     */
    @Deprecated public static String forUri(String input) {
        return encode(Encoders.URI_ENCODER, input);
    }

    /**
     * See {@link #forUri(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     *
     * @deprecated  There is never a need to encode a complete URI with this form of encoding.
     */
    @Deprecated public static void forUri(Writer out, String input)
        throws IOException
    {
        encode(Encoders.URI_ENCODER, out, input);
    }

    /**
     * Performs percent-encoding for a component of a URI, such as a query
     * parameter name or value, path or query-string.  In particular this
     * method insures that special characters in the component do not get
     * interpreted as part of another component.
     *
     * <pre>
     *     &lt;a href="http://www.owasp.org/&lt;%=Encode.forUriComponent(...)%&gt;?query#fragment"&gt;
     *
     *     &lt;a href="/search?value=&lt;%=Encode.forUriComponent(...)%&gt;&amp;order=1#top"&gt;
     * </pre>
     *
     * <b>Encoding Table</b>
     * <p>The following characters are <i>not</i> encoded:</p>
     * <pre>
     * U+20:                           - .   0 1 2 3 4 5 6 7 8 9
     * U+40: @ A B C D E F G H I J K L M N O P Q R S T U V W X Y Z         _
     * U+60:   a b c d e f g h i j k l m n o p q r s t u v w x y z       ~
     * </pre>
     *
     * <b>Encoding Notes</b>
     * <ul>
     *
     *   <li>Unlike {@link #forUri(String)} this method is safe to be
     *   used in most containing contexts, including: HTML/XML, CSS,
     *   and JavaScript contexts.</li>
     *
     *   <li>URL encoding is an encoding for bytes, not unicode.  The
     *   input string is thus first encoded as a sequence of UTF-8
     *   byte.  The bytes are then encoded as {@code %xx} where {@code
     *   xx} is the two-digit hexidecimal representation of the
     *   byte. (The implementation does this as one step for
     *   performance.)</li>
     *
     *   <li>Surrogate pairs are first decoded to a Unicode code point
     *   before encoding as UTF-8.</li>
     *
     *   <li>Invalid characters (e.g. partial or invalid surrogate
     *   pairs), are replaced with a hyphen ({@code -}) character.</li>
     *
     * </ul>
     *
     * @param input the input to encode
     * @return the encoded result
     */
    public static String forUriComponent(String input) {
        return encode(Encoders.URI_COMPONENT_ENCODER, input);
    }

    /**
     * See {@link #forUriComponent(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forUriComponent(Writer out, String input)
        throws IOException
    {
        encode(Encoders.URI_COMPONENT_ENCODER, out, input);
    }

    /**
     * Encoder for XML and XHTML.  See {@link #forHtml(String)} for a
     * description of the encoding and context.
     *
     * @see #forHtml(String)
     * @param input the input to encode
     * @return the encoded result
     */
    public static String forXml(String input) {
        return encode(Encoders.XML_ENCODER, input);
    }

    /**
     * See {@link #forXml(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forXml(Writer out, String input)
        throws IOException
    {
        encode(Encoders.XML_ENCODER, out, input);
    }

    /**
     * Encoder for XML and XHTML text content.  See {@link
     * #forHtmlContent(String)} for description of encoding and
     * context.
     *
     * @see #forHtmlContent(String)
     * @param input the input to encode
     * @return the encoded result
     */
    public static String forXmlContent(String input) {
        return encode(Encoders.XML_CONTENT_ENCODER, input);
    }

    /**
     * See {@link #forXmlContent(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forXmlContent(Writer out, String input)
        throws IOException
    {
        encode(Encoders.XML_CONTENT_ENCODER, out, input);
    }

    /**
     * Encoder for XML and XHTML attribute content.  See {@link
     * #forHtmlAttribute(String)} for description of encoding and
     * context.
     *
     * @see #forHtmlAttribute(String)
     * @param input the input to encode
     * @return the encoded result
     */
    public static String forXmlAttribute(String input) {
        return encode(Encoders.XML_ATTRIBUTE_ENCODER, input);
    }

    /**
     * See {@link #forXmlAttribute(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forXmlAttribute(Writer out, String input)
        throws IOException
    {
        encode(Encoders.XML_ATTRIBUTE_ENCODER, out, input);
    }

    /**
     * Encoder for XML comments.  <strong>NOT FOR USE WITH
     * (X)HTML CONTEXTS.</strong>  (X)HTML comments may be interpreted by
     * browsers as something other than a comment, typically in vendor
     * specific extensions (e.g. {@code <--if[IE]-->}).
     * For (X)HTML it is recommend that unsafe content never be included
     * in a comment.
     *
     * <p>The caller must provide the comment start and end sequences.</p>
     *
     * <p>This method replaces all invalid XML characters with spaces,
     * and replaces the "--" sequence (which is invalid in XML comments)
     * with "-~" (hyphen-tilde).  <b>This encoding behavior may change
     * in future releases.</b>  If the comments need to be decoded, the
     * caller will need to come up with their own encode/decode system.</p>
     *
     * <pre>
     *     out.println("&lt;?xml version='1.0'?&gt;");
     *     out.println("&lt;data&gt;");
     *     out.println("&lt;!-- "+Encode.forXmlComment(comment)+" --&gt;");
     *     out.println("&lt;/data&gt;");
     * </pre>
     *
     * @param input the input to encode
     * @return the encoded result
     */
    public static String forXmlComment(String input) {
        return encode(Encoders.XML_COMMENT_ENCODER, input);
    }

    /**
     * See {@link #forXmlComment(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forXmlComment(Writer out, String input)
        throws IOException
    {
        encode(Encoders.XML_COMMENT_ENCODER, out, input);
    }

    /**
     * Encodes data for an XML CDATA section.  On the chance that the input
     * contains a terminating {@code "]]>"}, it will be replaced by
     * {@code "]]>]]<![CDATA[>"}.
     * As with all XML contexts, characters that are invalid according to the
     * XML specification will be replaced by a space character.   Caller must
     * provide the CDATA section boundaries.
     *
     * <pre>
     *     &lt;xml-data&gt;&lt;![CDATA[&lt;%=Encode.forCDATA(...)%&gt;]]&gt;&lt;/xml-data&gt;
     * </pre>
     *
     * @param input the input to encode
     * @return the encoded result
     */
    public static String forCDATA(String input) {
        return encode(Encoders.CDATA_ENCODER, input);
    }

    /**
     * See {@link #forCDATA(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forCDATA(Writer out, String input)
        throws IOException
    {
        encode(Encoders.CDATA_ENCODER, out, input);
    }

    /**
     * Encodes for a Java string.  This method will use "\b", "\t", "\r", "\f",
     * "\n", "\"", "\'", "\\", octal and unicode escapes.  Valid surrogate
     * pairing is not checked.   The caller must provide the enclosing quotation
     * characters.  This method is useful for when writing code generators and
     * outputting debug messages.
     *
     * <pre>
     *     out.println("public class Hello {");
     *     out.println("    public static void main(String[] args) {");
     *     out.println("        System.out.println(\"" + Encode.forJava(message) + "\");");
     *     out.println("    }");
     *     out.println("}");
     * </pre>
     *
     * @param input the input to encode
     * @return the input encoded for java strings.
     */
    public static String forJava(String input) {
        return encode(Encoders.JAVA_ENCODER, input);
    }

    /**
     * See {@link #forJava(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forJava(Writer out, String input)
        throws IOException
    {
        encode(Encoders.JAVA_ENCODER, out, input);
    }

    /**
     * <p>Encodes for a JavaScript string.  It is safe for use in HTML
     * script attributes (such as {@code onclick}), script
     * blocks, JSON files, and JavaScript source.  The caller MUST
     * provide the surrounding quotation characters for the string.
     * Since this performs additional encoding so it can work in all
     * of the JavaScript contexts listed, it may be slightly less
     * efficient than using one of the methods targetted to a specific
     * JavaScript context ({@link #forJavaScriptAttribute(String)},
     * {@link #forJavaScriptBlock}, {@link #forJavaScriptSource}).
     * Unless you are interested in saving a few bytes of output or
     * are writing a framework on top of this library, it is recommend
     * that you use this method over the others.</p>
     *
     * <b>Example JSP Usage:</b>
     * <pre>
     *    &lt;button onclick="alert('&lt;%=Encode.forJavaScript(data)%&gt;');"&gt;
     *    &lt;script type="text/javascript"&gt;
     *        var data = "&lt;%=Encode.forJavaScript(data)%&gt;";
     *    &lt;/script&gt;
     * </pre>
     *
     * <table cellspacing="1" class="memberSummary" cellpadding="1" border="0">
     *   <caption><b>Encoding Description</b></caption>
     *   <thead>
     *     <tr>
     *       <th align="left" colspan="2" class="colFirst">Input Character</th>
     *       <th align="left" class="colLast">Encoded Result</th>
     *       <th align="left" class="colLast">Notes</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr class="altColor">
     *       <td class="colFirst">U+0008</td><td><i>BS</i></td>
     *       <td class="colLast"><code>\b</code></td>
     *       <td class="colLast">Backspace character</td>
     *     </tr>
     *     <tr class="rowColor">
     *       <td class="colFirst">U+0009</td><td><i>HT</i></td>
     *       <td class="colLast"><code>\t</code></td>
     *       <td class="colLast">Horizontal tab character</td>
     *     </tr>
     *     <tr class="altColor">
     *       <td class="colFirst">U+000A</td><td><i>LF</i></td>
     *       <td class="colLast"><code>\n</code></td>
     *       <td class="colLast">Line feed character</td>
     *     </tr>
     *     <tr class="rowColor">
     *       <td class="colFirst">U+000C</td><td><i>FF</i></td>
     *       <td class="colLast"><code>\f</code></td>
     *       <td class="colLast">Form feed character</td>
     *     </tr>
     *     <tr class="altColor">
     *       <td class="colFirst">U+000D</td><td><i>CR</i></td>
     *       <td class="colLast"><code>\r</code></td>
     *       <td class="colLast">Carriage return character</td>
     *     </tr>
     *     <tr class="rowColor">
     *       <td class="colFirst">U+0022</td><td><code>"</code></td>
     *       <td class="colLast"><code>\x22</code></td>
     *       <td class="colLast">The encoding <code>\"</code> is not used here because
     *       it is not safe for use in HTML attributes.  (In HTML
     *       attributes, it would also be correct to use
     *       "\&amp;quot;".)</td>
     *     </tr>
     *     <tr class="altColor">
     *       <td class="colFirst">U+0026</td><td><code>&amp;</code></td>
     *       <td class="colLast"><code>\x26</code></td>
     *       <td class="colLast">Ampersand character</td>
     *     </tr>
     *     <tr class="rowColor">
     *       <td class="colFirst">U+0027</td><td><code>'</code></td>
     *       <td class="colLast"><code>\x27</code></td>
     *       <td class="colLast">The encoding <code>\'</code> is not used here because
     *       it is not safe for use in HTML attributes.  (In HTML
     *       attributes, it would also be correct to use
     *       "\&amp;#39;".)</td>
     *     </tr>
     *     <tr class="altColor">
     *       <td class="colFirst">U+002F</td><td><code>/</code></td>
     *       <td class="colLast"><code>\/</code></td>
     *       <td class="colLast">This encoding is used to avoid an input sequence
     *       "&lt;/" from prematurely terminating a &lt;/script&gt;
     *       block.</td>
     *     </tr>
     *     <tr class="rowColor">
     *       <td class="colFirst">U+005C</td><td><code>\</code></td>
     *       <td class="colLast"><code>\\</code></td>
     *       <td class="colLast"></td>
     *     </tr>
     *     <tr class="altColor">
     *       <td class="colFirst" colspan="2">U+0000&nbsp;to&nbsp;U+001F</td>
     *       <td class="colLast"><code>\x##</code></td>
     *       <td class="colLast">Hexadecimal encoding is used for characters in this
     *       range that were not already mentioned in above.</td>
     *     </tr>
     *   </tbody>
     * </table>
     *
     * @param input the input string to encode
     * @return the input encoded for JavaScript
     * @see #forJavaScriptAttribute(String)
     * @see #forJavaScriptBlock(String)
     */
    public static String forJavaScript(String input) {
        return encode(Encoders.JAVASCRIPT_ENCODER, input);
    }

    /**
     * See {@link #forJavaScript(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forJavaScript(Writer out, String input)
        throws IOException
    {
        encode(Encoders.JAVASCRIPT_ENCODER, out, input);
    }

    /**
     * <p>This method encodes for JavaScript strings contained within
     * HTML script attributes (such as {@code onclick}).  It is
     * NOT safe for use in script blocks.  The caller MUST provide the
     * surrounding quotation characters.  This method performs the
     * same encode as {@link #forJavaScript(String)} with the
     * exception that <code>/</code> is not escaped.</p>
     *
     * <p><strong>Unless you are interested in saving a few bytes of
     * output or are writing a framework on top of this library, it is
     * recommend that you use {@link #forJavaScript(String)} over this
     * method.</strong></p>
     *
     * <b>Example JSP Usage:</b>
     * <pre>
     *    &lt;button onclick="alert('&lt;%=Encode.forJavaScriptAttribute(data)%&gt;');"&gt;
     * </pre>
     *
     * @param input the input string to encode
     * @return the input encoded for JavaScript
     * @see #forJavaScript(String)
     * @see #forJavaScriptBlock(String)
     */
    public static String forJavaScriptAttribute(String input) {
        return encode(Encoders.JAVASCRIPT_ATTRIBUTE_ENCODER, input);
    }

    /**
     * See {@link #forJavaScriptAttribute(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forJavaScriptAttribute(Writer out, String input)
        throws IOException
    {
        encode(Encoders.JAVASCRIPT_ATTRIBUTE_ENCODER, out, input);
    }

    /**
     * <p>This method encodes for JavaScript strings contained within
     * HTML script blocks.  It is NOT safe for use in script
     * attributes (such as <code>onclick</code>).  The caller must
     * provide the surrounding quotation characters.  This method
     * performs the same encode as {@link #forJavaScript(String)} with
     * the exception that <code>"</code> and <code>'</code> are
     * encoded as <code>\"</code> and <code>\'</code>
     * respectively.</p>
     *
     * <p><strong>Unless you are interested in saving a few bytes of
     * output or are writing a framework on top of this library, it is
     * recommend that you use {@link #forJavaScript(String)} over this
     * method.</strong></p>
     *
     * <b>Example JSP Usage:</b>
     * <pre>
     *    &lt;script type="text/javascript"&gt;
     *        var data = "&lt;%=Encode.forJavaScriptBlock(data)%&gt;";
     *    &lt;/script&gt;
     * </pre>
     *
     * @param input the input string to encode
     * @return the input encoded for JavaScript
     * @see #forJavaScript(String)
     * @see #forJavaScriptAttribute(String)
     */
    public static String forJavaScriptBlock(String input) {
        return encode(Encoders.JAVASCRIPT_BLOCK_ENCODER, input);
    }

    /**
     * See {@link #forJavaScriptBlock(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forJavaScriptBlock(Writer out, String input)
        throws IOException
    {
        encode(Encoders.JAVASCRIPT_BLOCK_ENCODER, out, input);
    }

    /**
     * <p>This method encodes for JavaScript strings contained within
     * a JavaScript or JSON file.  <strong>This method is NOT safe for
     * use in ANY context embedded in HTML.</strong> The caller must
     * provide the surrounding quotation characters.  This method
     * performs the same encode as {@link #forJavaScript(String)} with
     * the exception that <code>/</code> and <code>&amp;</code> are not
     * escaped and <code>"</code> and <code>'</code> are encoded as
     * <code>\"</code> and <code>\'</code> respectively.</p>
     *
     * <p><strong>Unless you are interested in saving a few bytes of
     * output or are writing a framework on top of this library, it is
     * recommend that you use {@link #forJavaScript(String)} over this
     * method.</strong></p>
     *
     * <b>Example JSP Usage:</b>
     * This example is serving up JavaScript source directly:
     * <pre>
     *    &lt;%@page contentType="text/javascript; charset=UTF-8"%&gt;
     *    var data = "&lt;%=Encode.forJavaScriptSource(data)%&gt;";
     * </pre>
     *
     * This example is serving up JSON data (users of this use-case
     * are encouraged to read up on "JSON Hijacking"):
     * <pre>
     *    &lt;%@page contentType="application/json; charset=UTF-8"%&gt;
     *    &lt;% myapp.jsonHijackingPreventionMeasure(); %&gt;
     *    {"data":"&lt;%=Encode.forJavaScriptSource(data)%&gt;"}
     * </pre>
     *
     * @param input the input string to encode
     * @return the input encoded for JavaScript
     * @see #forJavaScript(String)
     * @see #forJavaScriptAttribute(String)
     * @see #forJavaScriptBlock(String)
     */
    public static String forJavaScriptSource(String input) {
        return encode(Encoders.JAVASCRIPT_SOURCE_ENCODER, input);
    }

    /**
     * See {@link #forJavaScriptSource(String)} for description of encoding.  This
     * version writes directly to a Writer without an intervening string.
     *
     * @param out where to write encoded output
     * @param input the input string to encode
     * @throws IOException if thrown by writer
     */
    public static void forJavaScriptSource(Writer out, String input)
        throws IOException
    {
        encode(Encoders.JAVASCRIPT_SOURCE_ENCODER, out, input);
    }

    // Additional?
    // MySQL
    // PostreSQL
    // Oracle
    // ...

    /**
     * Core encoding loop shared by public methods.  It first uses the
     * encoder to scan the input for characters that need encoding.  If
     * no characters require encoding, the input string is returned.
     * Otherwise a buffer is used to encode the remainder
     * of the input.
     *
     * @param encoder the encoder to use
     * @param str the string to encode
     * @return the input string encoded with the provided encoder.
     */
    static String encode(Encoder encoder, String str) {
        if (str == null) {
            // consistent with String.valueOf(...) use "null" for null.
            str = "null";
        }

        // quick pass--see if we need to actually encode anything, if not
        // return the value unchanged.
        final int n = str.length();
        int j = encoder.firstEncodedOffset(str, 0, n);

        if (j == n) {
            return str;
        }

        // otherwise, we need to encode.  We use a buffer to avoid
        // excessive memory allocation for these calls.  Note: this means that
        // an encoder implementation must NEVER call this method internally.
        return new Buffer().encode(encoder, str, j);
    }

    /**
     * Core encoding loop shared by public methods.  It first uses the
     * encoder to scan the input for characters that need encoding.  If no
     * characters require encoding, the input string is written directly to
     * the writer.  Otherwise a buffer is used to encode the
     * remainder of the input to the buffers.  This version saves a wrapping
     * in an String.
     *
     * @param encoder the encoder to use
     * @param out the writer for the encoded output
     * @param str the string to encode
     * @throws IOException if thrown by the writer
     */
    static void encode(Encoder encoder, Writer out, String str)
        throws IOException
    {
        if (str == null) {
            // consistent with String.valueOf(...) use "null" for null.
            str = "null";
        }

        // quick pass--see if we need to actually encode anything, if not
        // return the value unchanged.
        final int n = str.length();
        int j = encoder.firstEncodedOffset(str, 0, n);

        if (j == n) {
            out.write(str);
            return;
        }

        // otherwise, we need to encode.  We use a buffer to avoid
        // excessive memory allocation for these calls.  Note: this means that
        // an encoder implementation must NEVER call this method internally.
        new Buffer().encode(encoder, out, str, j);
    }

    /**
     * A buffer used for encoding.
     */
    static class Buffer {
        /**
         * Input buffer size, used to extract a copy of the input
         * from a string and then send to the encoder.
         */
        static final int INPUT_BUFFER_SIZE = 1024;
        /**
         * Output buffer size used to store the encoded output before
         * wrapping in a string.
         */
        static final int OUTPUT_BUFFER_SIZE = INPUT_BUFFER_SIZE * 2;

        /**
         * The input buffer.  A heap-allocated, array-backed buffer of
         * INPUT_BUFFER_SIZE used for holding the characters to encode.
         */
        final CharBuffer _input = CharBuffer.allocate(INPUT_BUFFER_SIZE);
        /**
         * The output buffer.  A heap-allocated, array-backed buffer of
         * OUTPUT_BUFFER_SIZE used for holding the encoded output.
         */
        final CharBuffer _output = CharBuffer.allocate(OUTPUT_BUFFER_SIZE);

        /**
         * The core String encoding routine of this class.  It uses the input
         * and output buffers to allow the encoders to work in reuse arrays.
         * When the input and/or output exceeds the capacity of the reused
         * arrays, temporary ones are allocated and then discarded after
         * the encode is done.
         *
         * @param encoder the encoder to use
         * @param str the string to encode
         * @param j the offset in {@code str} to start encoding
         * @return the encoded result
         */
        String encode(Encoder encoder, String str, int j) {
            final int n = str.length();
            final int remaining = n - j;

            if (remaining <= INPUT_BUFFER_SIZE && j <= OUTPUT_BUFFER_SIZE) {
                // the remaining input to encode fits completely in the pre-
                // allocated buffer.
                str.getChars(0, j, _output.array(), 0);
                str.getChars(j, n, _input.array(), 0);

                _input.limit(remaining).position(0);
                _output.clear().position(j);

                CoderResult cr = encoder.encodeArrays(_input, _output, true);
                if (cr.isUnderflow()) {
                    return new String(_output.array(), 0, _output.position());
                }

                // else, it's an overflow, we need to use a new output buffer
                // we'll allocate this buffer to be the exact size of the worst
                // case, guaranteeing a second overflow would not be possible.
                CharBuffer tmp = CharBuffer.allocate(_output.position()
                            + encoder.maxEncodedLength(_input.remaining()));

                // copy over everything that has been encoded so far
                tmp.put(_output.array(), 0, _output.position());

                cr = encoder.encodeArrays(_input, tmp, true);
                if (cr.isOverflow()) {
                    throw new AssertionError("unexpected result from encoder");
                }

                return new String(tmp.array(), 0, tmp.position());
            } else {
                // the input it too large for our pre-allocated buffers
                // we'll use a temporary direct heap allocation
                final int m = j + encoder.maxEncodedLength(remaining);
                CharBuffer buffer = CharBuffer.allocate(m);
                str.getChars(0, j, buffer.array(), 0);
                str.getChars(j, n, buffer.array(), m - remaining);

                CharBuffer input = buffer.duplicate();
                input.limit(m).position(m-remaining);
                buffer.position(j);

                CoderResult cr = encoder.encodeArrays(input, buffer, true);

                if (cr.isOverflow()) {
                    throw new AssertionError("unexpected result from encoder");
                }

                return new String(buffer.array(), 0, buffer.position());
            }
        }

        /**
         * The core Writer encoding routing of this class.  It uses the
         * input and output buffers to allow the encoders to reuse arrays.
         * Unlike the string version, this method will never allocate more
         * memory, instead encoding is done in batches and flushed to the
         * writer in batches as large as possible.
         *
         * @param encoder the encoder to use
         * @param out where to write the encoded output
         * @param str the string to encode
         * @param j the position in the string at which the first character
         * needs encoding.
         * @throws IOException if thrown by the writer.
         */
        void encode(Encoder encoder, Writer out, String str, int j)
            throws IOException
        {
            out.write(str, 0, j);

            final int n = str.length();

            _input.clear();
            _output.clear();

            final char[] inputArray = _input.array();
            final char[] outputArray = _output.array();

            for (;;) {
                final int remainingInput = n - j;
                final int startPosition = _input.position();
                final int batchSize = Math.min(remainingInput, _input.remaining());
                str.getChars(j, j+batchSize, inputArray, startPosition);

                _input.limit(startPosition + batchSize);


                for (;;) {
                    CoderResult cr = encoder.encodeArrays(
                        _input, _output, batchSize == remainingInput);

                    if (cr.isUnderflow()) {
                        // get next input batch
                        break;
                    }

                    // else, output buffer full, flush and continue.
                    out.write(outputArray, 0, _output.position());
                    _output.clear();
                }

                j += _input.position() - startPosition;

                if (j == n) {
                    // done.  flush remaining output buffer and return
                    out.write(outputArray, 0, _output.position());
                    return;
                }

                _input.compact();
            }
        }
    }
}
