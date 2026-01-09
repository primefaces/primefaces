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
package org.primefaces.behavior.confirm;

import org.primefaces.cdk.api.FacesBehaviorDescription;
import org.primefaces.cdk.api.FacesBehaviorHandler;
import org.primefaces.component.api.Confirmable;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.ClientBehaviorContext;
import jakarta.faces.component.behavior.FacesBehavior;
import jakarta.faces.context.FacesContext;

import org.json.JSONObject;

@FacesBehavior(ConfirmBehavior.BEHAVIOR_ID)
@FacesBehaviorHandler(ConfirmBehaviorHandler.class)
@FacesBehaviorDescription("Confirm is a behavior element used to integrate with global confirm dialog.")
public class ConfirmBehavior extends ConfirmBehaviorBaseImpl {

    public static final String BEHAVIOR_ID = "org.primefaces.behavior.ConfirmBehavior";

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {
        FacesContext context = behaviorContext.getFacesContext();
        UIComponent component = behaviorContext.getComponent();

        if (isDisabled()) {
            if (component instanceof Confirmable) {
                ((Confirmable) component).setConfirmationScript(null);
            }

            return null;
        }

        String source = getSource();
        String type = JSONObject.quote(getType());
        String headerText = JSONObject.quote(getHeader());

        String messageText;
        UIComponent messageFacetComponent = component.getFacet("confirmMessage");
        if (FacetUtils.shouldRenderFacet(messageFacetComponent)) {
            messageText = JSONObject.quote(ComponentUtils.encodeComponent(messageFacetComponent, context));
        }
        else {
            messageText = JSONObject.quote(getMessage());
        }
        String beforeShow = JSONObject.quote(getBeforeShow());
        String yesButtonClass = JSONObject.quote(getYesButtonClass());
        String yesButtonLabel = JSONObject.quote(getYesButtonLabel());
        String yesButtonIcon = JSONObject.quote(getYesButtonIcon());
        String noButtonClass = JSONObject.quote(getNoButtonClass());
        String noButtonLabel = JSONObject.quote(getNoButtonLabel());
        String noButtonIcon = JSONObject.quote(getNoButtonIcon());

        source = (source == null) ? component.getClientId(context) : source;

        if (component instanceof Confirmable) {
            String sourceProperty = (source == null || "this".equals(source)) ? "source:this" : "source:\"" + source + "\"";
            String icon = getIcon();

            String script = "PrimeFaces.confirm({" + sourceProperty
                                                   + ",type:" + type
                                                   + ",escape:" + isEscape()
                                                   + ",header:" + headerText
                                                   + ",message:" + messageText
                                                   + ",yesButtonClass:" + yesButtonClass
                                                   + ",yesButtonLabel:" + yesButtonLabel
                                                   + ",yesButtonIcon:" + yesButtonIcon
                                                   + ",noButtonClass:" + noButtonClass
                                                   + ",noButtonLabel:" + noButtonLabel
                                                   + ",noButtonIcon:" + noButtonIcon
                                                   + ",icon:\"" + (icon == null ? "" : icon)
                                                   + "\",beforeShow:" + beforeShow
                                                   + "});return false;";
            ((Confirmable) component).setConfirmationScript(script);

            return null;
        }
        else {
            throw new FacesException("Component " + source + " is not a Confirmable. "
                    + "ConfirmBehavior can only be attached to components that implement "
                    + Confirmable.class.getName() + " interface");
        }
    }

}
