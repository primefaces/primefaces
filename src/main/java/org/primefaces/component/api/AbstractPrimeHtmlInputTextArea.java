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
package org.primefaces.component.api;

import java.util.Collection;
import java.util.List;

import javax.faces.component.html.HtmlInputTextarea;

import org.primefaces.util.LangUtils;

/**
 * Extended {@link HtmlInputTextarea} to allow for new events such as "input" and "paste".
 * Remove if JSF4.0 ever implements these events.
 */
public abstract class AbstractPrimeHtmlInputTextArea extends HtmlInputTextarea {

    // new HTML5 events
    public enum PropertyKeys {
        inputmode,
        oncut,
        oncopy,
        onpaste,
        onwheel,
        oncontextmenu,
        oninput,
        oninvalid,
        onreset,
        onsearch,
        ondrag,
        ondragend,
        ondragenter,
        ondragleave,
        ondragover,
        ondragstart,
        ondrop,
        onscroll
    }

    protected static final List<String> EVENT_NAMES = LangUtils.unmodifiableList(
                            "blur",
                            "change",
                            "valueChange",
                            "select",
                            "click",
                            "dblclick",
                            "focus",
                            "keydown",
                            "keypress",
                            "keyup",
                            "mousedown",
                            "mousemove",
                            "mouseout",
                            "mouseover",
                            "mouseup", // new events below here
                            "wheel",
                            "cut",
                            "copy",
                            "paste",
                            "contextmenu",
                            "input",
                            "invalid",
                            "reset",
                            "search",
                            "drag",
                            "dragend",
                            "dragenter",
                            "dragleave",
                            "dragover",
                            "dragstart",
                            "drop",
                            "scroll");

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public String getInputmode() {
        return (String) getStateHelper().eval(PropertyKeys.inputmode, null);
    }

    public void setInputmode(String inputmode) {
        getStateHelper().put(PropertyKeys.inputmode, inputmode);
    }

    public String getOncut() {
        return (String) getStateHelper().eval(PropertyKeys.oncut);
    }

    public void setOncut(String oncut) {
        getStateHelper().put(PropertyKeys.oncut, oncut);
    }

    public String getOncopy() {
        return (String) getStateHelper().eval(PropertyKeys.oncopy);
    }

    public void setOncopy(String oncopy) {
        getStateHelper().put(PropertyKeys.oncopy, oncopy);
    }

    public String getOnpaste() {
        return (String) getStateHelper().eval(PropertyKeys.onpaste);
    }

    public void setOnpaste(String onpaste) {
        getStateHelper().put(PropertyKeys.onpaste, onpaste);
    }

    public String getOnwheel() {
        return (String) getStateHelper().eval(PropertyKeys.onwheel);
    }

    public void setOnwheel(String onwheel) {
        getStateHelper().put(PropertyKeys.onwheel, onwheel);
    }

    public String getOncontextmenu() {
        return (String) getStateHelper().eval(PropertyKeys.oncontextmenu);
    }

    public void setOncontextmenu(String oncontextmenu) {
        getStateHelper().put(PropertyKeys.oncontextmenu, oncontextmenu);
    }

    public String getOninput() {
        return (String) getStateHelper().eval(PropertyKeys.oninput);
    }

    public void setOninput(String oninput) {
        getStateHelper().put(PropertyKeys.oninput, oninput);
    }

    public String getOninvalid() {
        return (String) getStateHelper().eval(PropertyKeys.oninvalid);
    }

    public void setOninvalid(String oninvalid) {
        getStateHelper().put(PropertyKeys.oninvalid, oninvalid);
    }

    public String getOnreset() {
        return (String) getStateHelper().eval(PropertyKeys.onreset);
    }

    public void setOnreset(String onreset) {
        getStateHelper().put(PropertyKeys.onreset, onreset);
    }

    public String getOnsearch() {
        return (String) getStateHelper().eval(PropertyKeys.onsearch);
    }

    public void setOnsearch(String onsearch) {
        getStateHelper().put(PropertyKeys.onsearch, onsearch);
    }

    public String getOndrag() {
        return (String) getStateHelper().eval(PropertyKeys.ondrag);
    }

    public void setOndrag(String ondrag) {
        getStateHelper().put(PropertyKeys.ondrag, ondrag);
    }

    public String getOndragend() {
        return (String) getStateHelper().eval(PropertyKeys.ondragend);
    }

    public void setOndragend(String ondragend) {
        getStateHelper().put(PropertyKeys.ondragend, ondragend);
    }

    public String getOndragenter() {
        return (String) getStateHelper().eval(PropertyKeys.ondragenter);
    }

    public void setOndragenter(String ondragenter) {
        getStateHelper().put(PropertyKeys.ondragenter, ondragenter);
    }

    public String getOndragleave() {
        return (String) getStateHelper().eval(PropertyKeys.ondragleave);
    }

    public void setOndragleave(String ondragleave) {
        getStateHelper().put(PropertyKeys.ondragleave, ondragleave);
    }

    public String getOndragover() {
        return (String) getStateHelper().eval(PropertyKeys.ondragover);
    }

    public void setOndragover(String ondragover) {
        getStateHelper().put(PropertyKeys.ondragover, ondragover);
    }

    public String getOndragstart() {
        return (String) getStateHelper().eval(PropertyKeys.ondragstart);
    }

    public void setOndragstart(String ondragstart) {
        getStateHelper().put(PropertyKeys.ondragstart, ondragstart);
    }

    public String getOndrop() {
        return (String) getStateHelper().eval(PropertyKeys.ondrop);
    }

    public void setOndrop(String ondrop) {
        getStateHelper().put(PropertyKeys.ondrop, ondrop);
    }

    public String getOnscroll() {
        return (String) getStateHelper().eval(PropertyKeys.onscroll);
    }

    public void setOnscroll(String onscroll) {
        getStateHelper().put(PropertyKeys.onscroll, onscroll);
    }
}
