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
package org.primefaces.component.password;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.Widget;

@FacesComponentBase
public abstract class PasswordBase extends AbstractPrimeHtmlInputText implements Widget  {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PasswordRenderer";

    public PasswordBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "When enabled, displays strength indicator with feedback.", defaultValue = "false")
    public abstract boolean isFeedback();

    @Property(description = "When enabled, displays feedback inline.", defaultValue = "false")
    public abstract boolean isInline();

    @Property(description = "Label for prompt state.")
    public abstract String getPromptLabel();

    @Property(description = "Label for weak password state.")
    public abstract String getWeakLabel();

    @Property(description = "Label for good password state.")
    public abstract String getGoodLabel();

    @Property(description = "Label for strong password state.")
    public abstract String getStrongLabel();

    @Property(description = "When enabled, redisplay the password value.", defaultValue = "false")
    public abstract boolean isRedisplay();

    @Property(description = "Search expression to match password with another password component.")
    public abstract String getMatch();

    @Property(description = "Event to show the password.")
    public abstract String getShowEvent();

    @Property(description = "Event to hide the password.")
    public abstract String getHideEvent();

    @Property(description = "When enabled, ignores LastPass browser extension.", defaultValue = "false")
    public abstract boolean isIgnoreLastPass();

    @Property(description = "When enabled, shows toggle mask button.", defaultValue = "false")
    public abstract boolean isToggleMask();
}