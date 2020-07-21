/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.renderkit;

import javax.faces.application.FacesMessage;
import org.primefaces.component.api.UINotification;
import org.primefaces.component.messages.Messages;
import org.primefaces.util.MessageFactory;

public class UINotificationRenderer extends CoreRenderer {

    protected boolean shouldRender(UINotification component, FacesMessage message, String severityName) {
        String severityLevel = component.getSeverity();
        return (!message.isRendered() || component.isRedisplay())
                && (severityLevel == null || severityLevel.contains(severityName));
    }

    protected String getSeverityName(FacesMessage message) {
        int ordinal = message.getSeverity().getOrdinal();
        String severity = null;

        if (ordinal == FacesMessage.SEVERITY_INFO.getOrdinal()) {
            severity = "info";
        }
        else if (ordinal == FacesMessage.SEVERITY_ERROR.getOrdinal()) {
            severity = "error";
        }
        else if (ordinal == FacesMessage.SEVERITY_WARN.getOrdinal()) {
            severity = "warn";
        }
        else if (ordinal == FacesMessage.SEVERITY_FATAL.getOrdinal()) {
            severity = "fatal";
        }

        return severity;
    }

    /**
     * Gets the severity in I18N human readable text for ARIA screen readers.
     *
     * @param message the message to process
     * @return the human readable text for the severity
     */
    protected String getSeverityText(FacesMessage message) {
        int ordinal = message.getSeverity().getOrdinal();
        String severity = null;

        if (ordinal == FacesMessage.SEVERITY_INFO.getOrdinal()) {
            severity = MessageFactory.getMessage(Messages.ARIA_INFO, null);
        }
        else if (ordinal == FacesMessage.SEVERITY_ERROR.getOrdinal()) {
            severity = MessageFactory.getMessage(Messages.ARIA_ERROR, null);
        }
        else if (ordinal == FacesMessage.SEVERITY_WARN.getOrdinal()) {
            severity = MessageFactory.getMessage(Messages.ARIA_WARN, null);
        }
        else if (ordinal == FacesMessage.SEVERITY_FATAL.getOrdinal()) {
            severity = MessageFactory.getMessage(Messages.ARIA_FATAL, null);
        }

        return severity;
    }

    protected String getClientSideSeverity(String severity) {
        if (severity == null) {
            return "all,error"; // validation.js checks if severity contains error.
        }
        return severity;
    }

}
