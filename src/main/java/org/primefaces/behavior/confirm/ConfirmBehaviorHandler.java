/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.BehaviorConfig;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import org.primefaces.behavior.base.AbstractBehaviorHandler;

public class ConfirmBehaviorHandler extends AbstractBehaviorHandler<ConfirmBehavior> {

    private final TagAttribute header;
    private final TagAttribute message;
    private final TagAttribute icon;
    private final TagAttribute disabled;
    private final TagAttribute beforeShow;
    private final TagAttribute escape;

    public ConfirmBehaviorHandler(BehaviorConfig config) {
        super(config);
        this.header = this.getAttribute(ConfirmBehavior.PropertyKeys.header.name());
        this.message = this.getAttribute(ConfirmBehavior.PropertyKeys.message.name());
        this.icon = this.getAttribute(ConfirmBehavior.PropertyKeys.icon.name());
        this.disabled = this.getAttribute(ConfirmBehavior.PropertyKeys.disabled.name());
        this.beforeShow = this.getAttribute(ConfirmBehavior.PropertyKeys.beforeShow.name());
        this.escape = this.getAttribute(ConfirmBehavior.PropertyKeys.escape.name());
    }

    @Override
    protected ConfirmBehavior createBehavior(FaceletContext ctx, String eventName, UIComponent parent) {
        Application application = ctx.getFacesContext().getApplication();
        ConfirmBehavior behavior = (ConfirmBehavior) application.createBehavior(ConfirmBehavior.BEHAVIOR_ID);

        setBehaviorAttribute(ctx, behavior, this.header, ConfirmBehavior.PropertyKeys.header.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.message, ConfirmBehavior.PropertyKeys.message.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.icon, ConfirmBehavior.PropertyKeys.icon.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.disabled, ConfirmBehavior.PropertyKeys.disabled.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.beforeShow, ConfirmBehavior.PropertyKeys.beforeShow.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.escape, ConfirmBehavior.PropertyKeys.escape.getExpectedType());

        return behavior;
    }

}
