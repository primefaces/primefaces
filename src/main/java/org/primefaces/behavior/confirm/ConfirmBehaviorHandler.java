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
