/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.integrationtests.wizard;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Wizard001 implements Serializable {
    private boolean highlight;
    private String step;
    private boolean custom;
    private boolean required;
    private boolean status;
    private boolean blockNext;
    private boolean blockBack;
    private String effect;
    private String value;

    @PostConstruct
    public void init() {
        var parameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        highlight = Boolean.parseBoolean(parameters.get("highlight"));
        step = parameters.getOrDefault("start", "first");
        custom = Boolean.parseBoolean(parameters.get("custom"));
        required = Boolean.parseBoolean(parameters.get("required"));
        status = !"false".equals(parameters.get("status"));
        blockNext = Boolean.parseBoolean(parameters.get("blockNext"));
        blockBack = Boolean.parseBoolean(parameters.get("blockBack"));
        effect = parameters.get("effect");
        value = required ? null : "ok";
    }
}
