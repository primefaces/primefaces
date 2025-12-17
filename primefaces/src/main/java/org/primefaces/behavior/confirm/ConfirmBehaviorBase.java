/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.cdk.api.FacesBehaviorBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.api.behavior.PrimeClientBehavior;

@FacesBehaviorBase
public abstract class ConfirmBehaviorBase extends PrimeClientBehavior {

    @Property(description = "Source component to attach confirmation to.")
    public abstract String getSource();

    @Property(description = "Type of confirmation dialog.", defaultValue = "dialog")
    public abstract String getType();

    @Property(description = "Header text of the confirmation dialog.")
    public abstract String getHeader();

    @Property(description = "Message text of the confirmation dialog.")
    public abstract String getMessage();

    @Property(description = "Icon to display in the confirmation dialog.")
    public abstract String getIcon();

    @Property(description = "Label for the yes button.")
    public abstract String getYesButtonLabel();

    @Property(description = "CSS class for the yes button.")
    public abstract String getYesButtonClass();

    @Property(description = "Icon for the yes button.")
    public abstract String getYesButtonIcon();

    @Property(description = "Label for the no button.")
    public abstract String getNoButtonLabel();

    @Property(description = "CSS class for the no button.")
    public abstract String getNoButtonClass();

    @Property(description = "Icon for the no button.")
    public abstract String getNoButtonIcon();

    @Property(description = "Disables the confirmation behavior.", defaultValue = "false")
    public abstract boolean isDisabled();

    @Property(description = "JavaScript callback to execute before showing the confirmation dialog.")
    public abstract String getBeforeShow();

    @Property(description = "Whether to escape HTML in the message.", defaultValue = "true")
    public abstract boolean isEscape();
}

