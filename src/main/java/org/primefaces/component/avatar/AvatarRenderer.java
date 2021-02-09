/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.avatar;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.LangUtils;
import org.primefaces.util.SharedStringBuilder;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AvatarRenderer extends CoreRenderer {

    private static final Pattern LETTER_PATTTERN = Pattern.compile("\\b[a-zA-Z]");
    private static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/";
    private static final String SB_AVATAR = AvatarRenderer.class.getName() + "#avatar";

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Avatar avatar = (Avatar) component;
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                .add(Avatar.STYLE_CLASS)
                .add(avatar.getStyleClass())
                .add("circle".equals(avatar.getShape()), Avatar.CIRCLE_CLASS)
                .add("large".equals(avatar.getSize()), Avatar.SIZE_LARGE_CLASS)
                .add("xlarge".equals(avatar.getSize()), Avatar.SIZE_XLARGE_CLASS)
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", avatar.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        String label = calculateLabel(context, avatar);
        String style = avatar.getStyle();
        if (avatar.isDynamicColor() && label != null) {
            String colorCss = generateBackgroundColor(avatar.getLabel());
            style = style == null ? colorCss : colorCss + style;
        }

        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        if (avatar.getChildCount() > 0) {
            renderChildren(context, avatar);
        }
        else {
            encodeDefaultContent(context, avatar, label);
        }

        writer.endElement("div");
    }

    protected void encodeDefaultContent(FacesContext context, Avatar avatar, String label) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (LangUtils.isNotBlank(label)) {
            writer.startElement("span", null);
            String textClass = getStyleClassBuilder(context)
                        .add(Avatar.SIZE_TEXT_CLASS)
                        .add(avatar.isDynamicColor(), Avatar.DYNAMIC_COLOR_CLASS)
                        .build();
            writer.writeAttribute("class", textClass, "styleClass");
            writer.write(label);
            writer.endElement("span");
        }
        else if (avatar.getIcon() != null) {
            String iconStyleClass = getStyleClassBuilder(context)
                .add(Avatar.ICON_CLASS)
                .add(avatar.getIcon())
                .build();

            writer.startElement("span", null);
            writer.writeAttribute("class", iconStyleClass, "styleClass");
            writer.endElement("span");
        }
        else if (avatar.getGravatar() != null) {
            writer.startElement("img", null);
            writer.writeAttribute("src", generateGravatar(context, avatar), "src");
            writer.endElement("img");
        }
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    /**
     * Generates a label based on the text if its more than 2 characters. Example: PrimeFaces Rocks = PR
     *
     * @param avatar the Avatar component
     * @return the calculated label text.
     */
    protected String calculateLabel(FacesContext context, Avatar avatar) {
        String value = avatar.getLabel();
        if (value == null || value.length() <= 2) {
            return value;
        }
        Matcher m = LETTER_PATTTERN.matcher(value);
        StringBuilder sb = SharedStringBuilder.get(context, SB_AVATAR);
        while (m.find()) {
            sb.append(m.group());
        }
        String initials = sb.toString();
        initials = initials.length() == 1 ? initials : initials.charAt(0) + initials.substring(initials.length() - 1);
        return initials;
    }

    /**
     * Generates a dynamic color based on the hash of the label.
     *
     * @param label the label to generate the color for
     * @return the new color and background color styles
     */
    protected String generateBackgroundColor(String label) {
        return "color:#fff;background-color: hsl(" + Math.abs((label.hashCode() % 40) * 9) + ", 100%, 50%);";
    }

    /**
     * Generate a Gravatar URL for an email addressed based on API docs.
     *
     * @see <a href="https://en.gravatar.com/site/implement/images/">Image Requests</a>
     * @param context the Face
     * @param avatar the Avatar to create a Gravatar for
     * @return the URL to retrieve the Gravatar image
     */
    protected String generateGravatar(FacesContext context, Avatar avatar) {
        String email = avatar.getGravatar();
        String config = avatar.getGravatarConfig();
        String url;
        try {
            StringBuilder sb = SharedStringBuilder.get(context, SB_AVATAR);
            sb.append(GRAVATAR_URL);
            generateMailHash(sb, email);
            if (LangUtils.isNotBlank(config)) {
                sb.append('?').append(config);
            }
            url = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new FacesException("Failed to generate Gravatar URL for value: " + email);
        }
        return url;
    }

    /**
     * Converts email address into a hash code for Gravatar API.
     *
     * @param sb the Stringbuilder to add the email address to
     * @param email the email to encode
     * @throws NoSuchAlgorithmException if hash can't be encoded
     */
    protected void generateMailHash(StringBuilder sb, String email) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5"); // NOSONAR
        md.update(email.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
    }
}
