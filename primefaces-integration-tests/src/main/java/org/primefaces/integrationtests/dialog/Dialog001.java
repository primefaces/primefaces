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
package org.primefaces.integrationtests.dialog;

import lombok.Data;
import org.primefaces.event.CloseEvent;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;

@Named
@ViewScoped
@Data
public class Dialog001 implements Serializable {

    private static final long serialVersionUID = -7518459955779385834L;

    private String text1;
    private String text2;
    private LocalDate date;

    private DialogData dialogData;

    @PostConstruct
    public void init() {
        date = LocalDate.now();
        dialogData = new DialogData();
    }

    public void submit() {
        FacesMessage msg = new FacesMessage("Submit", "text2: " + this.text2);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void showDialog() {
        dialogData.setText(this.text2);
    }

    public void dialogOk() {
        this.text2 = dialogData.getText();

        FacesMessage msg = new FacesMessage("Dialog - OK", "text2: " + this.text2);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void handleClose(CloseEvent event) {
        FacesMessage msg = new FacesMessage("Dialog - close-event", "text2: " + this.dialogData.getText());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
