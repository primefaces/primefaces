package org.primefaces.integrationtests.inputnumber;

import lombok.Data;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

@ViewScoped
@Named
@Data
public class InputNumber007 implements Serializable {
    private String otherValue;

    private Double value;

    public void setValue(Double value) {
        this.value = value;

        String message = value != null ? value.toString() : "null";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
    }
}
