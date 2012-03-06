package org.primefaces.jsfunit;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import java.io.IOException;
import org.jboss.jsfunit.jsfsession.JSFClientSession;

public class SpinnerClient extends PrimeFacesClient<SpinnerClient> {

    public SpinnerClient(JSFClientSession client) {
        super(client);
    }

    public SpinnerClient(JSFClientSession client, String componentId) {
        super(client, componentId);
    }

    public SpinnerClient spinDown() throws IOException {
        HtmlElement d = (HtmlElement) getElement();
        HtmlElement down = (HtmlElement) d.getFirstChild().getNextSibling().getNextSibling();

        down.mouseDown();

        return this;
    }

    public SpinnerClient spinUp() throws IOException {
        HtmlElement d = (HtmlElement) getElement();
        HtmlElement up = (HtmlElement) d.getFirstChild().getNextSibling();

        up.mouseDown();

        return this;
    }

    public SpinnerClient spin(Integer value) {

        return this;
    }

    public String getValue() {
        return ((HtmlInput) getElement().getFirstChild()).getValueAttribute();
    }
}
