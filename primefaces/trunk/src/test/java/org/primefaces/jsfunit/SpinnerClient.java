/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.jsfunit;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import java.io.IOException;
import org.jboss.jsfunit.jsfsession.JSFClientSession;

public class SpinnerClient extends PrimeFacesClient {

    public SpinnerClient(JSFClientSession client) {
        super(client);
    }

    public SpinnerClient(JSFClientSession client, String componentId) {
        super(client, componentId);
    }

    public SpinnerClient spinDown() throws IOException {
        HtmlElement rootElement = (HtmlElement) getRootElement();
        HtmlElement downButton = (HtmlElement) rootElement.getFirstChild().getNextSibling().getNextSibling();

        downButton.mouseDown();

        return this;
    }

    public SpinnerClient spinUp() throws IOException {
        HtmlElement rootElement = (HtmlElement) getRootElement();
        HtmlElement upButton = (HtmlElement) rootElement.getFirstChild().getNextSibling();

        upButton.mouseDown();

        return this;
    }

    public SpinnerClient spin(Integer value) {
        return this;
    }

    public String getValue() {
        return ((HtmlInput) getRootElement().getFirstChild()).getValueAttribute();
    }
    
    public void setValue(String value) {
        ((HtmlInput) getRootElement().getFirstChild()).setValueAttribute(value);
    }
}